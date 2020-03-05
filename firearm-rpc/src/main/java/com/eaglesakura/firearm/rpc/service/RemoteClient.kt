package com.eaglesakura.firearm.rpc.service

import android.os.Bundle
import com.eaglesakura.firearm.aidl.IRemoteProcedureClient
import com.eaglesakura.firearm.rpc.internal.InternalUtils

/**
 * Client interface in Server process.
 */
class RemoteClient internal constructor(
    private val parent: ProcedureServiceBinder,
    internal val aidl: IRemoteProcedureClient
) {
    /**
     * Unique id of Client.
     */
    val id: String = InternalUtils.generateUniqueId()

    /**
     * Request to client(from server).
     * run client task.
     */
    fun executeOnClient(path: String, arguments: Bundle): Bundle {
        return parent.requestToClient(this, path, arguments)
    }
}