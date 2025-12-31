package server.handler

import annotation.RevisitLater
import server.messaging.SocketMessage
import server.messaging.codec.SocketCodec
import server.messaging.format.MessageFormat

/**
 * Template for a socket message handler.
 *
 * Each handler is expected to:
 * - Declare the message type it handles via [messageType].
 * - Specify the expected [SocketMessage] implementation through its generic parameter `T`.
 *   The [SocketMessage] implementation is produced by a [MessageFormat]
 *   and its associated [SocketCodec].
 *   For example, a JSON-based handler might declare `T` as `JSONMessage`, which
 *   wraps a `Map<String, Any>` and provides helper methods, while implementing
 *   `SocketMessage<Map<String, Any>>`.
 *
 * Handler matching behavior:
 * - Incoming [SocketMessage] instances are routed to handlers by the dispatcher.
 * - The default dispatchment logic involves matching handler's [messageType]
 *   [SocketMessage.type].
 * - Override [shouldHandle] only if type-based matching is insufficient.
 *
 * **Contract**: All handlers registered under the same [messageType] must expect
 * the same concrete [SocketMessage] implementation (by architecture).
 *
 * @param T The concrete implementation of [SocketMessage] this handler expects.
 */
@RevisitLater(
    "We may want to enforce type safety on T, so socket dispatchment" +
            "rely on shouldHandle() and runtime validation of declared payload type" +
            "and actual received payload type"
)
interface SocketMessageHandler<T : SocketMessage> {
    /**
     * Human-readable name for the handler, mainly used for logging and debugging.
     */
    val name: String

    /**
     * Message type or identifier that this handler is responsible for.
     */
    val messageType: String

    /**
     * Concrete message class this handler expects (which same as [T]).
     * Use the syntax `classname::class.java`.
     */
    val expectedMessageClass: Class<out SocketMessage>

    /**
     * Determines whether this handler should process the given [message].
     *
     * Default implementation compares the message's type with [messageType].
     * Handler may override this behavior if unsuitable or insufficient.
     *
     * @param message The socket message to evaluate.
     * @return `true` if this handler should handle the message; otherwise `false`.
     */
    fun shouldHandle(message: T): Boolean {
        return message.type() == messageType
    }

    /**
     * Handles the socket message.
     *
     * @param ctx The handler context, containing the message, player ID,
     * and [HandlerContext.sendRaw] method for sending responses.
     */
    suspend fun handle(ctx: HandlerContext<T>)
}
