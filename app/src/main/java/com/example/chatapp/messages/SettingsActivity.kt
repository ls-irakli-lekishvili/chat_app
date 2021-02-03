package com.example.chatapp.messages

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = "Settings"
    }
}