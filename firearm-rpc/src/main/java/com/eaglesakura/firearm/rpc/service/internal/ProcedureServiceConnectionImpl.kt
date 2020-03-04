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
import com.eaglesakura.firearm.rpc.service.ProcedureServiceConnection
import com.eaglesakura.firearm.rpc.service.client.ProcedureServiceClientCallback
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import kotlin.concurrent.thread
import kotlin.coroutines.coroutineContext

internal class ProcedureServiceConnectionImpl(
    private val context: Context,
    /**
 * Intent for connect to service.
 */
private val intent: Intent,

    /**
 * Background dispatcher.
 */
private val coroutineDispatcher: CoroutineDispatcher,

    private val callback: ProcedureServiceClientCallback

) : IRemoteProcedureClient.Stub(), ServiceConnection, ProcedureConnection,
    ProcedureServiceConnection {

    private var name: ComponentName? = null

    private var aidl: IRemoteProcedureService? = null

    private var _clientId: String? = null

    private var _connectionHints: Bundle? = null

    override val clientId: String
        get() = _clientId!!

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
                    this._clientId = it.clientId
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

    override suspend fun disconnect() {
        aidl?.also {
            it.unregister(clientId)
        }

        context.unbindService(this)
        name = null
        aidl = null
        _clientId = null
        _connectionHints = null
    }

    /**
     * Request to server(from client).
     * run server task.
     */
    override suspend fun request(path: String, arguments: Bundle): Bundle {
        val aidl = this.aidl ?: throw IllegalStateException("Server not connected[$intent]")
        // call remote task.
        val channel = Channel<Bundle>()
        thread(name = "Remote:Server:$path") {
            try {
                val result = aidl.requestFromClient(clientId, RemoteRequest().also {
                    it.path = path
                    it.arguments = arguments
                }.bundle)!!
                GlobalScope.launch(coroutineDispatcher) {
                    channel.send(result)
                }
            } catch (e: Exception) {
                channel.close(e)
            }
        }
        return RemoteRequest.Result(channel.receive()).result!!
    }

    /**
     * Request from server.
     * run client task.
     */
    override fun requestFromService(arguments: Bundle): Bundle {
        val request = RemoteRequest(arguments)
        val result = runBlocking(coroutineDispatcher) {
            // call client task.
            callback.execute(
                this@ProcedureServiceConnectionImpl,
                request.path,
                request.arguments!!
            )
        }

        return RemoteRequest.Result().also {
            it.result = result
        }.bundle
    }
}