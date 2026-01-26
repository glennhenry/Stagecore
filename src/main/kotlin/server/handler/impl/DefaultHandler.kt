package server.handler.impl

import server.handler.HandlerContext
import server.handler.SocketMessageHandler
import server.messaging.format.DefaultFormat
import server.messaging.socket.DefaultMessage
import utils.logging.Logger
import kotlin.reflect.KClass

/**
 * Default handler as the fallback for any unregistered socket handlers.
 *
 * This handler works together with [DefaultFormat] and [DefaultMessage].
 */
class DefaultHandler : SocketMessageHandler<DefaultMessage> {
    override val name: String = "DefaultHandler"
    override val messageType: String = "Default"
    override val expectedMessageClass: KClass<DefaultMessage> = DefaultMessage::class

    override suspend fun handle(ctx: HandlerContext<DefaultMessage>) = with(ctx) {
        Logger.warn { "No handler registered/implemented for type=${message.type()}" }
    }
}
