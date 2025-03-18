package com.rspinoni.momclient

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rspinoni.momclient.adapters.MessagesListAdapter
import com.rspinoni.momclient.di.BUNDLE_NAME_KEY
import com.rspinoni.momclient.di.BUNDLE_NUMBER_KEY
import com.rspinoni.momclient.di.DI
import com.rspinoni.momclient.model.Chat
import com.rspinoni.momclient.model.Message
import com.rspinoni.momclient.rest.RestClientService
import com.rspinoni.momclient.storage.UnreadMessagesService
import jakarta.inject.Inject

class ChatActivity : AppCompatActivity() {
    @Inject lateinit var restClientService: RestClientService
    @Inject lateinit var unreadMessagesService: UnreadMessagesService
    private lateinit var chat: Chat
    private lateinit var unreadMessages: List<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        DI.injectChatActivity(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val number = intent.getStringExtra(BUNDLE_NUMBER_KEY)
        val name = intent.getStringExtra(BUNDLE_NAME_KEY)
        if (!(name is String && number is String)) {
            Log.e("ChatActivity", "invalid input data for the opened chat $name $number")
            finish()
        }
        chat = Chat(number!!, name!!)
        Log.i("ChatActivity", "Opening chat: ${chat.name} ${chat.phoneNumber}")

        unreadMessages = unreadMessagesService.getUnreadMessagesFromSender(chat.phoneNumber)
        initializeMessageList(unreadMessages)

        //todo send an update to the server to remove the unreadMessages
    }

    private fun initializeMessageList(messages: List<Message>) {
        val customAdapter = MessagesListAdapter(messages.toTypedArray(), this, chat.phoneNumber)
        val recyclerView: RecyclerView = findViewById(R.id.messages_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }
}