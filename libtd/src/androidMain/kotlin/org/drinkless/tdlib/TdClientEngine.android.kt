package org.drinkless.tdlib

actual class TdClientEngine actual constructor() {

    init {
        try {
            System.loadLibrary("tdjson")
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
    }

    actual fun createClient(): Long {
        return JsonClient.create()
    }

    actual fun send(clientId: Long, jsonQuery: String) {
        JsonClient.send(clientId, jsonQuery)
    }

    actual fun receive(clientId: Long, timeout: Double): String? {
        return JsonClient.receive(clientId, timeout)
    }
}
