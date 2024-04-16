package com.example.miniproject

import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserOnlineTracker {
    private val userStatusRef = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user_status")
    private val auth = FirebaseAuth.getInstance()

    fun updateUserStatus(isOnline: Boolean) {
        if(auth.currentUser!=null) {
            val userId = auth.currentUser?.uid
            userId?.let {
                val statusRef = userStatusRef.child(userId)
                statusRef.child("status").setValue(if (isOnline) "Online" else "Offline")
                statusRef.child(if (isOnline) "last_online" else "last_offline")
                    .setValue(ServerValue.TIMESTAMP)
            }
        }
    }

}

