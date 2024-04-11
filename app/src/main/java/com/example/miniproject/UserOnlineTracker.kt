package com.example.miniproject

import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserOnlineTracker {
    private val userStatusRef = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user_status")
    private val weeklyStatusRef = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("weekly_stats")
    private val auth = FirebaseAuth.getInstance()

    fun updateUserStatus(isOnline: Boolean) {
        val userId = auth.currentUser?.uid
        userId?.let {
            val statusRef = userStatusRef.child(userId)
            statusRef.child("status").setValue(if (isOnline) "online" else "offline")
            statusRef.child(if (isOnline) "last_online" else "last_offline").setValue(ServerValue.TIMESTAMP)
        }
    }

}

