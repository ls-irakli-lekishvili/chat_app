package com.example.chatapp.views

import android.widget.ImageView
import android.widget.TextView
import com.example.chatapp.R
import com.example.chatapp.models.User
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.username_text_view_new_message).text = user.username
        val imageContainer: ImageView = viewHolder.itemView.findViewById(R.id.image_view_new_message)
        Picasso.get().load(user.profileImageUrl).into(imageContainer)
    }
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

}