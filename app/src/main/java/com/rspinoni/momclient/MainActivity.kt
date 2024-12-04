package com.rspinoni.momclient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    //to find the localhost IP use ipconfig and check the IPv4 of Wireless LAN adapter Wi-Fi
    private val url: String = "ws://172.16.196.11:8080/websocket"
    private val subscriptionPath: String = "/topic/notifications"
    private val stompClientService: StompClientService = StompClientService(url, subscriptionPath)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        stompClientService.connectAndSubscribe()
    }

    override fun onDestroy() {
        stompClientService.disconnect()
        super.onDestroy()
    }
}