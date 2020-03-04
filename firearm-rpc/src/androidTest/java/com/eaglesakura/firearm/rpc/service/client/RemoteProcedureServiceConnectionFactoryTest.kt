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
import com.eaglesakura.firearm.rpc.service.ProcedureServiceConnection
import kotlinx.coroutines.channels.Channel
import org.junit.Assert.* // ktlint-disable no-wildcard-imports
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RemoteProcedureServiceConnectionFactoryTest {

    @Test
    fun newConnection() = instrumentationBlockingTest {
        val connection = ProcedureServiceConnectionFactory.connect(
                targetContext,
                object : ProcedureServiceClientCallback {
                    override suspend fun execute(
                        connection: ProcedureServiceConnection,
                        path: String,
                        arguments: Bundle
                    ): Bundle {
                        return Bundle()
                    }
                },
                Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        )
        assertNotEquals("", connection.clientId)
        assertNotNull(connection.connectionHints) // access ok.
        connection.disconnect()
    }

    @Test
    fun requestToServer() = instrumentationBlockingTest {
        ProcedureServiceConnectionFactory.connect(
                targetContext,
                object : ProcedureServiceClientCallback {
                    override suspend fun execute(
                        connection: ProcedureServiceConnection,
                        path: String,
                        arguments: Bundle
                    ): Bundle {
                        return Bundle()
                    }
                },
                Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        ).use { connection ->
            require(connection is ProcedureServiceConnection)
            connection.request("/", bundleOf(Pair("Hello", "World")))
        }
    }

    @Test
    fun echoFromServer() = instrumentationBlockingTest {
        val channel = Channel<Unit>()
        ProcedureServiceConnectionFactory.connect(
                targetContext,
                object : ProcedureServiceClientCallback {
                    override suspend fun execute(
                        connection: ProcedureServiceConnection,
                        path: String,
                        arguments: Bundle
                    ): Bundle {
                        assertEquals("/ping", path)
                        channel.send(Unit)
                        return Bundle()
                    }
                },
                Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        ).use { connection ->
            (connection as ProcedureServiceConnection).request(
                    "/echo",
                    bundleOf(
                            Pair("Hello", "Echo")
                    )
            )
        }

        channel.receive()
    }
}