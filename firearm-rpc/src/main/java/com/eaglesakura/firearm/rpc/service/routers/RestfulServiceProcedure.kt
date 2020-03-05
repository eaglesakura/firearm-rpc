package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.ProcedureServerConnection
import com.eaglesakura.firearm.rpc.service.RemoteClient
import kotlinx.coroutines.CancellationException

/**
 * Procedure call from client, run in service.
 */
class RestfulServiceProcedure(
    /**
     * Procedure path.
     */
    val path: String
) {
    /**
     * Convert exception.
     */
    var errorMap: (error: Exception) -> Exception = { it }

    /**
     * Implementation stub for Server.
     */
    internal lateinit var listenInServer: (client: RemoteClient, arguments: Bundle) -> Bundle

    /**
     * Request client to server.
     * Execute in server.
     */
    fun fetch(
        connection: ProcedureServerConnection,
        arguments: Bundle
    ): Bundle {
        try {
            return connection.executeOnServer(path, arguments)
        } catch (err: CancellationException) {
            throw err
        } catch (err: Exception) {
            throw errorMap(err)
        }
    }
}