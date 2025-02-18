package com.rspinoni.momclient

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.rspinoni.momclient.di.BUNDLE_NAME_KEY
import com.rspinoni.momclient.di.BUNDLE_NUMBER_KEY

class NewChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Activity", "Creating NewChatActivity")
        setContentView(R.layout.activity_new_chat)
    }

    fun addChatSubmitHandler(v: View) {
        val number: String = findViewById<EditText>(R.id.new_chat_number)?.text.toString()
        val name: String = findViewById<EditText>(R.id.new_chat_name)?.text.toString()
        val data = Intent()
        data.putExtra(BUNDLE_NAME_KEY, name)
        data.putExtra(BUNDLE_NUMBER_KEY, number)
        setResult(RESULT_OK, data)
        finish()
    }
}