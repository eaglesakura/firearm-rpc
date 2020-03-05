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
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection
import com.eaglesakura.firearm.rpc.service.ProcedureClientCallback
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnectionFactory
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.sendBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RestfulProcedureRouterTest {

    init {
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
            require(connection is ProcedureServerConnection)
            val procedure = ExampleProcedureServer()
            procedure.hello.fetch(connection, ExampleProcedureServer.HelloArguments("World"))
        }
    }

    @Test
    fun echoFromServer() = instrumentationBlockingTest {
        val channel = Channel<Unit>()

        val clientRouter = ExampleProcedureClient()
        clientRouter.ping.listenInClient { connection, arguments ->
            console("Ping from server[$connection]")
            channel.sendBlocking(Unit)
            ExampleProcedureServer.VoidBundle()
        }

        ProcedureServerConnectionFactory.connect(
                targetContext,
                clientRouter.router,
                Intent(testContext, ExampleRemoteProcedureServerService::class.java)
        ).use { connection ->
            require(connection is ProcedureServerConnection)
            val server = ExampleProcedureServer()
            server.echo.fetch(connection, ExampleProcedureServer.EchoArguments(Bundle()))
        }
        channel.receive()
    }
}