package com.rspinoni.momclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rspinoni.momclient.R
import com.rspinoni.momclient.model.Chat

class ChatsListAdapter(private val dataSet: Array<Chat>, private val context: Context) :
    RecyclerView.Adapter<ChatsListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.chat_item_text)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.chat_element, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = "${dataSet[position].name}   ${dataSet[position].phoneNumber}"
    }

    override fun getItemCount() = dataSet.size
}