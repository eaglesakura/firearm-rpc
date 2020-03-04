package com.eaglesakura.firearm.rpc.extensions

import com.eaglesakura.firearm.rpc.service.BroadcastResult

/**
 * Check broadcast completed.
 */
val <T> List<BroadcastResult<T>>.allSuccess: Boolean
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
fun <T> List<BroadcastResult<T>>.listFailed(): List<BroadcastResult<T>> {
    val result = mutableListOf<BroadcastResult<T>>()
    this.forEach {
        if (it.failed) {
            result.add(it)
        }
    }
    return result.toList()
}