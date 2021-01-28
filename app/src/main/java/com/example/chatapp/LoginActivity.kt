package com.example.chatapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        findViewById<Button>(R.id.login_button_login).setOnClickListener {
            val email = findViewById<EditText>(R.id.email_editText_login).text.toString()
            val password = findViewById<EditText>(R.id.password_editText_login).text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
//          .addOnCompleteListener()
//          .add

        }
    }
}