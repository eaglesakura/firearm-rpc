package com.eaglesakura.firearm.rpc.service

import com.eaglesakura.firearm.rpc.service.routers.ServerProcedureRouter

/**
 * Client to server api.
 * Call from client.
 */
object ExampleProcedureServer {
    val router = ServerProcedureRouter()

    /**
     * Echo request.
     */
    val echo = router.handler("/echo")

    /**
     * Hello Request
     */
    val hello = router.handler("/")
}
