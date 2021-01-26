package com.eaglesakura.firearm.rpc.service

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.WorkerThread
import com.eaglesakura.armyknife.android.extensions.assertWorkerThread
import com.eaglesakura.firearm.rpc.internal.blockingRunInWorker
import com.eaglesakura.firearm.rpc.internal.console
import com.eaglesakura.firearm.rpc.service.internal.RemoteProcedureServerBinderImpl
import com.eaglesakura.firearm.rpc.service.internal.RemoteRequest

/**
 * Remote Procedure server instance.
 *
 * e.g.)
 * class FooService : LifecycleService(), ProcedureServiceBinder.Callback  {
 *      private val serverService = ProcedureServiceBinder(this, this as ProcedureServiceBinder.Callback)
 *
 *      override fun onBind(intent: Intent?): IBinder? {
 *          return serverService.binder
 *      }
 * }
 */
class ProcedureServiceBinder(
    @Suppress("UNUSED_PARAMETER") context: Context,
    callback: Callback
) {
    /**
     *
     */
    private val aidlImpl = RemoteProcedureServerBinderImpl(
        this,
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
    @WorkerThread
    fun requestToClient(client: RemoteClient, path: String, arguments: Bundle): Bundle {
        assertWorkerThread()
        val request = RemoteRequest().also {
            it.path = path
            it.arguments = arguments
        }.bundle
        val result = blockingRunInWorker("Server:[Server]->[${client.id}]:$path") {
            client.aidl.requestFromService(request)!!
        }
        return RemoteRequest.Result(result).result!!
    }

    /**
     * Execute all remote client.
     */
    @WorkerThread
    fun broadcastToClients(path: String, arguments: Bundle): List<BroadcastResult> {
        assertWorkerThread()
        val clients = aidlImpl.allClients.toList() // copy snapshot.
        val result = mutableListOf<BroadcastResult>()
        for (client in clients) {
            try {
                result.add(
                    BroadcastResult(
                        client,
                        client.executeOnClient(path, arguments),
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
        fun executeOnServer(client: RemoteClient, path: String, arguments: Bundle): Bundle
    }
}
