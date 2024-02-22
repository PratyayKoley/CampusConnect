package com.example.miniproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

lateinit var button: Button
lateinit var button2: Button
lateinit var auth: FirebaseAuth
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //To check if  user is logged in or not
        auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null) {
            val out = Intent(this, Main_Forum::class.java)
            startActivity(out)
        }
        //finding button student login
        button=findViewById(R.id.button)
        //making a function to handle on click
        button.setOnClickListener {
            //creating intent
            intent= Intent(this,student_login::class.java)
            startActivity(intent)
        }
        // finding alumni sign up
        button2=findViewById(R.id.button2)
        //making a function to handle on click
        button2.setOnClickListener {
            //creating intent
            intent= Intent(this,alumni_signup::class.java)
            startActivity(intent)
        }


    }
}


