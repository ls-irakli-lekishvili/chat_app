package com.example.chatapp.views

import android.widget.ImageView
import android.widget.TextView
import com.example.chatapp.R
import com.example.chatapp.models.ChatMessage
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class LatestMessageRow(private val chatMessage: ChatMessage): Item<ViewHolder>() {
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val view = viewHolder.itemView.findViewById<TextView>(R.id.message_textview_latest_message)
        view.text = chatMessage.text
        val chatPartnerId: String = if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
            chatMessage.toId
        } else {
            chatMessage.fromId
        }

        val reference = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        reference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatPartnerUser = snapshot.getValue(User::class.java)
                viewHolder.itemView.findViewById<TextView>(R.id.username_textview_latest_message).text = chatPartnerUser?.username
                val profilePic = viewHolder.itemView.findViewById<ImageView>(R.id.imageview_latest_message)
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(profilePic)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}