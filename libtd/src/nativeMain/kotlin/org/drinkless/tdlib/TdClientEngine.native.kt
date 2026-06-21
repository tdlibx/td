package org.drinkless.tdlib

import kotlinx.cinterop.*
import clib.tdjson.*

@OptIn(ExperimentalForeignApi::class)
actual class TdClientEngine actual constructor() {

    actual fun createClient(): Long {
        val ptr: COpaquePointer? = td_json_client_create()
        return ptr.toLong()
    }

    actual fun send(clientId: Long, jsonQuery: String) {
        if (clientId == 0L) return
        val ptr: COpaquePointer? = clientId.toCPointer<CPointed>()
        td_json_client_send(ptr, jsonQuery)
    }

    actual fun receive(clientId: Long, timeout: Double): String? {
        if (clientId == 0L) return null
        val ptr: COpaquePointer? = clientId.toCPointer<CPointed>()
        val responsePtr: CPointer<ByteVar>? = td_json_client_receive(ptr, timeout)
        return responsePtr?.toKString()
    }
}
