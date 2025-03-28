package com.rspinoni.momclient.rest

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rspinoni.momclient.MainActivity
import com.rspinoni.momclient.di.HTTP_SERVER_URL
import com.rspinoni.momclient.model.Message
import com.rspinoni.momclient.model.User
import jakarta.inject.Inject
import jakarta.inject.Singleton
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

@Singleton
class RestClientService @Inject constructor() {

    private val mediaType: MediaType = "application/json".toMediaType()

    private val client: OkHttpClient = OkHttpClient()

    private val mapper: ObjectMapper = jacksonObjectMapper()

    lateinit var context: MainActivity

    fun register(
            user: User,
            onResponse: (response: User) -> Unit = {},
            onFailure: (e: Exception) -> Unit = {},
    ) {
        val request: Request = Request.Builder().url("$HTTP_SERVER_URL/authorization/register")
            .post(mapper.writeValueAsString(user).toRequestBody(mediaType))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", e.stackTraceToString())
                context.runOnUiThread { onFailure.invoke(e) }
            }
            override fun onResponse(call: Call, response: Response) {
                Log.i("HttpClient", response.toString())
                context.runOnUiThread {
                    onResponse.invoke(
                        mapper.readValue(response.body?.string() ?: "{}", User::class.java))
                }
            }
        })
    }

    fun connect(
        user: User, onResponse: (response: List<Message>) -> Unit = {},
        onFailure: (e: Exception) -> Unit = {},
    ) {
        val request: Request = Request.Builder().url("$HTTP_SERVER_URL/authorization/connect")
            .post(mapper.writeValueAsString(user).toRequestBody(mediaType))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", e.stackTraceToString())
                context.runOnUiThread { onFailure.invoke(e) }
            }
            override fun onResponse(call: Call, response: Response) {
                Log.i("HttpClient", response.toString())
                val messages: List<Message> = mapper.readValue(response.body?.string() ?: "[]")
                context.runOnUiThread { onResponse.invoke(messages) }
            }
        })
    }

    fun deleteMessagesFromSender(user: User, senderPhoneNumber: String,
                                 onResponse: () -> Unit = {},
                                 onFailure: (e: Exception) -> Unit = {}) {
        val request: Request = Request.Builder()
            .url("$HTTP_SERVER_URL/messages/${senderPhoneNumber}").delete()
            .headers(Headers.headersOf(
                "deviceId", user.deviceId,
                "phoneNumber", user.phoneNumber))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", e.stackTraceToString())
                context.runOnUiThread { onFailure.invoke(e) }
            }
            override fun onResponse(call: Call, response: Response) {
                Log.i("HttpClient", response.toString())
                context.runOnUiThread { onResponse.invoke() }
            }
        })
    }
}