package server.handler

import server.messaging.socket.SocketMessage
import server.messaging.format.MessageFormat
import kotlin.reflect.KClass

/**
 * Template for a socket message handler.
 *
 * A [SocketMessageHandler] processes a specific kind of [SocketMessage] produced
 * by the message decoding and materialization pipeline.
 *
 * Each handler is expected to:
 * - Declare the logical message type it handles via [messageType].
 * - Specify the concrete [SocketMessage] implementation it expects through
 *   the generic parameter [T].
 *
 * The [SocketMessage] instance is produced by [MessageFormat.materialize].
 *
 * Examples:
 * - A JSON-based protocol may define a handler with `T = JsonMessage`,
 *   where `JsonMessage` wraps a `Map<String, Any>` and provides helper accessors.
 * - A more specific handler may declare `T = LoginRequest`, where
 *   `LoginRequest` is a strongly typed domain message implementing `SocketMessage`.
 *
 * Handler dispatch behavior:
 * - Incoming [SocketMessage] instances are routed to handlers by a dispatcher.
 * - By default, dispatch is performed by matching [messageType] against
 *   [SocketMessage.type].
 * - Override [shouldHandle] when type-based matching is insufficient.
 *
 * **Contract**: All handlers registered under the same [messageType] must expect
 * the same concrete [SocketMessage] implementation (by architecture).
 *
 * @param T The concrete implementation of [SocketMessage] this handler expects.
 */
interface SocketMessageHandler<T : SocketMessage> {
    /**
     * Human-readable name of the handler for logging and debugging.
     */
    val name: String

    /**
     * Logical message type or identifier that this handler is responsible for.
     *
     * This value is compared against [SocketMessage.type] during dispatch.
     *
     * **Important**: all socket message's type should be different, regardless
     * when they are different [SocketMessage] implementation. This is because
     * dispatchment logic solely rely on type.
     */
    val messageType: String

    /**
     * Concrete message class this handler expects (which same as [T]).
     * Use the syntax `classname::class`.
     */
    val expectedMessageClass: KClass<T>

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
     * Handles an incoming socket message.
     *
     * @param ctx The handler context, containing the decoded message,
     * connection metadata (such as player ID), and utilities such as
     * [HandlerContext.sendRaw] for sending responses.
     */
    suspend fun handle(ctx: HandlerContext<T>)
}
