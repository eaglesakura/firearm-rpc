package com.eaglesakura.firearm.rpc.service.internal

import android.os.Bundle
import com.eaglesakura.firearm.aidl.IRemoteProcedureClient
import com.eaglesakura.firearm.aidl.IRemoteProcedureService
import com.eaglesakura.firearm.rpc.internal.console
import com.eaglesakura.firearm.rpc.service.ProcedureServiceBinder
import com.eaglesakura.firearm.rpc.service.RemoteClient
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class IRemoteProcedureServiceImpl(
    private val parent: ProcedureServiceBinder,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val callback: ProcedureServiceBinder.Callback
) : IRemoteProcedureService.Stub() {
    private val clients: MutableMap<String, RemoteClient> = mutableMapOf()

    private val lock = ReentrantLock()

    /**
     * Get all clients list.
     * this property is thread-safe, and returns copied list.
     */
    val allClients: List<RemoteClient>
        get() {
            lock.withLock {
                return clients.values.toList()
            }
        }

    /**
     * Register client
     */
    override fun register(aidl: IRemoteProcedureClient, options: Bundle): Bundle {
        return lock.withLock {
            // make new client.
            val client = RemoteClient(parent, aidl)
            clients[client.id] = client

            val connectionHints = runBlocking(Dispatchers.Main) {
                callback.onConnectedClient(client, options)
            }

            console("Register client id[${client.id}] [$aidl]")

            return@withLock RegisterResult().also {
                it.clientId = client.id
                it.connectionHings = connectionHints
            }.bundle
        }
    }

    /**
     * Request to server(from client).
     * run server task.
     */
    override fun requestFromClient(clientId: String, arguments: Bundle): Bundle {
        val remoteRequest = RemoteRequest(arguments)
        val client = lock.withLock {
            clients[clientId] ?: return Bundle() // or Void.
        }

        console("requestFromClient from[${client.id}] [$client]")

        val result = runBlocking(coroutineDispatcher) {
            return@runBlocking callback.execute(
                    client,
                    remoteRequest.path,
                    remoteRequest.arguments!!
            )
        }
        return RemoteRequest.Result().also {
            it.result = result
        }.bundle
    }

    override fun unregister(clientId: String): Bundle {
        lock.withLock {
            clients.remove(clientId)?.also { client ->
                GlobalScope.launch(Dispatchers.Main) {
                    callback.onDisconnectedClient(client)
                }
            }
        }
        return Bundle() // Void.
    }
}