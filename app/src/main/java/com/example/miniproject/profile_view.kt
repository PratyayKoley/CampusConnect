package com.example.miniproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileView : AppCompatActivity() {
        private lateinit var usernameTextView: TextView
        private lateinit var emailTextView: TextView
        private lateinit var typeTextView: TextView
        private lateinit var databaseReference: DatabaseReference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_profile_view)

            // Initialize views
            usernameTextView = findViewById(R.id.Username)
            emailTextView = findViewById(R.id.Useremail)
            typeTextView = findViewById(R.id.Usertype)

            // Initialize Firebase database reference
            databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users")

            // Retrieve clicked username from intent extras
            val clickedUsername = intent.getStringExtra("username")

            // Load user profile based on clicked username
            loadUserProfile(clickedUsername)
        }

        private fun loadUserProfile(clickedUsername: String?) {
            if (!clickedUsername.isNullOrEmpty()) {
                val query = databaseReference.orderByChild("username").equalTo(clickedUsername)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (snapshot in dataSnapshot.children) {
                                val email = snapshot.child("email").getValue(String::class.java)
                                val userType = snapshot.child("Role").getValue(String::class.java)

                                // Set username, email, and user type in TextViews
                                usernameTextView.text = clickedUsername
                                emailTextView.text = email
                                typeTextView.text = userType
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        //Handle the errors

                    }
                })
            }
        }
}