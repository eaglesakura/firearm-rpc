package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.RemoteClient

class RestfulServiceProcedureRouter {
    private val table = mutableMapOf<String, RestfulServiceProcedure>()

    fun procedure(
        path: String,
        builder: (procedure: RestfulServiceProcedure) -> Unit = {}
    ):
            RestfulServiceProcedure {
        return RestfulServiceProcedure(path)
            .also { proc ->
                builder(proc)
                table[path] = proc
            }
    }

    /**
     * Handler in server.
     */
    operator fun invoke(
        client: RemoteClient,
        path: String,
        arguments: Bundle
    ): Bundle {
        val proc = table[path] ?: throw IllegalArgumentException("Path[$path] not match")
        return proc.listenInServer(client, arguments)
    }
}