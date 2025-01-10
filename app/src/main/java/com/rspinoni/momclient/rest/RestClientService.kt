package com.rspinoni.momclient.rest

import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.rspinoni.momclient.MainActivity
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

    private val mapper: ObjectMapper = ObjectMapper()

    fun register(user: UserWithPreKey, onResponse: (response: Response)->Unit) {
        val request: Request = Request.Builder().url("$httpServerUrl/authorization/register")
            .post(mapper.writeValueAsString(user).toRequestBody(mediaType))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HttpClient", e.stackTraceToString())
            }
            override fun onResponse(call: Call, response: Response) {
                Log.i("HttpClient", response.toString())
                context.runOnUiThread {
                    onResponse.invoke(response)
                }
            }
        })
    }
}