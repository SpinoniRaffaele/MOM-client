package com.rspinoni.momclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rspinoni.momclient.R
import com.rspinoni.momclient.model.Chat
import java.util.Locale

class ChatsListAdapter(private val dataSet: Array<Chat>, private val context: Context) :
    RecyclerView.Adapter<ChatsListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val containerView: LinearLayout = view.findViewById(R.id.chat_item_container)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.chat_list_element, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val nameTextView: TextView = viewHolder.containerView
            .findViewById(R.id.chat_item_text_name)
        val numberTextView: TextView = viewHolder.containerView
            .findViewById(R.id.chat_item_text_number)
        val unreadMessage: TextView = viewHolder.containerView
            .findViewById(R.id.chat_item_unread_messages_number)
        nameTextView.text = dataSet[position].name
        numberTextView.text = dataSet[position].phoneNumber
        unreadMessage.text = String.format(
            Locale.getDefault(), "%d", dataSet[position].unreadMessages)
    }

    override fun getItemCount() = dataSet.size
}