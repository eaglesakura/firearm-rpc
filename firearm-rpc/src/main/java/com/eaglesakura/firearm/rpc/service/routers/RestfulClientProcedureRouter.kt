package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection

class RestfulClientProcedureRouter {
    private val table = mutableMapOf<String, RestfulClientProcedure>()

    fun procedure(
        path: String,
        builder: (router: RestfulClientProcedureRouter, procedure: RestfulClientProcedure) -> Unit =
            { _, _ -> }
    ): RestfulClientProcedure {
        return RestfulClientProcedure(path)
            .also { proc ->
                builder(this@RestfulClientProcedureRouter, proc)
                table[path] = proc
            }
    }

    /**
     * Handler in client.
     */
    operator fun invoke(
        connection: ProcedureServerConnection,
        path: String,
        arguments: Bundle
    ): Bundle {
        val proc = table[path] ?: throw IllegalArgumentException("Path[$path] not match")
        return proc.listenInClient(connection, arguments)
    }
}