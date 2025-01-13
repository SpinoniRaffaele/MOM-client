package com.rspinoni.momclient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.ArraySet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rspinoni.momclient.adapters.ChatsListAdapter
import com.rspinoni.momclient.model.Chat
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
    private val domain: String = "172.29.64.1:8080"
    private val httpServerUrl: String = "http://$domain"
    private val websocketUrl: String = "ws://$domain/websocket"
    private val subscriptionPath: String = "/topic/notifications"
    private val stompClientService: StompClientService = StompClientService(websocketUrl, subscriptionPath)
    private val restClientService: RestClientService = RestClientService(httpServerUrl, this)
    private val clientDataStoreService: ClientDataStoreService = ClientDataStoreService(this)
    private val scope = MainScope()
    private val getNewChatInfo = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent: Intent? = result.data
            val resultName: String? = intent?.getStringExtra(RESULT_NAME_KEY)
            val resultNumber: String? = intent?.getStringExtra(RESULT_NUMBER_KEY)
            if (resultName is String && resultNumber is String) {
                Log.i("ActivityResult", "is string ok")
                scope.launch {
                    val updatedChats = clientDataStoreService.setNewChat(Chat(resultNumber, resultName))
                    initializeChatList(updatedChats)
                }
            }
            Log.i("ActivityResult", "$resultName - $resultNumber")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope.launch {
            Log.i("MainActivity", "onStart")
            val preferences = clientDataStoreService.getSavedPreferences()
                ?: DataStorePreferences("", ArraySet())
            val number = preferences.phoneNumber
            if (number != "") {
                setContentView(R.layout.activity_main_chats)
                stompClientService.connectAndSubscribe()
                initializeChatList(preferences.chats)
            } else {
                setContentView(R.layout.activity_main_register)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        stompClientService.disconnect()
    }

    fun subscribeHandler(v: View) {
        val phoneNumber: EditText = findViewById(R.id.editTextPhone)
        val phoneNumberString: String = phoneNumber.text.toString()
        val id = UUID.randomUUID().toString()
        Log.i("Subscribe", "Subscribe $phoneNumberString, id: $id")
        restClientService.register(UserWithPreKey(id, phoneNumberString, "DUMMY")) {
            setContentView(R.layout.activity_main_chats)
            scope.launch {
                clientDataStoreService.setRegisteredNumber(phoneNumberString)
            }
        }
    }

    fun newChatHandler(v: View) {
        getNewChatInfo.launch(Intent(this, NewChatActivity::class.java))
    }

    fun deleteStorageHandler(v: View) {
        scope.launch {
            clientDataStoreService.clear()
            Toast.makeText(baseContext, "Storage cleaned", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeChatList(chats: Set<Chat>) {
        Log.i("ChatList", "initialization")
        val customAdapter = ChatsListAdapter(chats.toTypedArray(), this)

        val recyclerView: RecyclerView = findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }
}