@file:Suppress("unused")

package com.eaglesakura.firearm.rpc.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.eaglesakura.armyknife.runtime.extensions.withChildContext
import com.eaglesakura.firearm.rpc.service.internal.ServerConnectionImpl
import com.eaglesakura.firearm.rpc.service.routers.ClientProcedureRouter
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
object ProcedureServerConnectionFactory {

    class Builder(
        private val context: Context,

        private val serviceIntent: Intent,

        private val callback: ProcedureClientCallback
    ) {

        /**
         * Optional.
         *
         * Connect connectionHings.
         */
        var options: Bundle? = null

        suspend fun connect(): ProcedureServerConnection {
            return withChildContext(Dispatchers.Main) {
                val connection = ServerConnectionImpl(
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
    ): ProcedureServerConnection {
        return connect(
            context,
            object : ProcedureClientCallback {
                override fun executeOnClient(
                    connection: ProcedureServerConnection,
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
        router: ClientProcedureRouter,
        serviceIntent: Intent,
        block: (builder: Builder) -> Unit = {}
    ): ProcedureServerConnection {
        return connect(
            context,
            object : ProcedureClientCallback {
                override fun executeOnClient(
                    connection: ProcedureServerConnection,
                    path: String,
                    arguments: Bundle
                ): Bundle {
                    return router.executeOnClient(connection, path, arguments)
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
        callback: ProcedureClientCallback,
        serviceIntent: Intent,
        block: (builder: Builder) -> Unit = {}
    ): ProcedureServerConnection {
        val builder = Builder(context, serviceIntent, callback).also(block)
        return builder.connect()
    }
}
