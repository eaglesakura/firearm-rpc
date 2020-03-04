package com.eaglesakura.firearm.rpc.internal

import com.eaglesakura.firearm.rpc.Configure

internal fun Any.console(msg: String) {
    Configure.log(javaClass.simpleName, msg)
}