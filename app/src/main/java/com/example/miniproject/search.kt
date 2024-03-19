package com.example.miniproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Search : AppCompatActivity() {
    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchView = findViewById(R.id.search_user)
        listView = findViewById(R.id.list_view)
        databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("users")

        // Initialize the ListView with an empty adapter
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedUsername = adapter.getItem(position)
            val intent = Intent(this, ProfileView::class.java).apply {
                putExtra("username", selectedUsername)
            }
            startActivity(intent)
        }


        // Filter the ListView items based on search query
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        // Get the role passed from the previous activity
        val selectedRole = intent.getStringExtra("selectedRole")

        // Fetch data based on the selected role
        if (selectedRole != null) {
            fetchDataBasedOnRole(adapter, selectedRole)
        }
    }

    private fun fetchDataBasedOnRole(adapter: ArrayAdapter<String>, role: String) {
        val query = databaseReference.orderByChild("Role").equalTo(role)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = mutableListOf<String>()
                for (snapshot in dataSnapshot.children) {
                    val username = snapshot.child("username").getValue(String::class.java)
                    username?.let { data.add(it) }
                }
                adapter.clear()
                adapter.addAll(data)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
            }
        })
    }

}