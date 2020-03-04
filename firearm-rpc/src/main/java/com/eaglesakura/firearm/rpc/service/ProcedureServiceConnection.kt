package com.eaglesakura.firearm.rpc.service

import android.os.Bundle
import com.eaglesakura.firearm.rpc.ProcedureConnection

/**
 * ProcedureConnection for ProcedureServiceBinder.
 *
 * this object includes AIDL.
 *
 * e.g.)
 *
 *  val connection =  ProcedureServiceConnectionFactory.connect( /* arguments... */ )
 *  connection.request("/path/to/proc", Bundle()) // request to service.
 *  connection.disconnect() // unbind service.
 *
 * @see com.eaglesakura.firearm.rpc.service.ProcedureServiceBinder
 */
interface ProcedureServiceConnection : ProcedureConnection {
    /**
     * Identifier of connection.
     */
    val clientId: String

    /**
     * Server connectionHings at registered.
     */
    val connectionHints: Bundle

    /**
     * Execute in remote clientProcedure.
     */
    suspend fun request(path: String, arguments: Bundle): Bundle
}