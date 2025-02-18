package com.rspinoni.momclient.di

import com.rspinoni.momclient.ChatActivity
import com.rspinoni.momclient.MainActivity
import dagger.Component
import jakarta.inject.Singleton

@Component
@Singleton
interface ApplicationComponent {
    fun injectMainActivity(mainActivity: MainActivity)
    fun injectChatActivity(chatActivity: ChatActivity)
}