package com.eaglesakura.firearm.rpc.service

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.eaglesakura.armyknife.android.extensions.UIHandler
import com.eaglesakura.armyknife.android.extensions.postOrRun
import com.eaglesakura.firearm.aidl.IRemoteProcedureClient
import com.eaglesakura.firearm.rpc.internal.InternalUtils
import java.io.Closeable

/**
 * Client interface in Server process.
 */
class RemoteClient internal constructor(
    private val parent: ProcedureServiceBinder,
    internal val aidl: IRemoteProcedureClient
) : LifecycleOwner, Closeable {

    private val registry = LifecycleRegistry(this)

    /**
     * Unique id of Client.
     */
    val id: String = InternalUtils.generateUniqueId()

    private val values: MutableMap<String, Any> = mutableMapOf()

    init {
        UIHandler.postOrRun {
            registry.currentState = Lifecycle.State.RESUMED
        }
    }

    /**
     * Put remote client data.
     */
    fun <T> putExtra(key: String, value: T) {
        values[key] = value as Any
    }

    /**
     * Get remote client extra.
     */
    fun <T> getExtra(key: String, defValue: T): T? {
        @Suppress("UNCHECKED_CAST")
        return (values[key] as? T) ?: defValue
    }

    /**
     * Request to client(from server).
     * run client task.
     */
    fun executeOnClient(path: String, arguments: Bundle): Bundle {
        return parent.requestToClient(this, path, arguments)
    }

    override fun close() {
        UIHandler.postOrRun {
            registry.currentState = Lifecycle.State.DESTROYED
        }
    }

    override fun getLifecycle(): Lifecycle = registry
}
