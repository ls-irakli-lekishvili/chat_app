package com.example.chatapp

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
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

                snapshot.children.forEach { entity ->
                    val user = entity.getValue(User::class.java)
                    user?.let {
                        adapter.add(UserItem(it))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    class UserItem(private val user: User): Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {
            viewHolder.itemView.findViewById<TextView>(R.id.username_text_view_new_message).text = user.username
            val imageContainer: ImageView = viewHolder.itemView.findViewById(R.id.image_view_new_message)
            Picasso.get().load(user.profileImageUrl).into(imageContainer)
        }
        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }

    }


}