package org.drinkless.tdlib

expect class TdClientEngine() {
    fun createClient(): Long
    fun send(clientId: Long, jsonQuery: String)
    fun receive(clientId: Long, timeout: Double): String?
}
