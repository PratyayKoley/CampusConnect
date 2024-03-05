package com.example.miniproject
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class student_login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var Stu_Email: EditText
    private lateinit var Password: EditText
    private lateinit var Login: Button
    private lateinit var databaseReference: DatabaseReference

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^crce\\.\\d{3,5}\\.ce@gmail\\.com$")
        return emailRegex.matches(email)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_login)

        auth = FirebaseAuth.getInstance()

        Stu_Email = findViewById(R.id.AluEmail)
        Password = findViewById(R.id.Password)
        Login = findViewById(R.id.button3)
        // Inside your Login.setOnClickListener block
        databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users")
        Login.setOnClickListener{
            if(checking() && isValidEmail(Stu_Email.text.toString())){
                val email = Stu_Email.text.toString()
                val password = Password.text.toString()
                val role = "Student"
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this@student_login) { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser

                            val userUid = currentUser?.uid.toString()

                            // Push the role information to the Realtime Database
                            databaseReference.child(userUid).child("Role").setValue(role)
                            Toast.makeText(this@student_login, "Login Successful", Toast.LENGTH_LONG).show()
                            intent = Intent(this,Main_Forum::class.java)
                            startActivity(intent)
                        } else {
                            val exception = task.exception
                            Toast.makeText(this@student_login, "Authentication failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter valid email and password", Toast.LENGTH_LONG).show()
            }
        }


    }
    private fun checking():Boolean
    {

        return !(Stu_Email.text.toString().trim().isEmpty() || Password.text.toString().trim().isEmpty())
    }
}