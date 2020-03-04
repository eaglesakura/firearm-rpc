package com.eaglesakura.firearm.rpc.service.client

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.ProcedureServiceConnection

/**
 * Implementation the callback to client.
 */
interface ProcedureServiceClientCallback {
    /**
     * Do something in your task.
     *
     * Call from Server, run in Client.
     */
    suspend fun execute(
        /**
         * Sender connection.
         */
        connection: ProcedureServiceConnection,

        /**
         * Request path in client.
         */
        path: String,

        /**
         * arguments.
         */
        arguments: Bundle
    ): Bundle
}