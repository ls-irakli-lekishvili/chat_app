package com.example.chatapp.extensions

fun validEmail(email: String): Boolean {
    val regex = Regex("""^\w+@\w+\.[a-zA-Z]+$""")
    return regex.containsMatchIn(email)
}