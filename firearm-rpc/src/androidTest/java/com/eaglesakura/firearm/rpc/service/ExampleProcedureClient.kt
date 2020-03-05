package com.eaglesakura.firearm.rpc.service

import com.eaglesakura.firearm.rpc.service.routers.ClientProcedureRouter

/**
 * Server to client api.
 * Call from server.
 */
object ExampleProcedureClient {
    val router = ClientProcedureRouter()

    /**
     * Ping to client.
     */
    val ping = router.handler("/ping")
}