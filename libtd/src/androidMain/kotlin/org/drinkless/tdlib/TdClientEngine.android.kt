package org.drinkless.tdlib

actual class TdClientEngine actual constructor() {
    actual fun createClient(): Long = JsonClient.create()

    actual fun send(clientId: Long, jsonQuery: String) {
        JsonClient.send(clientId, jsonQuery)
    }

    actual fun receive(clientId: Long, timeout: Double): String? = JsonClient.receive(clientId, timeout)
}
