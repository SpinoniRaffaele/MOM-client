package com.rspinoni.momclient.storage

import com.rspinoni.momclient.model.Message
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class UnreadMessagesService @Inject constructor() {

    private var unreadMessages: List<Message> = ArrayList()

    fun setUnreadMessages(unreadMessages: List<Message>) {
        this.unreadMessages = unreadMessages
    }

    fun getUnreadMessagesFromSender(senderPhoneNumber: String): List<Message> {
        return unreadMessages.filter { unreadMessage ->
            unreadMessage.sendersPhoneNumber == senderPhoneNumber }
    }
}
