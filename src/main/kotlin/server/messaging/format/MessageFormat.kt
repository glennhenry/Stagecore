package server.messaging.format

import server.messaging.codec.SocketCodec
import server.messaging.SocketMessage

/**
 * Describes a socket message format, separating wire-level data from the
 * higher-level message payload.
 *
 * It acts as a bridge between codec's output from raw bytes
 * with the [messageFactory] which transform it into high-level [SocketMessage].
 *
 * @param T The raw message data type produced by the codec (wire representation).
 * @param codec The [SocketCodec] responsible for verifying, decoding, and encoding
 *        the raw message data.
 * @param messageFactory A factory that transforms the decoded [T] data into a
 *        high-level [SocketMessage] representation.
 */
data class MessageFormat<T>(
    val codec: SocketCodec<T>,
    val messageFactory: (T) -> SocketMessage
)
