package com.eaglesakura.firearm.rpc.service.internal

import android.os.Bundle

/**
 * Result at IRemoteProcedureServer.register()
 *
 * @see RemoteProcedureServerBinderImpl
 */
internal class RegisterResult internal constructor(
    internal val bundle: Bundle = Bundle()
) {
    /**
     * Unique id of your client.
     */
    var connectionId: String
        get() = bundle.getString("request.EXTRA_ID") ?: ""
        set(value) = bundle.putString("request.EXTRA_ID", value)

    /**
     * Options in register.
     */
    var connectionHings: Bundle?
        get() = bundle.getBundle("request.EXTRA_OPTIONS")
        set(value) = bundle.putBundle("request.EXTRA_OPTIONS", value)
}
