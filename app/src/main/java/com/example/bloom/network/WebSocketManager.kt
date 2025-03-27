package com.example.bloom.network

import okhttp3.*
import java.util.concurrent.TimeUnit

object WebSocketManager {
    private var webSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .readTimeout(3, TimeUnit.SECONDS)
        .build()
    //const val SERVER_URL = "ws://10.0.2.2:8080/ws/unity" // 에뮬레이터에서 localhost 대체

   const val SERVER_URL = "wss://bloom-story.kro.kr/ws/unity" // ⭐ 서버 주소

    fun connect(listener: WebSocketListener) {
        val request = Request.Builder().url(SERVER_URL).build()
        webSocket = client.newWebSocket(request, listener)
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    fun close() {
        webSocket?.close(1000, "Connection closed")
    }
}
