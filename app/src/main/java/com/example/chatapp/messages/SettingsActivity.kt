package com.example.chatapp.messages

import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.messaging.FirebaseMessaging
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnFastChooseColorListener


lateinit var switch: SwitchMaterial
lateinit var changeColor: View

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = "Settings"
        setUp()
    }

    private fun setUp() {
        switch = findViewById(R.id.notification_switch_activity_settings)
        changeColor = findViewById(R.id.div_container_change_color_settings)
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
                // bazashi feris atana


                // returns to ChatLogActivity
                finish()
            }

            override fun onCancel() {
            }
        })
            .setColumns(5)
            .show()
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