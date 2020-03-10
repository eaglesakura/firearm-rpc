package com.eaglesakura.firearm.rpc.service.internal

import android.os.Bundle
import androidx.annotation.WorkerThread
import com.eaglesakura.armyknife.android.extensions.assertWorkerThread
import com.eaglesakura.firearm.aidl.IRemoteProcedureClient
import com.eaglesakura.firearm.aidl.IRemoteProcedureService
import com.eaglesakura.firearm.rpc.internal.blockingRunInWorker
import com.eaglesakura.firearm.rpc.internal.console
import com.eaglesakura.firearm.rpc.service.ProcedureServiceBinder
import com.eaglesakura.firearm.rpc.service.RemoteClient
import java.io.IOException
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

internal class RemoteProcedureServerBinderImpl(
    private val parent: ProcedureServiceBinder,
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
    @WorkerThread
    override fun register(aidl: IRemoteProcedureClient, options: Bundle): Bundle {
        assertWorkerThread()

        return lock.withLock {
            // make new client.
            val client = RemoteClient(parent, aidl)
            clients[client.id] = client
            val connectionHints = callback.onConnectedClient(client, options)
            console("Register client id[${client.id}] [$aidl]")
            return@withLock RegisterResult().also {
                it.connectionId = client.id
                it.connectionHings = connectionHints
            }.bundle
        }
    }

    /**
     * Request to server(from client).
     * run server task.
     */
    @WorkerThread
    override fun requestFromClient(clientId: String, arguments: Bundle): Bundle {
        assertWorkerThread()

        val remoteRequest = RemoteRequest(arguments)
        val client = lock.withLock {
            clients[clientId] ?: return Bundle() // or Void.
        }

        val result =
            blockingRunInWorker("Server:[${client.id}]->[Server]:${remoteRequest.path}") {
                callback.executeOnServer(
                    client,
                    remoteRequest.path,
                    remoteRequest.arguments!!
                )
            }
        return RemoteRequest.Result().also {
            it.result = result
        }.bundle
    }

    @WorkerThread
    override fun unregister(clientId: String): Bundle {
        assertWorkerThread()

        lock.withLock {
            clients.remove(clientId)?.also { client ->
                try {
                    client.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                callback.onDisconnectedClient(client)
            }
        }
        return Bundle() // Void.
    }
}