package com.eaglesakura.firearm.rpc.service.internal

import android.os.Bundle
import com.eaglesakura.firearm.rpc.internal.delegateBundleExtra
import com.eaglesakura.firearm.rpc.internal.delegateStringExtra

/**
 * Result at IRemoteProcedureServer.register()
 *
 * @see IRemoteProcedureServiceImpl
 */
internal class RegisterResult internal constructor(
    internal val bundle: Bundle = Bundle()
) {
    /**
     * Unique id of your client.
     */
    var clientId: String by bundle.delegateStringExtra("request.EXTRA_ID", "")

    /**
     * Options in register.
     */
    var connectionHings: Bundle? by bundle.delegateBundleExtra("request.EXTRA_OPTIONS")
}