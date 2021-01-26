package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection
import com.eaglesakura.firearm.rpc.service.RemoteClient

/**
 * Procedure call from client, run in service.
 */
class ServerProcedure(
    /**
     * Procedure path.
     */
    val path: String
) {
    /**
     * Implementation stub for Server.
     */
    lateinit var listenOnServer: (client: RemoteClient, arguments: Bundle) -> Bundle

    /**
     * Request client to server.
     * Execute in server.
     */
    fun fetch(
        connection: ProcedureServerConnection,
        arguments: Bundle
    ): Bundle {
        return connection.executeOnServer(path, arguments)
    }
}
