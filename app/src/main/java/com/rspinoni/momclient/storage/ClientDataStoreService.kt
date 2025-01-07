package com.rspinoni.momclient.storage

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mom-data")

class ClientDataStoreService(private val context: Context) {
    private val REGISTERED_NUMBER_KEY = stringPreferencesKey("registered-number")

    suspend fun getRegisteredNumber(): String? {
        Log.i("Data", "Retrieving registered number")
        val registeredNumberFlow: Flow<String> = context.dataStore.data.map { data: Preferences ->
            data[REGISTERED_NUMBER_KEY] ?: ""
        }
        val registeredNumber = registeredNumberFlow.firstOrNull()
        Log.i("Data", "Retrieved registered number: $registeredNumber")
        return registeredNumber
    }

    suspend fun setRegisteredNumber(registeredNumber: String) {
        context.dataStore.edit { data ->
            data[REGISTERED_NUMBER_KEY] = registeredNumber
        }
        Log.i("Data", "Saved registered number $registeredNumber")
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
        Log.i("Data", "Destroyed")
    }
}
