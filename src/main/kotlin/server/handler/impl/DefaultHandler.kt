package server.handler.impl

import server.handler.SocketMessageHandler
import server.handler.HandlerContext
import server.messaging.SocketMessage
import utils.logging.Logger
import server.messaging.codec.DefaultCodec
import server.messaging.format.DefaultMessage

/**
 * Default handler as the fallback for any unregistered socket handlers.
 *
 * This handler works together with [DefaultCodec] and [DefaultMessage].
 */
class DefaultHandler : SocketMessageHandler<DefaultMessage> {
    override val name: String = "DefaultHandler"
    override val messageType: String = "Default"
    override val expectedMessageClass: Class<out SocketMessage> = DefaultMessage::class.java

    override fun shouldHandle(message: DefaultMessage): Boolean {
        return true
    }

    override suspend fun handle(ctx: HandlerContext<DefaultMessage>) = with(ctx) {
        Logger.warn { "No handler registered/implemented for type=${message.type()}" }
    }
}
