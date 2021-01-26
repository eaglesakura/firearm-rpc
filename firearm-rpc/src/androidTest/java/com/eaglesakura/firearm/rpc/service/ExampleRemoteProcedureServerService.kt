package com.eaglesakura.firearm.rpc.service

import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.WorkerThread
import androidx.lifecycle.LifecycleService
import com.eaglesakura.armyknife.android.extensions.assertWorkerThread
import com.eaglesakura.firearm.rpc.internal.console
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ExampleRemoteProcedureServerService : LifecycleService(), ProcedureServiceBinder.Callback {

    private val serverService =
        ProcedureServiceBinder(this, this)

    private val exampleServer = ExampleServer()

    private val exampleClient = ExampleClient()

    init {
        exampleServer.echo.listenOnServer = { client, arguments ->
            GlobalScope.launch {
                exampleClient.ping.fetch(client, Bundle())
            }
            Bundle()
        }

        exampleServer.hello.listenOnServer = { client, arguments ->
            console("message [${arguments.getString("message")}]")
            Bundle()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        console("onBind[$intent]")
        return serverService.binder
    }

    override fun onDestroy() {
        console("onDestroy[$this]")
        super.onDestroy()
    }

    @WorkerThread
    override fun onConnectedClient(client: RemoteClient, options: Bundle): Bundle {
        assertWorkerThread()
        console("Connected client[${client.id}]")
        return Bundle()
    }

    @WorkerThread
    override fun onDisconnectedClient(client: RemoteClient) {
        assertWorkerThread()
        console("Disconnected client[${client.id}]")
    }

    override fun executeOnServer(client: RemoteClient, path: String, arguments: Bundle): Bundle {
        console("Hello Remote Call!! [$path]")
        return exampleServer.router.executeOnServer(client, path, arguments)
    }
}
