package com.eaglesakura.firearm.rpc.service.internal

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.UiThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.eaglesakura.armyknife.android.extensions.UIHandler
import com.eaglesakura.armyknife.android.extensions.assertUIThread
import com.eaglesakura.armyknife.android.extensions.assertWorkerThread
import com.eaglesakura.armyknife.android.extensions.postOrRun
import com.eaglesakura.armyknife.android.extensions.runBlockingOnUiThread
import com.eaglesakura.firearm.aidl.IRemoteProcedureClient
import com.eaglesakura.firearm.aidl.IRemoteProcedureService
import com.eaglesakura.firearm.rpc.ProcedureConnection
import com.eaglesakura.firearm.rpc.internal.blockingRunInWorker
import com.eaglesakura.firearm.rpc.service.ProcedureClientCallback
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield

internal class ServerConnectionImpl(
    private val context: Context,
    /**
     * Intent for connect to service.
     */
    private val intent: Intent,

    private val callback: ProcedureClientCallback
) : IRemoteProcedureClient.Stub(),
    ServiceConnection,
    ProcedureConnection,
    ProcedureServerConnection {

    private var name: ComponentName? = null

    private var aidl: IRemoteProcedureService? = null

    private var _connectionId: String? = null

    private var _connectionHints: Bundle? = null

    override val connectionId: String
        get() = _connectionId!!

    override val connectionHints: Bundle
        get() = _connectionHints!!

    private val registry = LifecycleRegistry(this)

    init {
        UIHandler.postOrRun {
            registry.currentState = Lifecycle.State.CREATED
        }
    }

    /**
     * Connect remote service.
     */
    @UiThread
    internal suspend fun connect(options: Bundle) {
        assertUIThread()
        context.bindService(intent, this@ServerConnectionImpl, BIND_AUTO_CREATE)
        while (coroutineContext.isActive) {
            if (aidl != null) {
                val result = withContext(Dispatchers.Default) {
                    aidl!!.register(this@ServerConnectionImpl, options)!!
                }
                RegisterResult(result).also {
                    this._connectionId = it.connectionId
                    this._connectionHints = it.connectionHings!!
                    this.registry.currentState = Lifecycle.State.RESUMED
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

    @UiThread
    override fun disconnect() {
        assertWorkerThread()
        aidl?.also {
            it.unregister(connectionId)
        }

        runBlockingOnUiThread {
            context.unbindService(this)
            name = null
            aidl = null
            _connectionId = null
            _connectionHints = null
            registry.currentState = Lifecycle.State.DESTROYED
        }
    }

    /**
     * Request to server(from client).
     * run server task.
     */
    override fun executeOnServer(path: String, arguments: Bundle): Bundle {
        val aidl = this.aidl ?: throw IllegalStateException("Server not connected[$intent]")
        // call remote task.
        val result = blockingRunInWorker("Client:[$connectionId]->[Server]:$path") {
            aidl.requestFromClient(
                connectionId,
                RemoteRequest().also {
                    it.path = path
                    it.arguments = arguments
                }.bundle
            )!!
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
        val result =
            blockingRunInWorker("Client:[Server]->[$connectionId]:${request.path}") {
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

    override fun getLifecycle(): Lifecycle = registry
}
