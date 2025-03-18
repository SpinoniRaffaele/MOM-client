package com.rspinoni.momclient.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rspinoni.momclient.R
import com.rspinoni.momclient.model.Message

class MessagesListAdapter(private val dataSet: Array<Message>, private val context: Context,
                          private val chatPhoneNumber: String) :
    RecyclerView.Adapter<MessagesListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val containerView: LinearLayout = view.findViewById(R.id.message_item_container)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.message_list_element, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val content: TextView = viewHolder.containerView
            .findViewById(R.id.message_item_content)
        content.text = dataSet[position].content
        content.textAlignment = if (isReceived(dataSet[position])) View.TEXT_ALIGNMENT_TEXT_END
                else View.TEXT_ALIGNMENT_TEXT_START
    }

    override fun getItemCount() = dataSet.size

    private fun isReceived(message: Message): Boolean =
        message.sendersPhoneNumber == chatPhoneNumber
}