package com.eaglesakura.firearm.rpc.service

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
    val ping = router.procedure("/ping")
}