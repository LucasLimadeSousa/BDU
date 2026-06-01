package com.example.bdu.livros

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bdu.R
import com.example.bdu.model.ChatMessage
import com.example.bdu.model.Sender
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == Sender.USER) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_chat_bot, parent, false)
            BotViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is UserViewHolder) holder.bind(message)
        else if (holder is BotViewHolder) holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textMsg: TextView = view.findViewById(R.id.tv_message_text)
        private val timeMsg: TextView = view.findViewById(R.id.tv_message_time)

        fun bind(message: ChatMessage) {
            textMsg.text = message.text
            timeMsg.text = formatTime(message.timestamp)
        }
    }

    inner class BotViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textMsg: TextView = view.findViewById(R.id.tv_message_text)
        private val timeMsg: TextView = view.findViewById(R.id.tv_message_time)

        fun bind(message: ChatMessage) {
            textMsg.text = message.text
            timeMsg.text = formatTime(message.timestamp)
        }
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}
