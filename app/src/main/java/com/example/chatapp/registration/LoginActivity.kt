package com.example.chatapp.registration

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.extensions.validEmail
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        findViewById<Button>(R.id.login_button_login).setOnClickListener {
            performLogin()
        }

        findViewById<TextView>(R.id.back_to_register_textview).setOnClickListener{
            finish()
        }
    }

    private fun performLogin() {
        val email = findViewById<EditText>(R.id.email_editText_login).text.toString()
        val password = findViewById<EditText>(R.id.password_editText_login).text.toString()

        if (!validateEmailAndPassword(email, password)) {
            return
        }

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Log.d("Login", "Successfully logged in: ${it.result?.user?.uid}")
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }

    private fun validateEmailAndPassword(email: String, password: String): Boolean {
        if(!validEmail(email)) {
            Toast.makeText(this, "Wrong Email Format", Toast.LENGTH_LONG).show()
            return false
        }

        if(password.length <= 5) {
            Toast.makeText(this, "Wrong Password Format", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }
}
