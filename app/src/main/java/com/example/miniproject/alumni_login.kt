package com.example.miniproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.android.gms.common.SignInButton.ButtonSize
import com.google.firebase.auth.FirebaseAuth

class alumni_login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var editTextTextEmailAddress:EditText
    private lateinit var Alu_Pass: EditText
    private lateinit var Login: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_login)
        auth= FirebaseAuth.getInstance()
        editTextTextEmailAddress= findViewById(R.id.editTextTextEmailAddress)
        Alu_Pass=findViewById(R.id.Alu_Pass)
        Login=findViewById(R.id.Login)

    }
}