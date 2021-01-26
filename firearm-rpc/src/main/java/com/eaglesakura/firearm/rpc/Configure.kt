package com.eaglesakura.firearm.rpc

import android.util.Log

object Configure {
    var log: (tag: String, msg: String) -> Unit = { tag, msg -> Log.d(tag, msg) }
}
