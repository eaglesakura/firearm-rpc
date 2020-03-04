package com.eaglesakura.firearm.rpc.service.internal

import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteRequestTest {

    @Test
    fun writeToBundle() {
        val request = RemoteRequest()
        request.path = "/test"
        request.arguments = bundleOf(
            Pair("Hello", "World")
        )

        assertEquals("/test", request.path)
        assertEquals("World", request.arguments!!.getString("Hello"))
        assertEquals("/test", request.bundle.getString("request.EXTRA_PATH"))
        assertEquals(
            "World",
            request.bundle.getBundle("request.EXTRA_ARGUMENTS")!!.getString("Hello")
        )
    }
}