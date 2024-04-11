package com.example.miniproject

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class UserOnlineTracker {
    private lateinit var userStatusRef: DatabaseReference
    private val auth = FirebaseAuth.getInstance()
    private lateinit var weeklyStatusRef: DatabaseReference


    fun updateUserStatus(isOnline: Boolean) {
        userStatusRef = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user_status")
        val userId = auth.currentUser?.uid
        userId?.let {
            val statusRef = userStatusRef.child(userId)
            if (isOnline) {
                statusRef.child("status").setValue("online")
                statusRef.child("last_online").setValue(ServerValue.TIMESTAMP)
            } else {
                statusRef.child("status").setValue("offline")
                statusRef.child("last_offline").setValue(ServerValue.TIMESTAMP)
            }
        }
    }

    fun calculateWeeklyOnlineTime() {
        userStatusRef = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("user_status")
        val userId = auth.currentUser?.uid
        userId?.let {
            val statusRef = userStatusRef.child(userId)
            statusRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastOnlineTimestamp = snapshot.child("last_online").getValue(Long::class.java)
                    val lastOfflineTimestamp = snapshot.child("last_offline").getValue(Long::class.java)

                    if (lastOnlineTimestamp != null && lastOfflineTimestamp != null) {
                        val onlineDuration = lastOfflineTimestamp - lastOnlineTimestamp
                        storeWeeklyOnlineTime(userId, onlineDuration)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors
                }
            })
        }
    }



    private fun storeWeeklyOnlineTime(userId: String, onlineDuration: Long) {
        // Format the online duration

        // Store the formatted duration in Firebase
        weeklyStatusRef = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("weekly_stats")

        val weeklyStatsRef = weeklyStatusRef.child(userId)
        val calendar = Calendar.getInstance()
        val weekNumber = calendar.get(Calendar.WEEK_OF_YEAR)
        weeklyStatsRef.child(weekNumber.toString()).setValue(onlineDuration)
    }


}
