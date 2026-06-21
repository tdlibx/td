package org.drinkless.tdlib

interface TdEngine {
    fun createClient(): Long
    fun send(clientId: Long, jsonQuery: String)
    fun receive(clientId: Long, timeout: Double): String?
}

expect class PlatformTdClientEngine() : TdEngine

class TdClientEngine(private val delegate: TdEngine = PlatformTdClientEngine()) : TdEngine {
    override fun createClient(): Long = delegate.createClient()
    override fun send(clientId: Long, jsonQuery: String) = delegate.send(clientId, jsonQuery)
    override fun receive(clientId: Long, timeout: Double): String? = delegate.receive(clientId, timeout)
}
