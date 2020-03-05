package com.eaglesakura.firearm.rpc.service

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleOwner
import com.eaglesakura.firearm.rpc.internal.console
import com.eaglesakura.firearm.rpc.service.internal.IRemoteProcedureServiceImpl
import com.eaglesakura.firearm.rpc.service.internal.RemoteRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlin.concurrent.thread

/**
 * Remote Procedure server instance.
 *
 * e.g.)
 * class FooService : LifecycleService(), ProcedureServiceBinder.Callback  {
 *      private val serverService = ProcedureServiceBinder(this, this, Dispatchers.Default)
 *
 *      override fun onBind(intent: Intent?): IBinder? {
 *          return serverService.binder
 *      }
 * }
 */
class ProcedureServiceBinder(
    @Suppress("UNUSED_PARAMETER") context: Context,
    callback: Callback,
    private val lifecycleOwner: LifecycleOwner,
    private val dispatcher: CoroutineDispatcher
) {
    /**
     *
     */
    private val aidlImpl = IRemoteProcedureServiceImpl(
        this,
        lifecycleOwner,
        dispatcher,
        callback
    )

    /**
     * Public interface for Service.
     */
    val binder: IBinder
        get() = aidlImpl

    /**
     * Get all clients list.
     * this property is thread-safe, and returns copied list.
     */
    val allClients: List<RemoteClient>
        get() = aidlImpl.allClients

    /**
     * Execute in remote client.
     */
    suspend fun request(client: RemoteClient, path: String, arguments: Bundle): Bundle {
        val channel = Channel<Bundle>()
        thread(name = "Remote:${client.id}:$path") {
            try {
                val request = RemoteRequest().also {
                    it.path = path
                    it.arguments = arguments
                }.bundle
                val result = client.aidl.requestFromService(request)!!
                GlobalScope.launch(dispatcher) {
                    channel.send(result)
                }
            } catch (e: Exception) {
                channel.close(e)
            }
        }
        return RemoteRequest.Result(channel.receive()).result!!
    }

    /**
     * Execute all remote client.
     */
    suspend fun broadcast(path: String, arguments: Bundle): List<BroadcastResult<Bundle>> {
        val clients = aidlImpl.allClients
        val result = mutableListOf<BroadcastResult<Bundle>>()
        for (client in clients) {
            try {
                result.add(
                    BroadcastResult(
                        client,
                        client.request(path, arguments),
                        null
                    )
                )
            } catch (e: Exception) {
                result.add(BroadcastResult(client, null, e))
                console("Broadcast failed client[${client.id}]")
            }
        }
        return result
    }

    interface Callback {
        /**
         * new connection from client.
         */
        @WorkerThread
        fun onConnectedClient(client: RemoteClient, options: Bundle): Bundle

        /**
         * kill connection from client.
         */
        @WorkerThread
        fun onDisconnectedClient(client: RemoteClient)

        /**
         * Do something in your task.
         * Call from client, run in service.
         *
         * @param client request sender.
         * @param path rest path
         * @param arguments optional, arguments for rest path.
         */
        @WorkerThread
        fun execute(client: RemoteClient, path: String, arguments: Bundle): Bundle
    }
}