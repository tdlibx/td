package org.drinkless.tdlib

/**
 * JNI wrapper for TDLib JSON interface.
 *
 * Loads both libtdjson.so (TDLib C library) and libtdjson_jni.so (JNI bridge
 * that maps Java external methods to the TDLib C API symbols).
 */
object JsonClient {
    init {
        try {
            // libtdjson.so must be loaded first – it provides the C API symbols
            System.loadLibrary("tdjson")
            // libtdjson_jni.so provides Java_org_drinkless_tdlib_JsonClient_* JNI symbols
            System.loadLibrary("tdjson_jni")
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
