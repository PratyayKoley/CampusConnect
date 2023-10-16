package com.example.miniproject
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth


class student_login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_login)
        //finding button student login
        button3=findViewById(R.id.button3)
        //making a function to handle on click
        button3.setOnClickListener {
            //creating intent
            intent= Intent(this,Main_Forum::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()





    }
}