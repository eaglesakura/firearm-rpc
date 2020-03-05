package com.eaglesakura.firearm.rpc.service

import android.os.Bundle
import androidx.annotation.WorkerThread
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
interface ProcedureServerConnection : ProcedureConnection {
    /**
     * Identifier of connection.
     */
    val connectionId: String

    /**
     * Server connectionHings at registered.
     */
    val connectionHints: Bundle

    /**
     * Execute in remote clientProcedure.
     */
    @WorkerThread
    fun executeOnServer(path: String, arguments: Bundle): Bundle
}