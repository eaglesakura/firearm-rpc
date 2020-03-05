package com.eaglesakura.firearm.rpc.service

import com.eaglesakura.firearm.rpc.service.routers.ClientProcedureRouter

/**
 * Server to client api.
 * Call from server.
 */
class ExampleClient {
    val router = ClientProcedureRouter()

    /**
     * Ping to client.
     */
    val ping = router.handler("/ping")
}