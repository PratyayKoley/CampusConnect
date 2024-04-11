/*package com.example.miniproject

        import androidx.appcompat.app.AppCompatActivity
                import android.os.Bundle
                import android.widget.EditText
                import com.google.android.gms.common.SignInButton.ButtonSize
                import com.google.firebase.auth.FirebaseAuth
                import android.widget.Button
                import android.widget.Toast
                import android.content.Intent

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
                Alu_Pass= findViewById(R.id.Alu_Pass)
                Login= findViewById(R.id.button4)
                Login.setOnClickListener {
                    if (checking()) {
                        val email = editTextTextEmailAddress.text.toString()
                        val password = Alu_Pass.text.toString()
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this) { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Login Successfull ", Toast.LENGTH_LONG).show()

                                } else {
                                    Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_LONG).show()

                                    auth.signInWithEmailAndPassword(email, password)
                                        .addOnCompleteListener(this@alumni_login) { task ->
                                            if (task.isSuccessful) {
                                                Toast.makeText(
                                                    this@alumni_login,
                                                    "Login Successful",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                intent = Intent(this, Main_Forum::class.java)
                                                startActivity(intent)
                                            } else {
                                                val exception = task.exception
                                                Toast.makeText(
                                                    this@alumni_login,
                                                    "Authentication failed: ${exception?.message}",Toast.LENGTH_LONG).show()
                                            }
                                        }
                                }
                                else
                                {
                                    Toast.makeText(this, "Enter Details", Toast.LENGTH_LONG).show()
                                }
                            }
                    }

                }
                private fun checking(): Boolean
                {

                    return !(editTextTextEmailAddress.text.toString().trim().isEmpty() || Alu_Pass.text.toString().trim().isEmpty())
                }*/
package com.example.miniproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import com.example.miniproject.databinding.ActivityAlumniLoginBinding
import com.example.miniproject.databinding.ActivityAlumniSignupBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class alumni_login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityAlumniLoginBinding
    private lateinit var editTextTextEmailAddress: EditText
    private lateinit var Alu_Pass: EditText
    private lateinit var Login: Button
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlumniLoginBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_alumni_login)
        auth = FirebaseAuth.getInstance()
        editTextTextEmailAddress = findViewById(R.id.editTextTextEmailAddress)
        Alu_Pass = findViewById(R.id.Alu_Pass)
        Login = findViewById(R.id.button4)
        databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users")
        Login.setOnClickListener {
            if (checking()) {
                val email = editTextTextEmailAddress.text.toString()
                val password = Alu_Pass.text.toString()
                val role = "Alumni"
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val currentUser = auth.currentUser
                            val userUid = currentUser?.uid.toString()
                            val currentemail  = currentUser?.email.toString()

                            databaseReference.child(userUid).child("Role").setValue(role)
                            databaseReference.child(userUid).child("email").setValue(currentemail)
                            Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
                            val intent = Intent(this, Profile::class.java)
                            startActivity(intent)
                        } else {
                            val exception = task.exception
                            Toast.makeText(
                                this, "Authentication failed: ${exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter Details", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checking(): Boolean {
        return !(editTextTextEmailAddress.text.toString().trim().isEmpty() || Alu_Pass.text.toString().trim().isEmpty())
    }
}
