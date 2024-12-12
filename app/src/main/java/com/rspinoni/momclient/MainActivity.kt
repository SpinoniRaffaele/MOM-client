package com.rspinoni.momclient

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.rspinoni.momclient.model.UserWithPreKey
import com.rspinoni.momclient.rest.RestClientService
import com.rspinoni.momclient.signal.SignalProtocolService
import com.rspinoni.momclient.stomp.StompClientService
import java.util.UUID

class MainActivity : AppCompatActivity() {
    //to find the localhost IP use `ipconfig` and check the IPv4 of the connection you have
    private val domain: String = "172.16.196.11:8080"
    private val httpServerUrl: String = "http://$domain"
    private val websocketUrl: String = "ws://$domain/websocket"
    private val subscriptionPath: String = "/topic/notifications"
    private val stompClientService: StompClientService = StompClientService(websocketUrl, subscriptionPath)
    private val signalProtocolService: SignalProtocolService = SignalProtocolService()
    private val restClientService: RestClientService = RestClientService(httpServerUrl)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //stompClientService.connectAndSubscribe()
    }

    override fun onDestroy() {
        stompClientService.disconnect()
        super.onDestroy()
    }

    fun subscribeHandler(v: View) {
        val phoneNumber: EditText = findViewById(R.id.editTextPhone)
        val id = UUID.randomUUID().toString()
        val entity = signalProtocolService.createEntity(phoneNumber.text.toString())
        Log.i("Subscribe",
            "Subscribe ${phoneNumber.text}, id: $id and preKey: ${entity.preKey}")
        //todo think about how to send the preKey and store it
        restClientService.register(UserWithPreKey(id, phoneNumber.text.toString(),
            "thisShouldBeThePreKey"))
    }
}