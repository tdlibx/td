package org.drinkless.tdlib

actual class PlatformTdClientEngine actual constructor() : TdEngine {
    actual override fun createClient(): Long = JsonClient.create()

    actual override fun send(
        clientId: Long,
        jsonQuery: String,
    ) {
        JsonClient.send(clientId, jsonQuery)
    }

    actual override fun receive(
        clientId: Long,
        timeout: Double,
    ): String? = JsonClient.receive(clientId, timeout)
}
