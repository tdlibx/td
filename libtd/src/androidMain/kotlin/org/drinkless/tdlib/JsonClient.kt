package org.drinkless.tdlib

/**
 * JNI wrapper for TDLib JSON interface.
 */
object JsonClient {
    init {
        try {
            System.loadLibrary("tdjson")
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    external fun create(): Long

    @JvmStatic
    external fun send(clientId: Long, request: String)

    @JvmStatic
    external fun receive(clientId: Long, timeout: Double): String?

    @JvmStatic
    external fun execute(clientId: Long, request: String): String?

    @JvmStatic
    external fun destroy(clientId: Long)
}
