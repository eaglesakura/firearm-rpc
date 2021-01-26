package com.eaglesakura.firearm.rpc.extensions

import com.eaglesakura.firearm.rpc.ProcedureConnection

/**
 * Connection use with disconnect.
 *
 * e.g.)
 *
 * val connection = ...
 * connection.use {
 *      // execute connection.
 * }
 */
suspend fun <C : ProcedureConnection, T> C.use(block: (suspend (connection: C) -> T)): T {
    return try {
        block(this)
    } finally {
        disconnect()
    }
}
