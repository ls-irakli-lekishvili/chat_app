package com.example.chatapp.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.dto.SignUpResponse
import com.example.chatapp.dto.SignInDto
import com.example.chatapp.dto.SignInResponse
import com.example.chatapp.dto.SignUpDto
import com.google.firebase.auth.FirebaseAuth


class AuthViewModel: ViewModel()  {

    private val _signUpLiveData = MutableLiveData<SignUpResponse>()
    val signUpLiveData: LiveData<SignUpResponse> = _signUpLiveData

    private val _signInLiveData = MutableLiveData<SignInResponse>()
    val signInLiveData: LiveData<SignInResponse> = _signInLiveData

    fun signUp(signUpDto: SignUpDto) {
        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(signUpDto.email, signUpDto.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _signUpLiveData.postValue(SignUpResponse(true, "SUCCESS"))
                } else {
                    _signUpLiveData.postValue(SignUpResponse(false, "INTERNAL_ERROR"))
                }
            }
            .addOnFailureListener {
                _signUpLiveData.postValue(SignUpResponse(false, "Failed to create user: ${it.message}"))
            }
    }

    fun signIn(signInDto: SignInDto) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(signInDto.email, signInDto.password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _signInLiveData.postValue(SignInResponse(true, "SUCCESS"))
                } else {
                    _signInLiveData.postValue(SignInResponse(false, "INTERNAL_ERROR"))
                }
            }
            .addOnFailureListener {
                _signInLiveData.postValue(SignInResponse(false, "Failed to log in: ${it.message}"))
            }
    }

}