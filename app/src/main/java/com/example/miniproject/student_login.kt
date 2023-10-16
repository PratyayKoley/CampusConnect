package com.example.miniproject
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class student_login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var Stu_Email: EditText
    private lateinit var Password: EditText
    private lateinit var Login: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_login)

        auth = FirebaseAuth.getInstance()

        Login.setOnClickListener{
            if(checking()){
                val email=Stu_Email.text.toString()
                val password=Password.text.toString()
                auth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(this){
                        task ->
                        if(task.isSuccessful){
                            Toast.makeText(this,"Login Successfull ",Toast.LENGTH_LONG).show()

                        }
                        else
                        {
                            Toast.makeText(this,"Wrong Credentials",Toast.LENGTH_LONG).show()
                        }
                    }

            }
            else
            {
                Toast.makeText(this,"Enter the details",Toast.LENGTH_LONG).show()
            }
        }

    }
    private fun checking():Boolean
    {
        if(Stu_Email.text.toString().trim().isEmpty() || Password.text.toString().trim().isEmpty())
        {
            return true
        }
            return false
    }
}