package com.eaglesakura.firearm.rpc.extensions

import com.eaglesakura.firearm.rpc.ProcedureConnection

/**
 * Connection use with disconnect.
 *
 * e.g.)
 *
 * val connection = ...
 * connection.use {
 *      require(connection is Remote)
 * }
 */
suspend fun <C : ProcedureConnection, T> C.use(block: (suspend (connection: C) -> T)) {
    try {
        block(this)
    } finally {
        disconnect()
    }
}