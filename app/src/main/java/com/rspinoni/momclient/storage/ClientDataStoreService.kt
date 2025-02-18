package com.rspinoni.momclient.storage

import android.content.Context
import android.util.ArraySet
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rspinoni.momclient.model.Chat
import com.rspinoni.momclient.model.DataStorePreferences
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mom-data")

@Singleton
class ClientDataStoreService @Inject constructor() {
    private val REGISTERED_NUMBER_KEY = stringPreferencesKey("registered-number")
    private val DEVICE_ID_KEY = stringPreferencesKey("device-id")
    private val CHATS_KEY = stringSetPreferencesKey("chats")
    lateinit var context: Context

    suspend fun getSavedPreferences(): DataStorePreferences? {
        val preferencesFlow: Flow<DataStorePreferences> = context.dataStore.data.map { data: Preferences ->
            DataStorePreferences.invoke(
                data[REGISTERED_NUMBER_KEY] ?: "",
                data[DEVICE_ID_KEY] ?: "",
                data[CHATS_KEY] ?: ArraySet()
            )
        }
        val preferences = preferencesFlow.firstOrNull()
        Log.i("Data", "Retrieved preferences: ${preferences?.phoneNumber}")
        return preferences
    }

    suspend fun setRegisteredNumber(registeredNumber: String) {
        context.dataStore.edit { data ->
            data[REGISTERED_NUMBER_KEY] = registeredNumber
        }
        Log.i("Data", "Saved registered number $registeredNumber")
    }

    suspend fun setDeviceId(deviceId: String) {
        context.dataStore.edit { data ->
            data[DEVICE_ID_KEY] = deviceId
        }
        Log.i("Data", "Saved device id $deviceId")
    }

    suspend fun setNewChat(chat: Chat): Set<Chat> {
        val serializedChat: String = DataStorePreferences.serializeChat(chat)
        var result: Set<Chat> = ArraySet()
        context.dataStore.edit {data ->
            data[CHATS_KEY] = (data[CHATS_KEY] ?: ArraySet()).plus(serializedChat)
            result = DataStorePreferences.deserializeChats(data[CHATS_KEY] ?: ArraySet())
        }
        return result
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
        Log.i("Data", "Destroyed")
    }
}
