package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.BroadcastResult
import com.eaglesakura.firearm.rpc.service.ProcedureServiceBinder
import com.eaglesakura.firearm.rpc.service.ProcedureServiceConnection
import com.eaglesakura.firearm.rpc.service.RemoteClient
import kotlinx.coroutines.CancellationException

/**
 * Procedure call from service, run in client.
 */
class RestfulClientProcedure<Arguments, ProcedureResult>(
    /**
     * Procedure path.
     */
    val path: String
) {
    /**
     * Convert Arguments to Bundle.
     */
    lateinit var argumentsToBundle: (arguments: Arguments) -> Bundle

    /**
     * Convert Bundle to Arguments.
     */
    lateinit var bundleToArguments: (arguments: Bundle) -> Arguments

    /**
     * Convert Bundle to ProcedureResult.
     */
    lateinit var bundleToResult: (result: Bundle) -> ProcedureResult

    /**
     * Convert ProcedureResult to Bundle.
     */
    lateinit var resultToBundle: (result: ProcedureResult) -> Bundle

    /**
     * Convert exception.
     */
    var errorMap: (error: Exception) -> Exception = { it }

    /**
     * Implementation stub for Client.
     */
    internal lateinit var clientProcedure: suspend (connection: ProcedureServiceConnection, arguments: Bundle) -> Bundle
        private set

    /**
     * Request handler in client.
     */
    fun listenInClient(block: suspend (connection: ProcedureServiceConnection, arguments: Arguments) -> ProcedureResult) {
        clientProcedure = { connection, arg ->
            try {
                resultToBundle(block(connection, bundleToArguments(arg)))
            } catch (err: CancellationException) {
                throw err
            } catch (err: Exception) {
                throw errorMap(err)
            }
        }
    }

    /**
     * Request service to client.
     * Execute in client.
     */
    suspend operator fun invoke(
        client: RemoteClient,
        arguments: Arguments
    ): ProcedureResult {
        try {
            return bundleToResult(client.request(path, argumentsToBundle(arguments)))
        } catch (err: CancellationException) {
            throw err
        } catch (err: Exception) {
            throw errorMap(err)
        }
    }

    /**
     * Request service to all clients.
     * Execute in all clients.
     */
    suspend operator fun invoke(
        binder: ProcedureServiceBinder,
        arguments: Arguments
    ): List<BroadcastResult<ProcedureResult>> {
        val result = mutableListOf<BroadcastResult<ProcedureResult>>()
        for (client in binder.allClients) {
            try {
                result.add(BroadcastResult(client, invoke(client, arguments), null))
            } catch (e: Exception) {
                result.add(BroadcastResult(client, null, e))
            }
        }
        return result.toList()
    }
}