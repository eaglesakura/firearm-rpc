package com.eaglesakura.firearm.rpc.service.client

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eaglesakura.armyknife.android.junit4.extensions.instrumentationBlockingTest
import com.eaglesakura.armyknife.android.junit4.extensions.targetContext
import com.eaglesakura.armyknife.android.junit4.extensions.testContext
import com.eaglesakura.firearm.rpc.extensions.use
import com.eaglesakura.firearm.rpc.service.ExampleRemoteProcedureServerService
import com.eaglesakura.firearm.rpc.service.ProcedureClientCallback
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnectionFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteProcedureServiceConnectionFactoryTest {

    @Test
    fun newConnection() = instrumentationBlockingTest {
        val connection = ProcedureServerConnectionFactory.connect(
            targetContext,
            object : ProcedureClientCallback {
                override fun executeOnClient(
                    connection: ProcedureServerConnection,
                    path: String,
                    arguments: Bundle
                ): Bundle {
                    return Bundle()
                }
            },
            Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        )
        assertNotEquals("", connection.connectionId)
        assertNotNull(connection.connectionHints) // access ok.
        connection.disconnect()
    }

    @Test
    fun requestToServer() = instrumentationBlockingTest {
        ProcedureServerConnectionFactory.connect(
            targetContext,
            object : ProcedureClientCallback {
                override fun executeOnClient(
                    connection: ProcedureServerConnection,
                    path: String,
                    arguments: Bundle
                ): Bundle {
                    return Bundle()
                }
            },
            Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        ).use { connection ->
            connection.executeOnServer("/", bundleOf(Pair("Hello", "World")))
        }
    }

    @Test
    fun echoFromServer() = instrumentationBlockingTest {
        val channel = Channel<Unit>()
        ProcedureServerConnectionFactory.connect(
            targetContext,
            object : ProcedureClientCallback {
                override fun executeOnClient(
                    connection: ProcedureServerConnection,
                    path: String,
                    arguments: Bundle
                ): Bundle {
                    assertEquals("/ping", path)
                    channel.sendBlocking(Unit)
                    return Bundle()
                }
            },
            Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        ).use { connection ->
            connection.executeOnServer(
                "/echo",
                bundleOf(
                    Pair("Hello", "Echo")
                )
            )
        }

        channel.receive()
    }
}