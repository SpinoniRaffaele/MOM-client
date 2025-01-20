package com.rspinoni.momclient.rest

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.rspinoni.momclient.MainActivity
import com.rspinoni.momclient.model.Message
import com.rspinoni.momclient.model.User
import com.rspinoni.momclient.model.UserWithPreKey
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException

class RestClientService(private val httpServerUrl: String, private val context: MainActivity) {

    private val mediaType: MediaType = "application/json".toMediaType()

    private val client: OkHttpClient = OkHttpClient()

    private val mapper: ObjectMapper = jacksonObjectMapper()

    fun register(
            user: UserWithPreKey, onResponse: (response: Response) -> Unit = {},
        onFailure: (e: Exception) -> Unit = {},
    ) {
        val request: Request = Request.Builder().url("$httpServerUrl/authorization/register")
            .post(mapper.writeValueAsString(user).toRequestBody(mediaType))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", e.stackTraceToString())
                context.runOnUiThread { onFailure.invoke(e) }
            }
            override fun onResponse(call: Call, response: Response) {
                Log.i("HttpClient", response.toString())
                context.runOnUiThread { onResponse.invoke(response) }
            }
        })
    }

    fun connect(
        user: User, onResponse: (response: List<Message>) -> Unit = {},
        onFailure: (e: Exception) -> Unit = {},
    ) {
        val request: Request = Request.Builder().url("$httpServerUrl/authorization/connect")
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
}