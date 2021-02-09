package com.eaglesakura.firearm.rpc.service.internal

import android.os.Bundle

internal class RemoteRequest internal constructor(
    internal val bundle: Bundle = Bundle()
) {
    /**
     * Required.
     */
    var path: String
        get() = bundle.getString("request.EXTRA_PATH") ?: "/"
        set(value) = bundle.putString("request.EXTRA_PATH", value)

    /**
     * Optional.
     * arguments of Request.
     */
    var arguments: Bundle?
        get() = bundle.getBundle("request.EXTRA_ARGUMENTS")
        set(value) = bundle.putBundle("request.EXTRA_ARGUMENTS", value)

    internal class Result(
        val bundle: Bundle = Bundle()
    ) {
        var result: Bundle?
            get() = bundle.getBundle("request.result.EXTRA_RESULT")
            set(value) = bundle.putBundle("request.result.EXTRA_RESULT", value)
    }
}
