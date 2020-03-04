@file:Suppress("MemberVisibilityCanBePrivate")

package com.eaglesakura.firearm.rpc.service

/**
 * Service to all-client broadcast results.
 */
data class BroadcastResult<T>(
    val client: RemoteClient,
    val result: T?,
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