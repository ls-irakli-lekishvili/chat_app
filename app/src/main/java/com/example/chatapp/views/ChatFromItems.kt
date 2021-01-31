package com.example.chatapp.views

import android.widget.ImageView
import android.widget.TextView
import com.example.chatapp.R
import com.example.chatapp.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatFromItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val fromText = viewHolder.itemView.findViewById<TextView>(R.id.textview_chat_from)
        fromText.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.findViewById<ImageView>(R.id.imageview_chat_from)
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}