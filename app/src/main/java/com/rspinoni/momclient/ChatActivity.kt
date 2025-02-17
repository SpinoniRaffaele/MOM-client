package com.rspinoni.momclient

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rspinoni.momclient.di.BUNDLE_NAME_KEY
import com.rspinoni.momclient.di.BUNDLE_NUMBER_KEY
import com.rspinoni.momclient.di.DI
import com.rspinoni.momclient.model.Chat
import com.rspinoni.momclient.rest.RestClientService
import jakarta.inject.Inject

class ChatActivity : AppCompatActivity() {
    @Inject lateinit var restClientService: RestClientService

    private lateinit var chat: Chat

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
    }
}