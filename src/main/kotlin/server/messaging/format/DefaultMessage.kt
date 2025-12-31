package server.messaging.format

import server.messaging.SocketMessage

/**
 * Default implementation of [SocketMessage] where any message format
 * is decoded into UTF-8 [String].
 *
 * This is used as fallback for any unknown message format.
 *
 * - Underlying payload is simply a `String` which is the UTF-8 decoded of raw bytes.
 * - The type of message is always `[Undetermined]`.
 * - Any message is always considered as valid.
 * - Message is empty when the string payload is also empty.
 */
class DefaultMessage(val payload: String) : SocketMessage {
    private var type: String = "[Undetermined]"
    override fun type(): String = type
    override fun isValid(): Boolean = true
    override fun isEmpty(): Boolean = payload.isEmpty()
    override fun toString(): String = "DefaultMessage($payload)"
}
