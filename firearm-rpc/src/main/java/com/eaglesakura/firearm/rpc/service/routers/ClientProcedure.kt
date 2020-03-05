package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.BroadcastResult
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection
import com.eaglesakura.firearm.rpc.service.ProcedureServiceBinder
import com.eaglesakura.firearm.rpc.service.RemoteClient

/**
 * Procedure call from service, run in client.
 */
class ClientProcedure(
    /**
     * Procedure path.
     */
    val path: String
) {
    /**
     * Implementation stub for Client.
     */
    lateinit var listenOnClient: (connection: ProcedureServerConnection, arguments: Bundle) -> Bundle

    /**
     * Request service to client.
     * Execute in client.
     */
    fun fetch(
        client: RemoteClient,
        arguments: Bundle
    ): Bundle {
        return client.executeOnClient(path, arguments)
    }

    /**
     * Request service to all clients.
     * Execute in all clients.
     */
    fun fetch(
        binder: ProcedureServiceBinder,
        arguments: Bundle
    ): List<BroadcastResult> {
        val result = mutableListOf<BroadcastResult>()
        for (client in binder.allClients) {
            try {
                result.add(BroadcastResult(client, fetch(client, arguments), null))
            } catch (e: Exception) {
                result.add(BroadcastResult(client, null, e))
            }
        }
        return result.toList()
    }
}