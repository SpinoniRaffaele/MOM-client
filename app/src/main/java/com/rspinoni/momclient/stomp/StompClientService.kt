package com.rspinoni.momclient.stomp

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.subscribeText
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

class StompClientService(private val url: String, private val subscriptionPath: String) {
    private val scope = CoroutineScope(MainScope().coroutineContext)
    private var session: StompSession? = null
    private val stompClient = StompClient(OkHttpWebSocketClient())

    fun connectAndSubscribe() {
        scope.launch {
            try {
                session = stompClient.connect(url)
                Log.i("Socket", "Connected")
                val subscription: Flow<String> = session!!.subscribeText(subscriptionPath)
                subscription.collect {
                        msg -> Log.i("Socket", "Received: $msg")
                }
            } catch (e: Exception) {
                Log.e("Socket", e.stackTraceToString())
            }
        }
    }

    fun disconnect() {
        runBlocking {
            session?.disconnect()
            Log.i("Socket", "Disconnected")
        }
        scope.cancel()
    }
}