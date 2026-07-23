package org.drinkless.tdlib

/**
 * Represents the result of loading the TDLib native libraries.
 */
sealed class TdLibInitResult {
    /** Native libraries loaded and JNI bridge is functional. */
    object Success : TdLibInitResult()

    /**
     * Native library loading failed.
     *
     * This typically means either:
     *  - `libtdjson.so` is missing from the APK (packaging issue), or
     *  - `libtdjson_jni.so` JNI bridge is missing (NDK bridge not compiled), or
     *  - The `.so` was compiled for a different ABI than the device's.
     */
    data class Error(
        val cause: UnsatisfiedLinkError,
    ) : TdLibInitResult() {
        val message: String get() = cause.message ?: "Unknown UnsatisfiedLinkError"
    }
}

/**
 * Handles safe initialization of TDLib native libraries.
 *
 * Call [init] early in the Application lifecycle (before creating any TDLib client).
 * If [init] returns [TdLibInitResult.Error], the library cannot be used and the
 * application should display a clear error to the user rather than crashing.
 */
object TdLibInitializer {
    @Volatile
    private var result: TdLibInitResult? = null

    /**
     * Attempts to load TDLib native libraries.
     * Safe to call multiple times – only executes once.
     */
    fun init(): TdLibInitResult =
        result ?: synchronized(this) {
            result ?: runInit().also { result = it }
        }

    /**
     * Returns the cached init result, or null if [init] has not been called yet.
     */
    fun getResult(): TdLibInitResult? = result

    /**
     * Returns true if the native libraries are loaded and functional.
     */
    val isAvailable: Boolean get() = result is TdLibInitResult.Success

    private fun runInit(): TdLibInitResult =
        try {
            // libtdjson.so must be loaded first – it provides the C API symbols
            System.loadLibrary("tdjson")
            // Validate that JNI methods are actually reachable by invoking a no-op execute
            // This will throw UnsatisfiedLinkError if the JNI bridge is missing
            JsonClient.execute(0L, "{\"@type\":\"getOption\",\"name\":\"version\"}")
            TdLibInitResult.Success
        } catch (e: UnsatisfiedLinkError) {
            TdLibInitResult.Error(e)
        }
}
