package com.eaglesakura.firearm.rpc.extensions

import com.eaglesakura.firearm.rpc.service.BroadcastResult

/**
 * Check broadcast completed.
 */
val List<BroadcastResult>.allSuccess: Boolean
    get() {
        this.forEach {
            if (it.failed) {
                return false
            }
        }
        return true
    }

/**
 * Listing all failed broadcast in this list.
 */
fun List<BroadcastResult>.listFailed(): List<BroadcastResult> {
    val result = mutableListOf<BroadcastResult>()
    this.forEach {
        if (it.failed) {
            result.add(it)
        }
    }
    return result.toList()
}
