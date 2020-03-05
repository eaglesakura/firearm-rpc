package com.eaglesakura.firearm.rpc.service

import android.os.Bundle
import androidx.annotation.WorkerThread

/**
 * Implementation the callback to client.
 */
interface ProcedureClientCallback {
    /**
     * Do something in your task.
     *
     * Call from Server, run in Client.
     */
    @WorkerThread
    fun executeOnClient(
        /**
         * Sender connection.
         */
        connection: ProcedureServerConnection,

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