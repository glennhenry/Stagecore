package server.messaging

/**
 * Represents a decoded (non-raw) socket message ready for processing by message handlers.
 *
 * Implementations act as **high-level message objects** that encapsulate the
 * decoded data and any helper methods needed by handlers.
 *
 * Examples:
 * - A comma delimited text message might be represented as `CommaMessage`, providing
 *   methods to access fields with `nextValue()`.
 * - A JSON message might be represented as `JsonMessage`, providing typed
 *   accessors for specific properties.
 */
interface SocketMessage {
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
