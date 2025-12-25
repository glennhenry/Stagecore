package server.messaging.format

import utils.logging.Logger

/**
 * Track message format and their codecs.
 */
class MessageFormatFinder {
    private val formats = mutableListOf<MessageFormat<*>>()

    fun <T> register(format: MessageFormat<T>) {
        formats.add(format)
    }

    /**
     * Detect the possible [MessageFormat] for raw bytes [data].
     */
    fun detectMessageFormat(data: ByteArray): List<MessageFormat<*>> {
        lateinit var default: MessageFormat<*>
        val matched = mutableListOf<MessageFormat<*>>()
        val peek = data.take(20)

        for (format in formats) {
            if (format.codec.name == "DefaultCodec") default = format
            try {
                val verifySuccess = format.codec.verify(data)
                if (verifySuccess) {
                    matched.add(format)
                }
            } catch (e: Exception) {
                Logger.verbose { "${format.codec.name} couldn't decode for data (peek-20): $peek; with error: $e." }
            }
        }

        return matched.ifEmpty { listOf(default) }
    }
}
