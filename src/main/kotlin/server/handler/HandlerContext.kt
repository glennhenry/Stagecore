package server.handler

import server.messaging.SocketMessage

/**
 * Encapsulate objects and data needed by handlers to handle message.
 *
 * @property playerId The player in-game unique identifier.
 * @property message Representation of decoded socket message.
 * @param T Type of [SocketMessage.payload] which the handler operates on.
 */
interface HandlerContext<T> {
    val playerId: String
    val message: SocketMessage<T>

    /**
     * Send the client [raw] (non-serialized) bytes.
     *
     * If needed, caller must serialize bytes manually. This can be done
     * by calling the appropriate serializer utility.
     */
    suspend fun sendRaw(raw: ByteArray, logOutput: Boolean = true, logFull: Boolean = false)
}
