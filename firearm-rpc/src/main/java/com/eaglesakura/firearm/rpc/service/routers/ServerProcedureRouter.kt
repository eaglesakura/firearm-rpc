package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.RemoteClient

class ServerProcedureRouter {
    private val table = mutableMapOf<String, ServerProcedure>()

    /**
     * Add API Handler for Server.
     */
    fun handler(
        path: String,
        builder: (procedure: ServerProcedure) -> Unit = {}
    ):
        ServerProcedure {
            return ServerProcedure(path)
                .also { proc ->
                    builder(proc)
                    table[path] = proc
                }
        }

    /**
     * Handler in server.
     */
    fun executeOnServer(
        client: RemoteClient,
        path: String,
        arguments: Bundle
    ): Bundle {
        val proc = table[path] ?: throw IllegalArgumentException("Path[$path] not match")
        return proc.listenOnServer(client, arguments)
    }
}
