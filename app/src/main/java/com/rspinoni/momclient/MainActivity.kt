package com.rspinoni.momclient

import android.os.Bundle
import android.util.ArraySet
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rspinoni.momclient.adapters.ChatsListAdapter
import com.rspinoni.momclient.model.DataStorePreferences
import com.rspinoni.momclient.model.UserWithPreKey
import com.rspinoni.momclient.rest.RestClientService
import com.rspinoni.momclient.stomp.StompClientService
import com.rspinoni.momclient.storage.ClientDataStoreService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.UUID

class MainActivity : AppCompatActivity() {
    //to find the localhost IP use `ipconfig` and check the IPv4 of the connection you have
    private val domain: String = "192.168.43.206:8080"
    private val httpServerUrl: String = "http://$domain"
    private val websocketUrl: String = "ws://$domain/websocket"
    private val subscriptionPath: String = "/topic/notifications"
    private val stompClientService: StompClientService = StompClientService(websocketUrl, subscriptionPath)
    private val restClientService: RestClientService = RestClientService(httpServerUrl, this)
    private val clientDataStoreService: ClientDataStoreService = ClientDataStoreService(this)
    private val scope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        scope.launch {
            val preferences = clientDataStoreService.getSavedPreferences()
                ?: DataStorePreferences("", ArraySet())
            val number = preferences.phoneNumber
            if (number != "") {
                setContentView(R.layout.activity_chats)
                stompClientService.connectAndSubscribe()
                initializeChatList(preferences.chats)
            } else {
                setContentView(R.layout.activity_main)
            }
        }
    }

    override fun onStop() {
        stompClientService.disconnect()
        //runBlocking { clientDataStoreService.clear() }
        super.onStop()
        scope.cancel()
    }

    fun subscribeHandler(v: View) {
        val phoneNumber: EditText = findViewById(R.id.editTextPhone)
        val phoneNumberString: String = phoneNumber.text.toString()
        val id = UUID.randomUUID().toString()
        scope.launch {
            clientDataStoreService.setRegisteredNumber(phoneNumberString)
        }
        Log.i("Subscribe", "Subscribe $phoneNumberString, id: $id")
        restClientService.register(UserWithPreKey(id, phoneNumberString, "DUMMY")) {
            setContentView(R.layout.activity_chats)
        }
    }

    private fun initializeChatList(chats: Set<String>) {
        Log.i("ChatList", "initialization")
        val customAdapter = ChatsListAdapter(chats.toTypedArray(), this)

        val recyclerView: RecyclerView = findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }
}