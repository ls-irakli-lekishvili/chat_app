package com.example.chatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Select User"

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_new_message)

        val adapter = GroupAdapter<ViewHolder>()

        recyclerView.adapter = adapter
    }

    class UserItem: Item<ViewHolder>() {
        override fun bind(viewHolder: ViewHolder, position: Int) {

        }

        override fun getLayout(): Int {
            return R.layout.user_row_new_message
        }

    }


}