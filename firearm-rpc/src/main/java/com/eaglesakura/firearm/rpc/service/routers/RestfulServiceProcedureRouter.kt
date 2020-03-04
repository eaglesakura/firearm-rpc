package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.RemoteClient

class RestfulServiceProcedureRouter {
    private val table = mutableMapOf<String, RestfulServiceProcedure<*, *>>()

    fun <Arguments, ProcedureResult> procedure(
        path: String,
        builder: (procedure: RestfulServiceProcedure<Arguments, ProcedureResult>) -> Unit
    ):
            RestfulServiceProcedure<Arguments, ProcedureResult> {
        return RestfulServiceProcedure<Arguments, ProcedureResult>(path)
            .also { proc ->
                builder(proc)
                require(proc.argumentsToBundle == proc.argumentsToBundle)
                require(proc.bundleToArguments == proc.bundleToArguments)
                require(proc.resultToBundle == proc.resultToBundle)
                require(proc.bundleToResult == proc.bundleToResult)
                table[path] = proc
            }
    }

    /**
     * Handler in server.
     */
    suspend operator fun invoke(
        client: RemoteClient,
        path: String,
        arguments: Bundle
    ): Bundle {
        val proc = table[path] ?: throw IllegalArgumentException("Path[$path] not match")
        return proc.serverProcedure(client, arguments)
    }
}