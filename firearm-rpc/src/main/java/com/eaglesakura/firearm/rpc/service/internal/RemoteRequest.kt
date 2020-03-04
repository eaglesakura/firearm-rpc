package com.eaglesakura.firearm.rpc.service.internal

import android.os.Bundle
import com.eaglesakura.firearm.rpc.internal.delegateBundleExtra
import com.eaglesakura.firearm.rpc.internal.delegateStringExtra

internal class RemoteRequest internal constructor(
    internal val bundle: Bundle = Bundle()
) {
    /**
     * Required.
     */
    var path: String by bundle.delegateStringExtra("request.EXTRA_PATH", "/")

    /**
     * Optional.
     * arguments of Request.
     */
    var arguments: Bundle? by bundle.delegateBundleExtra("request.EXTRA_ARGUMENTS")

    internal class Result(
        val bundle: Bundle = Bundle()
    ) {
        var result: Bundle? by bundle.delegateBundleExtra("request.result.EXTRA_RESULT")
    }
}