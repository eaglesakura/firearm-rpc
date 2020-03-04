package com.eaglesakura.firearm.rpc.service.routers

import android.content.Intent
import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eaglesakura.armyknife.android.junit4.extensions.instrumentationBlockingTest
import com.eaglesakura.armyknife.android.junit4.extensions.targetContext
import com.eaglesakura.armyknife.android.junit4.extensions.testContext
import com.eaglesakura.firearm.rpc.extensions.use
import com.eaglesakura.firearm.rpc.internal.console
import com.eaglesakura.firearm.rpc.service.ExampleProcedureClient
import com.eaglesakura.firearm.rpc.service.ExampleProcedureServer
import com.eaglesakura.firearm.rpc.service.ExampleRemoteProcedureServerService
import com.eaglesakura.firearm.rpc.service.ProcedureServiceConnection
import com.eaglesakura.firearm.rpc.service.client.ProcedureServiceClientCallback
import com.eaglesakura.firearm.rpc.service.client.ProcedureServiceConnectionFactory
import kotlinx.coroutines.channels.Channel
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RestfulProcedureRouterTest {

    init {
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
            val procedure = ExampleProcedureServer()
            procedure.hello(connection, ExampleProcedureServer.HelloArguments("World"))
        }
    }

    @Test
    fun echoFromServer() = instrumentationBlockingTest {
        val channel = Channel<Unit>()

        val clientRouter = ExampleProcedureClient()
        clientRouter.ping.listenInClient { connection, arguments ->
            console("Ping from server[$connection]")
            channel.send(Unit)
            ExampleProcedureServer.VoidBundle()
        }

        ProcedureServiceConnectionFactory.connect(
            targetContext,
            clientRouter.router,
            Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        ).use { connection ->
            require(connection is ProcedureServiceConnection)
            val server = ExampleProcedureServer()
            server.echo(connection, ExampleProcedureServer.EchoArguments(Bundle()))
        }
        channel.receive()
    }
}