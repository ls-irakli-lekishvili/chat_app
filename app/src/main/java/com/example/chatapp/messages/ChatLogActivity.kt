package com.example.chatapp.messages

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.application.RetrofitInstance
import com.example.chatapp.messages.LatestMessageActivity.Companion.currentUsersName
import com.example.chatapp.messages.LatestMessageActivity.Companion.headerStart
import com.example.chatapp.messages.NewMessageActivity.Companion.USER_KEY
import com.example.chatapp.models.ChatMessage
import com.example.chatapp.models.NotificationData
import com.example.chatapp.models.PushNotification
import com.example.chatapp.models.User
import com.example.chatapp.views.ChatFromItem
import com.example.chatapp.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null
    lateinit var recyclerView: RecyclerView
    lateinit var textView: TextView
    lateinit var text: String
    lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        setup()
        // useris setingebidan feris wamogeba da gadawodeba
        setChatColor(Color.GREEN)
        listenForMessages()
    }

    private fun setup() {
        supportActionBar?.title = toUser?.username
        toUser = intent.getParcelableExtra(USER_KEY)

        sendButton = findViewById(R.id.send_button_chat_log)
        recyclerView = findViewById(R.id.recyclerview_chat_log)
        textView = findViewById<EditText>(R.id.edittext_chat_log)

        recyclerView.adapter = adapter
        addListener()
    }

    private fun addListener() {
        sendButton.setOnClickListener {
            text = textView.text.toString().trim()
            if (text.isNotBlank()) {
                setupNotification()
                performSendMessage()
            }
        }
    }

    private fun setChatColor(color: Int) {
        recyclerView.setBackgroundColor(color)
    }

    private fun setupNotification() {
        val title = "new message from $currentUsersName"
        val message = text
        val topic = "${headerStart}${toUser?.uid}"
        if (title.isNotEmpty() && message.isNotEmpty()) {
            PushNotification(
                NotificationData(title, message),
                topic
            ).also {
                sendNotification(it)
            }
        }
    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessageActivity.currentUser ?: return
                        adapter.add(ChatToItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatFromItem(chatMessage.text, toUser!!))
                    }
                }
                recyclerView.scrollToPosition(adapter.itemCount - 1)

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
        val text = findViewById<EditText>(R.id.edittext_chat_log).text.toString()

        val fromId = FirebaseAuth.getInstance().uid ?: return
        val toId = user.uid

        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage =
            ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                findViewById<EditText>(R.id.edittext_chat_log).text.clear()
                findViewById<RecyclerView>(R.id.recyclerview_chat_log).scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)

        val latestMessageReference =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageReference.setValue(chatMessage)
        val latestMessageToReference =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToReference.setValue(chatMessage)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.setting, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d("ChatLog", Gson().toJson(response))
            } else {
                Log.d("ChatLog", response.errorBody().toString())
            }
        } catch (e: Exception) {
            Log.e("ChatLog", e.toString())
        }
    }
}