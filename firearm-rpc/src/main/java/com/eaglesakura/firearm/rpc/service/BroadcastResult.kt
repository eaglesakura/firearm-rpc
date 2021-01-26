@file:Suppress("MemberVisibilityCanBePrivate")

package com.eaglesakura.firearm.rpc.service

import android.os.Bundle

/**
 * Service to all-client broadcast results.
 */
data class BroadcastResult(
    val client: RemoteClient,
    val result: Bundle?,
    val error: Exception?
) {
    /**
     * Check success.
     */
    val success: Boolean
        get() = error != null

    /**
     * Check failed.
     */
    val failed: Boolean
        get() = !success
}
