package com.eaglesakura.firearm.rpc.service.internal

import android.os.Bundle
import com.eaglesakura.armyknife.persistence.extensions.delegateBundleExtra
import com.eaglesakura.armyknife.persistence.extensions.delegateStringExtra

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
    var connectionId: String by bundle.delegateStringExtra("request.EXTRA_ID", "")

    /**
     * Options in register.
     */
    var connectionHings: Bundle? by bundle.delegateBundleExtra("request.EXTRA_OPTIONS")
}