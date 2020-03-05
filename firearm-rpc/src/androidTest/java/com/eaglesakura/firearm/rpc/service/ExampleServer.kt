package com.eaglesakura.firearm.rpc.service

import com.eaglesakura.firearm.rpc.service.routers.ServerProcedureRouter

/**
 * Client to server api.
 * Call from client.
 */
class ExampleServer {
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
