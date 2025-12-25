package server.messaging.format

import server.messaging.SocketMessage
import server.messaging.codec.SocketCodec

/**
 * Represent the format of socket message with the type [T].
 *
 * @param codec The [SocketCodec] responsible for verifying, decoding, and encoding this message type.
 * @param messageFactory The factory to instantiate a [SocketMessage] instance of type [T].
 * @param T The message data type handled by this instance.
 */
data class MessageFormat<T>(
    val codec: SocketCodec<T>,
    val messageFactory: (T) -> SocketMessage<T>
)
