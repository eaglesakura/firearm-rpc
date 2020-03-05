package com.eaglesakura.firearm.rpc.service.routers

import android.os.Bundle
import com.eaglesakura.firearm.rpc.service.ProcedureServiceConnection
import com.eaglesakura.firearm.rpc.service.RemoteClient
import kotlinx.coroutines.CancellationException

/**
 * Procedure call from client, run in service.
 */
class RestfulServiceProcedure<Arguments, ProcedureResult>(
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
     * Implementation stub for Server.
     */
    internal lateinit var serverProcedure: (client: RemoteClient, arguments: Bundle) -> Bundle

    /**
     * Request client to server.
     * Execute in server.
     */
    suspend operator fun invoke(
        connection: ProcedureServiceConnection,
        arguments: Arguments
    ): ProcedureResult {
        try {
            return bundleToResult(connection.request(path, argumentsToBundle(arguments)))
        } catch (err: CancellationException) {
            throw err
        } catch (err: Exception) {
            throw errorMap(err)
        }
    }

    /**
     * Request handler in server.
     */
    fun listenInServer(block: (client: RemoteClient, arguments: Arguments) -> ProcedureResult) {
        serverProcedure = { client, arg ->
            try {
                resultToBundle(block(client, bundleToArguments(arg)))
            } catch (err: CancellationException) {
                throw err
            } catch (err: Exception) {
                throw errorMap(err)
            }
        }
    }
}