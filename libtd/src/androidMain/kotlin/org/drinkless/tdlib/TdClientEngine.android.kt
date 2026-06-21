package org.drinkless.tdlib

actual class PlatformTdClientEngine actual constructor() : TdEngine {

    init {
        try {
            System.loadLibrary("tdjson")
        } catch (e: UnsatisfiedLinkError) {
            e.printStackTrace()
        }
    }

    override fun createClient(): Long {
        return JsonClient.create()
    }

    override fun send(clientId: Long, jsonQuery: String) {
        JsonClient.send(clientId, jsonQuery)
    }

    override fun receive(clientId: Long, timeout: Double): String? {
        return JsonClient.receive(clientId, timeout)
    }
}
