package com.example.chatapp.views

import android.widget.TextView
import com.example.chatapp.R
import com.example.chatapp.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatToItem(val text: String, val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val toText = viewHolder.itemView.findViewById<TextView>(R.id.textview_chat_to)
        toText.text = text

        // load our user image into the star
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageview_chat_to
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}