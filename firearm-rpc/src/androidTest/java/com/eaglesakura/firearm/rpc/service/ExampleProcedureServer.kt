package com.eaglesakura.firearm.rpc.service

import android.os.Bundle
import androidx.core.os.bundleOf
import com.eaglesakura.firearm.rpc.service.routers.RestfulServiceProcedureRouter

/**
 * Client to server api.
 * Call from client.
 */
class ExampleProcedureServer {
    val router = RestfulServiceProcedureRouter()

    /**
     * Echo request.
     */
    val echo = router.procedure<EchoArguments, VoidBundle>("/echo") { proc ->
        proc.argumentsToBundle = { it.bundle }
        proc.bundleToArguments = { EchoArguments(it) }
        proc.resultToBundle = { Bundle() }
        proc.bundleToResult = { VoidBundle() }
    }

    /**
     * Hello Request
     */
    val hello = router.procedure<HelloArguments, VoidBundle>("/") { proc ->
        proc.argumentsToBundle = { bundleOf(Pair("Hello", it.message)) }
        proc.bundleToArguments = { HelloArguments(it.getString("Hello")!!) }
        proc.resultToBundle = { Bundle() }
        proc.bundleToResult = { VoidBundle() }
    }

    class EchoArguments(val bundle: Bundle)
    class HelloArguments(val message: String)
    class VoidBundle
}