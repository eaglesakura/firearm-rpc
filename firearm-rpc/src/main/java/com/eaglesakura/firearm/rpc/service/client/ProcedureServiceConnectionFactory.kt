@file:Suppress("unused")

package com.eaglesakura.firearm.rpc.service.client

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eaglesakura.armyknife.runtime.extensions.withChildContext
import com.eaglesakura.firearm.rpc.service.ProcedureServiceConnection
import com.eaglesakura.firearm.rpc.service.internal.ProcedureServiceConnectionImpl
import com.eaglesakura.firearm.rpc.service.routers.RestfulClientProcedureRouter
import kotlinx.coroutines.Dispatchers

/**
 * Client to Server connection factory.
 *
 * e.g.)
 * val connection = ProcedureServiceConnectionFactory.connect(context, callback, intent) {
 *      // add options.
 * }
 *
 * connection.request("/", Bundle())    // request from client, to server.
 * connection.disconnect()
 */
@Suppress("MemberVisibilityCanBePrivate")
object ProcedureServiceConnectionFactory {

    class Builder(
        private val context: Context,

        private val serviceIntent: Intent,

        private val callback: ProcedureServiceClientCallback
    ) {

        /**
         * Optional.
         *
         * Connect connectionHings.
         */
        var options: Bundle? = null

        suspend fun connect(): ProcedureServiceConnection {
            return withChildContext(Dispatchers.Main) {
                val connection = ProcedureServiceConnectionImpl(
                        context,
                        serviceIntent,
                        callback
                )
                connection.connect(options ?: Bundle())
                connection
            }
        }
    }

    /**
     * Connect to server service.
     *
     * If service not created, then start service.
     * CAUTION!!, this connection is Not two-way rpc.
     */
    suspend fun connect(
        context: Context,
        serviceIntent: Intent,
        block: (builder: Builder) -> Unit = {}
    ): ProcedureServiceConnection {
        return connect(
                context,
                object : ProcedureServiceClientCallback {
                    override fun execute(
                        connection: ProcedureServiceConnection,
                        path: String,
                        arguments: Bundle
                    ): Bundle {
                        return Bundle()
                    }
                },
                serviceIntent, block
        )
    }

    /**
     * Connect to server service.
     *
     * If service not created, then start service.
     */
    suspend fun connect(
        context: Context,
        router: RestfulClientProcedureRouter,
        serviceIntent: Intent,
        block: (builder: Builder) -> Unit = {}
    ): ProcedureServiceConnection {
        return connect(
                context,
                object : ProcedureServiceClientCallback {
                    override fun execute(
                        connection: ProcedureServiceConnection,
                        path: String,
                        arguments: Bundle
                    ): Bundle {
                        return router(connection, path, arguments)
                    }
                },
                serviceIntent, block
        )
    }

    /**
     * Connect to server service.
     *
     * If service not created, then start service.
     */
    suspend fun connect(
        context: Context,
        callback: ProcedureServiceClientCallback,
        serviceIntent: Intent,
        block: (builder: Builder) -> Unit = {}
    ): ProcedureServiceConnection {
        val builder = Builder(context, serviceIntent, callback).also(block)
        return builder.connect()
    }
}