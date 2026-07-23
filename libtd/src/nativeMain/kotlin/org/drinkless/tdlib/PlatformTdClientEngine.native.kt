package org.drinkless.tdlib

import clib.tdjson.td_json_client_create
import clib.tdjson.td_json_client_receive
import clib.tdjson.td_json_client_send
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.CPointed
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toCPointer
import kotlinx.cinterop.toKString
import kotlinx.cinterop.toLong

@OptIn(ExperimentalForeignApi::class)
actual class PlatformTdClientEngine actual constructor() : TdEngine {
    actual override fun createClient(): Long {
        val ptr: COpaquePointer? = td_json_client_create()
        return ptr.toLong()
    }

    actual override fun send(
        clientId: Long,
        jsonQuery: String,
    ) {
        if (clientId == 0L) return
        val ptr: COpaquePointer? = clientId.toCPointer<CPointed>()
        td_json_client_send(ptr, jsonQuery)
    }

    actual override fun receive(
        clientId: Long,
        timeout: Double,
    ): String? {
        if (clientId == 0L) return null
        val ptr: COpaquePointer? = clientId.toCPointer<CPointed>()
        val responsePtr: CPointer<ByteVar>? = td_json_client_receive(ptr, timeout)
        return responsePtr?.toKString()
    }
}
