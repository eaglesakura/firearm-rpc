package com.eaglesakura.firearm.rpc.service

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
    val echo = router.procedure("/echo")
    /**
     * Hello Request
     */
    val hello = router.procedure("/")
}