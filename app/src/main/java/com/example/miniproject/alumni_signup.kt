package com.example.miniproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
lateinit var button5:Button
class alumni_signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_signup)
        //finding button student login
        button5=findViewById(R.id.button5)
        //making a function to handle on click
        button5.setOnClickListener {
            //creating intent
            intent= Intent(this,student_login::class.java)
            startActivity(intent)
        }

    }
}