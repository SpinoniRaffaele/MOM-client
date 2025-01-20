package com.rspinoni.momclient

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.ArraySet
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rspinoni.momclient.adapters.ChatsListAdapter
import com.rspinoni.momclient.model.Chat
import com.rspinoni.momclient.model.DataStorePreferences
import com.rspinoni.momclient.model.Message
import com.rspinoni.momclient.model.User
import com.rspinoni.momclient.model.UserWithPreKey
import com.rspinoni.momclient.rest.RestClientService
import com.rspinoni.momclient.stomp.StompClientService
import com.rspinoni.momclient.storage.ClientDataStoreService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.ArrayList
import java.util.UUID

const val BUNDLE_NAME_KEY: String = "name_"
const val BUNDLE_NUMBER_KEY: String = "number_"

class MainActivity : AppCompatActivity() {
    //to find the localhost IP use `ipconfig` and check the IPv4 of the connection you have
    private val domain: String = "192.168.241.206:8080"
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
            val resultName: String? = intent?.getStringExtra(BUNDLE_NAME_KEY)
            val resultNumber: String? = intent?.getStringExtra(BUNDLE_NUMBER_KEY)
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
            val preferences = clientDataStoreService.getSavedPreferences()!!
            val number = preferences.phoneNumber
            if (number != "") {
                initRegisteredUserChatList(preferences)
            } else {
                setContentView(R.layout.activity_main_register)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        stompClientService.disconnect()
    }

    private fun initRegisteredUserChatList(preferences: DataStorePreferences) {
        setContentView(R.layout.activity_main_chats)
        //stompClientService.connectAndSubscribe()
        restClientService.connect(User(preferences.deviceId, preferences.phoneNumber),
            { unreadMessages: List<Message> -> run {
                //todo: store locally the unread messages
                val chatList: MutableList<Chat> = ArrayList()
                preferences.chats.forEach { chat: Chat ->
                    val numOfUnreadMessages = unreadMessages.count { message ->
                        message.sendersPhoneNumber == chat.phoneNumber
                    }
                    chatList.add(Chat(chat.phoneNumber, chat.name, numOfUnreadMessages))
                }
                initializeChatList(chatList.toSet())
            }
        })
    }

    fun subscribeHandler(v: View) {
        val phoneNumber: EditText = findViewById(R.id.editTextPhone)
        val phoneNumberString: String = phoneNumber.text.toString()
        val id = UUID.randomUUID().toString()
        Log.i("Subscribe", "Subscribe $phoneNumberString, id: $id")
        restClientService.register(
            UserWithPreKey("DUMMY", id, phoneNumberString), {
                scope.launch {
                    clientDataStoreService.setRegisteredNumber(phoneNumberString)
                    clientDataStoreService.setDeviceId(id)
                }
                initRegisteredUserChatList(DataStorePreferences(phoneNumberString, id, ArraySet()))
            }) {
            Toast.makeText(
                this, "Error communicating with the server", Toast.LENGTH_SHORT).show()
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

    fun openChatHandler(v: View) {
        val openingChat = Chat(
            v.findViewById<TextView>(R.id.chat_item_text_number).text.toString(),
            v.findViewById<TextView>(R.id.chat_item_text_name).text.toString()
        )
        val data = Intent(this, ChatActivity::class.java)
        data.putExtra(BUNDLE_NAME_KEY, openingChat.name)
        data.putExtra(BUNDLE_NUMBER_KEY, openingChat.phoneNumber)
        startActivity(data)
    }

    private fun initializeChatList(chats: Set<Chat>) {
        Log.i("ChatList", "initialization")
        val customAdapter = ChatsListAdapter(chats.toTypedArray(), this)

        val recyclerView: RecyclerView = findViewById(R.id.chat_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }
}