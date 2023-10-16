package com.example.miniproject
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.Toast
import android.content.Intent

class alumni_signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var Stu_Email: EditText
    private lateinit var Alum_Pass: EditText
    private lateinit var button5: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alumni_signup)
        auth = FirebaseAuth.getInstance()
        Stu_Email = findViewById(R.id.Stu_Email)
        Alum_Pass = findViewById(R.id.Alum_Pass)
        button5 = finjdViewById(R.id.button5)

        button5.setOnClickListener {
            if (checking()) {
                val email =Stu_Email .text.toString()
                val password = Alum_Pass.text.toString()

                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Signup Successful", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, alumni_login::class.java)
                            startActivity(intent)
                        } else {
                            val exception = task.exception
                            Toast.makeText(
                                this,
                                "Authentication failed: ${exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter Details", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checking(): Boolean {
        return !(Stu_Email.text.toString().trim().isEmpty() || Alum_Pass.text.toString().trim().isEmpty())
    }
}

