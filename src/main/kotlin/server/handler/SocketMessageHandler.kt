package server.handler

import server.messaging.SocketMessage

/**
 * A template for socket message handler.
 *
 * Each handler is expected to:
 * - Declare the message type it handles via [SocketMessageHandler.messageType]
 * - Declare the expected payload type via its generic parameter `T`
 *
 * The dispatcher will route incoming [SocketMessage] instances to handlers
 * based on the message's [SocketMessage.type], and will provide a
 * [HandlerContext]`<T>` whose payload type matches the handler's expectation.
 *
 * Handler matching behavior:
 * - The default [match] implementation routes messages based on their
 *   [SocketMessage.type].
 * - Handlers should override [match] only when type-based matching is insufficient.
 */
interface SocketMessageHandler<T> {
    val name: String
    val messageType: String

    fun match(message: SocketMessage<*>): Boolean {
        return message.type() == messageType
    }

    suspend fun handle(ctx: HandlerContext<T>)
}
