package com.example.chatapp.messages

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.User
import com.example.chatapp.views.UserItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"
        fetchUsers()
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_new_message)
                val adapter = GroupAdapter<ViewHolder>()
                recyclerView.adapter = adapter

                val authedUserUid = FirebaseAuth.getInstance().uid
                snapshot.children.forEach { entity ->
                    val user = entity.getValue(User::class.java)
                    user?.let {
                        if (it.uid != authedUserUid)
                            adapter.add(UserItem(it))
                    }
                }

                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    companion object {
        val USER_KEY = "USER_KEY"
    }
}