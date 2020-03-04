package com.eaglesakura.firearm.rpc.service

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.UiThread
import androidx.lifecycle.LifecycleService
import com.eaglesakura.armyknife.android.extensions.assertUIThread
import com.eaglesakura.firearm.rpc.internal.console
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExampleRemoteProcedureServerService : LifecycleService(), ProcedureServiceBinder.Callback {

    private val serverService =
        ProcedureServiceBinder(this, this, Dispatchers.Default)

    private val serverRouter = ExampleProcedureServer()

    private val clientRouter = ExampleProcedureClient()

    init {
        serverRouter.echo.listenInServer { client, arguments ->
            GlobalScope.launch {
                clientRouter.ping(client, ExampleProcedureServer.VoidBundle())
            }

            ExampleProcedureServer.VoidBundle()
        }

        serverRouter.hello.listenInServer { client, arguments ->
            console("Message [${arguments.message}]")
            ExampleProcedureServer.VoidBundle()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        console("onBind[$intent]")
        return serverService.binder
    }

    override fun onDestroy() {
        console("onDestroy[$this]")
        super.onDestroy()
    }

    @UiThread
    override fun onConnectedClient(client: RemoteClient, options: Bundle): Bundle {
        assertUIThread()
        console("Connected client[${client.id}]")
        return Bundle() // Void.
    }

    override fun onDisconnectedClient(client: RemoteClient) {
        assertUIThread()
        console("Disconnected client[${client.id}]")
    }

    override suspend fun execute(client: RemoteClient, path: String, arguments: Bundle): Bundle {
        console("Hello Remote Call!! [$path]")
        return serverRouter.router(client, path, arguments)
    }
}