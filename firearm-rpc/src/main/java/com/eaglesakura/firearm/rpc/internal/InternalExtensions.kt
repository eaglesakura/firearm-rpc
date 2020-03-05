package com.eaglesakura.firearm.rpc.internal

import androidx.annotation.WorkerThread
import com.eaglesakura.armyknife.android.extensions.assertWorkerThread
import com.eaglesakura.firearm.rpc.Configure
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

/**
 * Blocking run in new worker thread.
 */
@WorkerThread
fun <T> blockingRunInWorker(name: String, block: () -> T): T {
    assertWorkerThread()
    val channel = Channel<Pair<T?, Exception?>>()
    thread(name = name) {
        Configure.log("prc-worker", "Execute $name")
        try {
            val result = Pair(block(), null)
            channel.sendBlocking(result)
        } catch (e: Exception) {
            channel.sendBlocking(Pair(null, e))
        } finally {
            Configure.log("prc-worker", "Finish $name")
        }
    }

    val result = runBlocking {
        channel.receive()
    }
    if (result.second != null) {
        throw result.second!!
    } else {
        return result.first as T
    }
}