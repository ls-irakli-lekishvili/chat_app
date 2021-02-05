package com.example.chatapp.messages

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.messages.LatestMessageActivity.Companion.chatColor
import com.example.chatapp.messages.LatestMessageActivity.Companion.currentUser
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnFastChooseColorListener


lateinit var switch: SwitchMaterial
lateinit var changeColor: View
lateinit var ref: DatabaseReference

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setUp()
    }

    private fun setUp() {
        supportActionBar?.title = "Settings"

        switch = findViewById(R.id.notification_switch_activity_settings)
        changeColor = findViewById(R.id.div_container_change_color_settings)

        ref = FirebaseDatabase.getInstance().reference
        addEvent()
    }

    private fun addEvent() {
        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                FirebaseMessaging.getInstance()
                    .subscribeToTopic(LatestMessageActivity.subscribedTopic)

            } else {
                FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(LatestMessageActivity.subscribedTopic)
            }
            updateNotificationStatus(isChecked)
        }

        changeColor.setOnClickListener {
            drawColorPicker()
        }
    }

    private fun drawColorPicker() {
        val colorPicker = ColorPicker(this)
        colorPicker.setColors(
            Color.RED, Color.GREEN, Color.BLUE,
            Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.LTGRAY
        )
        colorPicker.setRoundColorButton(true)
        colorPicker.setOnFastChooseColorListener(object : OnFastChooseColorListener {
            override fun setOnFastChooseColorListener(position: Int, color: Int) {
                chatColor = color
                updateChatColorToDB(color)
                finish()
            }

            override fun onCancel() {
            }
        })
            .setColumns(5)
            .show()
    }

    private fun updateNotificationStatus(isNotificationActive: Boolean) {
        ref.child("/users/${currentUser?.uid}").child("notification").setValue(isNotificationActive)
    }

    private fun updateChatColorToDB(color: Int) {
        ref.child("/users/${currentUser?.uid}").child("color").setValue(color)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}