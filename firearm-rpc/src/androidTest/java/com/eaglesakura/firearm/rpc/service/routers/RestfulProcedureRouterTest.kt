package com.eaglesakura.firearm.rpc.service.routers

import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eaglesakura.armyknife.android.junit4.extensions.instrumentationBlockingTest
import com.eaglesakura.armyknife.android.junit4.extensions.targetContext
import com.eaglesakura.armyknife.android.junit4.extensions.testContext
import com.eaglesakura.firearm.rpc.extensions.use
import com.eaglesakura.firearm.rpc.internal.console
import com.eaglesakura.firearm.rpc.service.ExampleClient
import com.eaglesakura.firearm.rpc.service.ExampleRemoteProcedureServerService
import com.eaglesakura.firearm.rpc.service.ExampleServer
import com.eaglesakura.firearm.rpc.service.ProcedureClientCallback
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnectionFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RestfulProcedureRouterTest {

    val exampleServer = ExampleServer()
    val exampleClient = ExampleClient()

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
            exampleServer.hello.fetch(connection, bundleOf("message" to "World"))
        }
    }

    @Test
    fun echoFromServer() = instrumentationBlockingTest {
        val channel = Channel<Unit>()

        exampleClient.ping.listenOnClient = { connection, arguments ->
            console("Ping from server[$connection]")
            channel.sendBlocking(Unit)
            Bundle()
        }

        ProcedureServerConnectionFactory.connect(
            targetContext,
            exampleClient.router,
            Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        ).use { connection ->
            exampleServer.echo.fetch(connection, Bundle())
        }
        channel.receive()
    }
}
