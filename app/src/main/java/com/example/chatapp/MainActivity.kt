package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        performRegister()

        val alreadyHaveAccountBtn = findViewById<TextView>(R.id.already_have_account_text_view).setOnClickListener {
            Log.d("main", "try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performRegister() {
        val registerBtn = findViewById<Button>(R.id.register_button_register).setOnClickListener {
            val email = findViewById<EditText>(R.id.email_editText_registration).text.toString()
            val password = findViewById<EditText>(R.id.password_editText_registration).text.toString()

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // if successfull
                    Log.d("main", "Logged in ${it.result?.user?.uid}")
                }
                .addOnFailureListener {
                    Log.d("main", "Failure ${it.message}")
                    Toast.makeText(this, "Failure register", Toast.LENGTH_LONG).show()
                }
        }

    }


}