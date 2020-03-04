package com.eaglesakura.firearm.rpc

/**
 * RPC interface(for Client).
 */
interface ProcedureConnection {
    /**
     * Disconnect to remote procedure.
     */
    suspend fun disconnect()
}