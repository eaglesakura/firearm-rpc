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
suspend fun <T> ProcedureConnection.use(block: (suspend (connection: ProcedureConnection) -> T)) {
    try {
        block(this)
    } finally {
        disconnect()
    }
}