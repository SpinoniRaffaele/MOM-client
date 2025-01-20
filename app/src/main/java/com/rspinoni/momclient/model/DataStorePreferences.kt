package com.rspinoni.momclient.model

import java.util.stream.Collectors

class DataStorePreferences(var phoneNumber: String, var deviceId: String, var chats: Set<Chat>) {

    companion object {
        private val CHAT_SEPARATOR = "_%&*&%_"

        operator fun invoke(phoneNumber: String, deviceId: String, chats: Set<String>) =
            DataStorePreferences(phoneNumber, deviceId, deserializeChats(chats))

        fun deserializeChats(chats: Set<String>): Set<Chat> {
            return chats.stream()
                .map { chat -> deserializeChat(chat) }
                .collect(Collectors.toSet())
        }

        fun deserializeChat(chat: String): Chat {
            return Chat(chat.split(CHAT_SEPARATOR)[0], chat.split(CHAT_SEPARATOR)[1])
        }

        fun serializeChats(chats: Set<Chat>): Set<String> {
            return chats.stream()
                .map { chat -> serializeChat(chat) }
                .collect(Collectors.toSet())
        }

        fun serializeChat(chat: Chat): String {
            return "${chat.phoneNumber}$CHAT_SEPARATOR${chat.name}"
        }
    }
}