package com.eaglesakura.firearm.rpc.internal

import java.util.UUID

internal object InternalUtils {

    /**
     * Returns random short strings(less than 10chars).
     *
     * @author @eaglesakura
     * @link https://github.com/eaglesakura/army-knife
     */
    @JvmStatic
    fun generateUniqueId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
}
