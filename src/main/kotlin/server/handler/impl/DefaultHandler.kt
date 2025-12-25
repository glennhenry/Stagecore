package server.handler.impl

import server.handler.HandlerContext
import server.handler.SocketMessageHandler
import server.messaging.SocketMessage
import utils.logging.Logger

/**
 * Default handler as the fallback for any unregistered socket handlers.
 */
class DefaultHandler : SocketMessageHandler {
    override val name: String = "DefaultHandler"

    override fun <T> match(message: SocketMessage<T>): Boolean {
        return true
    }

    override suspend fun handle(ctx: HandlerContext) = with(ctx) {
        Logger.warn { "No handler registered/implemented for type=${message.type()}" }
    }
}