package org.drinkless.tdlib

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong
import clib.tdjson.td_json_client_create
import clib.tdjson.td_json_client_receive
import clib.tdjson.td_json_client_send

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
