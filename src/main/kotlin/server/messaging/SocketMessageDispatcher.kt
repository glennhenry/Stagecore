package server.messaging

import annotation.Untested
import server.handler.impl.DefaultHandler
import server.handler.SocketMessageHandler
import utils.logging.Logger
import utils.logging.Logger.LOG_INDENT_PREFIX

/**
 * Manages handlers and socket message dispatchment.
 *
 * - [register] to add handler.
 * - [findHandlerFor] to find the handlers responsible for handling a [SocketMessage].
 *
 * Dispatchment, by default, is done by matching handler's [SocketMessageHandler.messageType].
 * Handler may override matching behavior through [SocketMessageHandler.shouldHandle].
 *
 * **Note**: Under our socket and server architecture, it's possible that, one socket packet
 * is matched by two different format (two codec) and produced into two different `SocketMessage`
 * instance, which may have different type or different message format class.
 * Then, multiple handlers with can match the two message (**as long as both have the same
 * expected message class**). We do not test this behavior or decide whether it's wrong,
 * as this is too specific and relating to domain. We delegate the opinion to each programmer.
 * Our testing is limited to basic functionality and mitigating potential of exception.
 */
@Untested(
    "Test under various scenarios: " +
            "1. [normal] One message type, one handler, one kind of expected message class." +
            "2. [normal] One message type, two handler, one kind of expected message class." +
            "3. [fail on register] One message type, one codec that produces one message format, " +
            "two handler matching by the same type, but they expect different message class. " +
            "This mean one of the two handler will receive a mismatched message class" +
            "(unexpected runtime behavior, should fail fast)"
)
class SocketMessageDispatcher {
    private val handlers = mutableListOf<SocketMessageHandler<*>>()
    private val handlersByType = mutableMapOf<String, MutableList<SocketMessageHandler<*>>>()

    /**
     * Register a handler.
     */
    fun <T : SocketMessage> register(handler: SocketMessageHandler<T>) {
        // find whether the same messageType has been registered before
        val sameTypes = handlersByType[handler.messageType]

        if (!sameTypes.isNullOrEmpty()) {
            val existingClass = sameTypes.first().expectedMessageClass
            require(existingClass == handler.expectedMessageClass) {
                "Handler registration error: messageType='${handler.messageType}' " +
                        "is already bound to ${existingClass.simpleName}, " +
                        "but handler '${handler.name}' expects ${handler.expectedMessageClass.simpleName}"
            }
        }

        handlers.add(handler)
        handlersByType
            .getOrPut(handler.messageType) { mutableListOf() }
            .add(handler)
    }

    /**
     * Find handlers to handle the particular [SocketMessage].
     *
     * @return Will return single list containing [DefaultHandler]
     *         if there is no matched handlers.
     */
    @Suppress("UNCHECKED_CAST")
    fun findHandlerFor(msg: SocketMessage): List<SocketMessageHandler<out SocketMessage>> {
        val default = handlers.first { it.name == "DefaultHandler" }
        val type = msg.type()

        val byType = handlersByType[type]

        val selected = when {
            // find by registered type (quick match)
            !byType.isNullOrEmpty() -> byType
            // find by match method (slower)
            else -> handlers.filter { handler ->
                (handler as SocketMessageHandler<SocketMessage>).shouldHandle(msg)
            }
            // default handler fallback
        }.ifEmpty { listOf(default) }

        logDispatchment(msg, selected)

        return selected as List<SocketMessageHandler<SocketMessage>>
    }

    private fun logDispatchment(msg: SocketMessage, selected: List<SocketMessageHandler<*>>) {
        Logger.debug {
            buildString {
                appendLine("[SOCKET DISPATCH]")
                appendLine("$LOG_INDENT_PREFIX msg (str) : $msg")
                append("$LOG_INDENT_PREFIX handlers  : ${selected.joinToString { it.name }}")
            }
        }
    }

    fun shutdown() {
        handlers.clear()
    }
}
