package com.example.chatapp.auth

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import com.example.chatapp.R
import com.example.chatapp.extensions.validEmail
import com.example.chatapp.messages.LatestMessageActivity
import com.example.chatapp.models.User
import com.example.chatapp.dialogs.ProgressBarDialog
import com.example.chatapp.dto.SignUpDto
import com.example.chatapp.view_model.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val authViewModel: AuthViewModel = AuthViewModel()
    var selectedPhotoUri: Uri? = null
    lateinit var myProgressBar: ProgressBarDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        myProgressBar = ProgressBarDialog(this)

        performRegister()

        findViewById<TextView>(R.id.already_have_account_text_view).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.selectphoto_button_register).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            findViewById<CircleImageView>(R.id.selectphoto_imageview_register).setImageBitmap(bitmap)
            findViewById<Button>(R.id.selectphoto_button_register).alpha = 0f
        }
    }

    private fun performRegister() {
        findViewById<Button>(R.id.register_button_register).setOnClickListener {
            val username = findViewById<EditText>(R.id.username_editText_registration).text.toString()
            val email = findViewById<EditText>(R.id.email_editText_registration).text.toString()
            val password = findViewById<EditText>(R.id.password_editText_registration).text.toString()

            if (!validateLogin(username, email, password)) {
                return@setOnClickListener
            }

            myProgressBar.show()

            authViewModel.signUp(SignUpDto(email, password))
            authViewModel.signUpLiveData.observe(
                this,
                {
                    if(it.success) {
                        uploadImageToFirebaseStorage()
                    } else {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        myProgressBar.dismiss()
                    }
                }
            )
        }
    }

    private fun validateLogin(username: String, email: String, password: String): Boolean {
        if (username.isBlank()) {
            Toast.makeText(this, "Username Shouldn't Be Blank", Toast.LENGTH_LONG).show()
            return false
        }
        if (!validEmail(email)) {
            Toast.makeText(this, "Email Should Be Valid", Toast.LENGTH_LONG).show()
            return false
        }
        if (password.length <= 5) {
            Toast.makeText(this, "Password Should Be More Than 6 Characters", Toast.LENGTH_LONG)
                .show()
            return false
        }

        if (selectedPhotoUri == null) {
            Toast.makeText(this, "Please Select Profile Image", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener { message ->
                Log.d("register", "Successfully uploaded image: ${message.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("register", "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("register", "Failed to upload image to storage: ${it.message}")
                myProgressBar.dismiss()
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val username = findViewById<EditText>(R.id.username_editText_registration).text.toString()

        val user = User(uid, username, profileImageUrl, true, Color.GREEN)

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