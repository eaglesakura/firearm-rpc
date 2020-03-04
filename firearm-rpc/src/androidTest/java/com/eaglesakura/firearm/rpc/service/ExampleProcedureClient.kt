package com.eaglesakura.firearm.rpc.service

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.routers.RestfulClientProcedureRouter

/**
 * Server to client api.
 * Call from server.
 */
class ExampleProcedureClient {
    val router = RestfulClientProcedureRouter()

    /**
     * Ping to client.
     */
    val ping =
        router.procedure<ExampleProcedureServer.VoidBundle, ExampleProcedureServer.VoidBundle>("/ping") { proc ->
            proc.argumentsToBundle = { Bundle() }
            proc.bundleToArguments = { ExampleProcedureServer.VoidBundle() }
            proc.resultToBundle = { Bundle() }
            proc.bundleToResult = { ExampleProcedureServer.VoidBundle() }
        }
}