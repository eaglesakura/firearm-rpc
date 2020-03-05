package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection

class RestfulClientProcedureRouter {
    private val table = mutableMapOf<String, RestfulClientProcedure<*, *>>()

    fun <Arguments, ProcedureResult> procedure(
        path: String,
        builder: (procedure: RestfulClientProcedure<Arguments, ProcedureResult>) -> Unit
    ): RestfulClientProcedure<Arguments, ProcedureResult> {
        return RestfulClientProcedure<Arguments, ProcedureResult>(path)
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
     * Handler in client.
     */
    operator fun invoke(
        connection: ProcedureServerConnection,
        path: String,
        arguments: Bundle
    ): Bundle {
        val proc = table[path] ?: throw IllegalArgumentException("Path[$path] not match")
        return proc.clientProcedure(connection, arguments)
    }
}