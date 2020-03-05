package com.eaglesakura.firearm.rpc.service.internal

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import com.eaglesakura.firearm.aidl.IRemoteProcedureClient
import com.eaglesakura.firearm.aidl.IRemoteProcedureService
import com.eaglesakura.firearm.rpc.ProcedureConnection
import com.eaglesakura.firearm.rpc.internal.blockingRunInWorker
import com.eaglesakura.firearm.rpc.service.ProcedureClientCallback
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.yield
import kotlin.coroutines.coroutineContext

internal class ServerConnectionImpl(
    private val context: Context,
    /**
     * Intent for connect to service.
     */
    private val intent: Intent,

    private val callback: ProcedureClientCallback
) : IRemoteProcedureClient.Stub(), ServiceConnection, ProcedureConnection,
        ProcedureServerConnection {

    private var name: ComponentName? = null

    private var aidl: IRemoteProcedureService? = null

    private var _connectionId: String? = null

    private var _connectionHints: Bundle? = null

    override val connectionId: String
        get() = _connectionId!!

    override val connectionHints: Bundle
        get() = _connectionHints!!

    /**
     * Connect remote service.
     */
    internal suspend fun connect(options: Bundle) {
        context.bindService(intent, this, BIND_AUTO_CREATE)
        while (coroutineContext.isActive) {
            if (aidl != null) {
                val result = aidl!!.register(this, options)!!
                RegisterResult(result).also {
                    this._connectionId = it.connectionId
                    this._connectionHints = it.connectionHings!!
                }
                return
            }
            delay(1)
        }

        yield()
        throw IllegalStateException("Connection failed")
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        this.name = name
        this.aidl = IRemoteProcedureService.Stub.asInterface(service)
    }

    /**
     * Clash in remote service.
     */
    override fun onServiceDisconnected(name: ComponentName?) {
        // TODO: on Crash remote process.
    }

    override fun disconnect() {
        aidl?.also {
            it.unregister(connectionId)
        }

        context.unbindService(this)
        name = null
        aidl = null
        _connectionId = null
        _connectionHints = null
    }

    /**
     * Request to server(from client).
     * run server task.
     */
    override fun executeOnServer(path: String, arguments: Bundle): Bundle {
        val aidl = this.aidl ?: throw IllegalStateException("Server not connected[$intent]")
        // call remote task.
        val result = blockingRunInWorker("rpc-from[$connectionId]-to-[Server]:$path") {
            aidl.requestFromClient(connectionId, RemoteRequest().also {
                it.path = path
                it.arguments = arguments
            }.bundle)!!
        }
        return RemoteRequest.Result(result).result!!
    }

    /**
     * Request from server.
     * run client task.
     */
    override fun requestFromService(arguments: Bundle): Bundle {
        val request = RemoteRequest(arguments)
        // call client task.
        val result = blockingRunInWorker("rpc-from[Server]-to-[$connectionId]:${request.path}") {
            callback.executeOnClient(
                    this@ServerConnectionImpl,
                    request.path,
                    request.arguments!!
            )
        }

        return RemoteRequest.Result().also {
            it.result = result
        }.bundle
    }
}