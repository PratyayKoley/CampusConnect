package com.example.miniproject
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import com.example.miniproject.databinding.ActivityAlumniSignupBinding

class alumni_signup : AppCompatActivity() {
    private lateinit var binding: ActivityAlumniSignupBinding
    private lateinit var firebaseauth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding=ActivityAlumniSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseauth=FirebaseAuth.getInstance()
        binding.button5.setOnClickListener{
            val email=binding.AluEmail.text.toString()
            val pass=binding.AlumPass.text.toString()
            if(email.isNotEmpty()&&pass.isNotEmpty())
            {
                firebaseauth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                    if (it.isSuccessful){
                        val intent=Intent(this,alumni_login::class.java)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"Field cannot be empty",Toast.LENGTH_SHORT).show()
            }

        }
        binding.textView12.setOnClickListener {
            val loginIntent=Intent(this,alumni_login::class.java)
            startActivity(loginIntent)
        }
    }




}