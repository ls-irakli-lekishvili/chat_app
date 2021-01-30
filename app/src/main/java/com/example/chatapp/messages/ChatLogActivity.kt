package com.example.chatapp.messages

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.messages.NewMessageActivity.Companion.USER_KEY
import com.example.chatapp.models.ChatMessage
import com.example.chatapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class ChatLogActivity: AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_chat_log)
        recyclerView.adapter = adapter

        val user = intent.getParcelableExtra<User>(USER_KEY) as User
        supportActionBar?.title = user.username

        listenForMessages()

        findViewById<Button>(R.id.send_button_chat_log).setOnClickListener {
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val reference = FirebaseDatabase.getInstance().getReference("/messages")
        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)
                chatMessage?.let {
                    if (it.fromId == FirebaseAuth.getInstance().uid) {
                        adapter.add(ChatFromItem(it.text))
                    } else {
                        adapter.add(ChatToItem(it.text))
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun performSendMessage() {
        val user = intent.getParcelableExtra<User>(USER_KEY) as User

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        val fromId = FirebaseAuth.getInstance().uid ?: return

        val toId = user.uid
        val text = findViewById<EditText>(R.id.edittext_chat_log).text.toString()
        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d("uniquetag", "savedtodatabase")
            }
    }

}

class ChatFromItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val fromText = viewHolder.itemView.findViewById<TextView>(R.id.textview_chat_from)
        fromText.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChatToItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        val toText = viewHolder.itemView.findViewById<TextView>(R.id.textview_chat_to)
        toText.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}