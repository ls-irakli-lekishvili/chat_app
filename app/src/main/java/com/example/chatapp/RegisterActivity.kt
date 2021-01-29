package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RegisterActivity : AppCompatActivity() {
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        performRegister()

        findViewById<TextView>(R.id.already_have_account_text_view).setOnClickListener {
            Log.d("main", "try to show login activity")

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.selectphoto_button_register).setOnClickListener {
            Log.d("Main", "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d("register", "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            findViewById<CircleImageView>(R.id.selectphoto_imageview_register).setImageBitmap(bitmap)
            findViewById<Button>(R.id.selectphoto_button_register).alpha = 0f
        }
    }

    private fun performRegister() {
            findViewById<Button>(R.id.register_button_register).setOnClickListener {
            val email = findViewById<EditText>(R.id.email_editText_registration).text.toString()
            val password = findViewById<EditText>(R.id.password_editText_registration).text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                Log.d("register", "Attempting to create user with email: $email")

                // Firebase Authentication to create a user with email and password
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener

                        // else if successful
                        Log.d("register", "Successfully created user with uid: ${it.result?.user?.uid}")

                        uploadImageToFirebaseStorage()
                    }
                    .addOnFailureListener{
                        Log.d("register", "Failed to create user: ${it.message}")
                        Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
        }

    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("register", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("register", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("register", "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val username = findViewById<EditText>(R.id.username_editText_registration).text.toString()

        val user = User(uid, username, profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d("main", "Finally we saved the user to Firebase Database")
                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("main", "Failed to set value to database: ${it.message}")
            }
    }


}

class User(val uid: String, val username: String, val profileImageUrl: String)