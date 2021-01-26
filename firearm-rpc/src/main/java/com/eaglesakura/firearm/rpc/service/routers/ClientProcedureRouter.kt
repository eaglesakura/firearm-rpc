package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection

class ClientProcedureRouter {
    private val table = mutableMapOf<String, ClientProcedure>()

    /**
     * Add API Handler for Client.
     */
    fun handler(
        path: String,
        builder: (router: ClientProcedureRouter, procedure: ClientProcedure) -> Unit =
            { _, _ -> }
    ): ClientProcedure {
        return ClientProcedure(path)
            .also { proc ->
                builder(this@ClientProcedureRouter, proc)
                table[path] = proc
            }
    }

    /**
     * Handler in client.
     */
    fun executeOnClient(
        connection: ProcedureServerConnection,
        path: String,
        arguments: Bundle
    ): Bundle {
        val proc = table[path] ?: throw IllegalArgumentException("Path[$path] not match")
        return proc.listenOnClient(connection, arguments)
    }
}
