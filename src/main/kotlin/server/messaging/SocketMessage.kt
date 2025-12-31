package server.messaging

import server.messaging.format.MessageFormat
import server.messaging.codec.SocketCodec

/**
 * Represents a decoded (non-raw) socket message whose raw bytes have been
 * deserialized into a structured [payload], ready for processing by message handlers.
 *
 * Implementations act as typed containers for the decoded payload.
 * The structure of [payload] is determined by the associated [MessageFormat]
 * and its [SocketCodec] implementation.
 *
 * For example:
 * - A delimited text message might produce `SocketMessage<List<String>>`
 * - A JSON message might produce `SocketMessage<Map<String, Any?>>`
 *
 * Implementations may also provide metadata such as a message type identifier
 * returned by [type], which handlers use to determine whether to process the message.
 *
 * @param T The type of the decoded payload, as defined by the message format.
 */
interface SocketMessage<T> {
    /**
     * The deserialized payload extracted from the raw socket data.
     */
    val payload: T

    /**
     * Returns the logical type or identifier of this socket message.
     */
    fun type(): String

    /**
     * Indicates whether the message is valid and safe to process.
     */
    fun isValid(): Boolean

    /**
     * Indicates whether the message is considered as empty.
     */
    fun isEmpty(): Boolean

    /**
     * Returns a human-readable string representation of this message,
     * primarily intended for debugging and logging purposes.
     */
    override fun toString(): String
}
