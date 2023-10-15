package com.example.miniproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

lateinit var button: Button
lateinit var button2: Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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
            intent= Intent(this,student_login::class.java)
            startActivity(intent)
        }


    }
}


