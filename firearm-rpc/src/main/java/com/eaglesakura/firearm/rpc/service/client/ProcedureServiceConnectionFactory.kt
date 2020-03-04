@file:Suppress("unused")

package com.eaglesakura.firearm.rpc.service.client

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.ProcedureServiceConnection
import com.eaglesakura.firearm.rpc.service.internal.ProcedureServiceConnectionImpl
import com.eaglesakura.firearm.rpc.service.routers.RestfulClientProcedureRouter
import kotlinx.coroutines.CoroutineDispatcher
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

        /**
         * Optional.
         * Coroutine dispatcher.
         */
        var coroutineDispatcher: CoroutineDispatcher = Dispatchers.Default

        suspend fun connect(): ProcedureServiceConnection {
            val connection =
                ProcedureServiceConnectionImpl(
                    context,
                    serviceIntent,
                    coroutineDispatcher,
                    callback
                )
            connection.connect(options ?: Bundle())
            return connection
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
                override suspend fun execute(
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
                override suspend fun execute(
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