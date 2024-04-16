package com.example.miniproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.miniproject.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileView : AppCompatActivity() {
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var typeTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var statusImageView: ImageView
    private lateinit var userdp: ImageView
    private lateinit var databaseReference: DatabaseReference
    private var userId: String? = null // Variable to store the user ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_view)

        // Initialize views
        userdp = findViewById(R.id.UserDP)
        usernameTextView = findViewById(R.id.Username)
        emailTextView = findViewById(R.id.Useremail)
        typeTextView = findViewById(R.id.Usertype)
        statusTextView = findViewById(R.id.Userstatus)
        statusImageView = findViewById(R.id.status)

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
                            val userDp = snapshot.child("profileImage").getValue(String::class.java)
                            userId = snapshot.key // Get the user ID

                            // Set username, email, and user type in TextViews
                            if (!userDp.isNullOrEmpty()) {
                                Glide.with(this@ProfileView /* context */)
                                    .load(userDp)
                                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                                    .apply(RequestOptions.overrideOf(300,300))
                                    .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                                    .into(userdp)
                            }
                            usernameTextView.text = clickedUsername
                            emailTextView.text = email
                            typeTextView.text = userType

                            // Fetch and display user's online/offline status
                            userId?.let { fetchUserStatus(it) }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the errors
                }
            })
        }
    }

    private fun fetchUserStatus(userId: String) {
        val userStatusReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("user_status").child(userId)

        userStatusReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val status = dataSnapshot.child("status").getValue(String::class.java)
                    statusTextView.text = status ?: "Unknown" // Handle null status
                    if(statusTextView.text == "Online") {
                        statusImageView.setImageResource(R.drawable.status_on)
                    }
                    else{
                        statusImageView.setImageResource(R.drawable.status_off)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the errors
            }
        })
    }
}
