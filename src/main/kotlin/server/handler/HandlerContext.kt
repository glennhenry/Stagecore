package server.handler

import annotation.RevisitLater
import server.core.network.Connection
import server.messaging.SocketMessage

/**
 * Encapsulate objects and data needed by handlers to handle message.
 *
 * @property playerId The player in-game unique identifier.
 * @property message High-level representation of the socket message.
 * @param T Concrete implementation of [SocketMessage] interface.
 */
@RevisitLater(
    "We may want to enforce type safety on T, so socket dispatchment" +
            "rely on shouldHandle() and runtime validation of declared payload type" +
            "and actual received payload type"
)
interface HandlerContext<T : SocketMessage> {
    var playerId: String
    val message: T

    /**
     * Send the client [raw] (non-serialized) bytes.
     *
     * If needed, caller must serialize bytes manually. This can be done
     * by calling the appropriate serializer utility.
     */
    suspend fun sendRaw(raw: ByteArray, logOutput: Boolean = true, logFull: Boolean = false)

    /**
     * To update the playerId for this connection (usually goes through [Connection.updatePlayerId]).
     */
    fun updatePlayerId(playerId: String)
}
