package com.example.miniproject
import android.widget.EditText

import android.content.Intent
import com.example.miniproject.model.Message
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.FirebaseRecyclerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.ProgressBar
import android.widget.LinearLayout
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import android.widget.ListView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import android.text.TextUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.miniproject.R.*
import com.example.miniproject.adapter.MessagesAdapter

class Main_Forum : AppCompatActivity() {
    // Your existing code for Main_Forum goes here...
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_forum)
        val moreImageView: ImageView = findViewById(R.id.More)
        val notify: ImageView = findViewById(R.id.imageButton7)
        val search: ImageView = findViewById(R.id.search)
        val book : ImageView = findViewById(R.id.books)

        moreImageView.setOnClickListener { view ->
            showPopupMenu(view)
        }

        notify.setOnClickListener {
            intent = Intent(this , calendarView::class.java)
            startActivity(intent)
        }
    }
    private fun showPopupMenu(view: View?) {
        auth = FirebaseAuth.getInstance()
        Log.d("Debug", "More ImageView clicked")
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(menu.menu_item, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                id.menu_item -> {
                    if (auth.currentUser != null) {
                        auth.signOut()
                        val out = Intent(this, MainActivity::class.java)
                        startActivity(out)
                    }
                    true
                }

                else -> false
            }
        }
        popupMenu.show()
    }
}

class GroupChatActivity : AppCompatActivity() {

    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main_forum)

        messageRecyclerView = findViewById(id.chatRecyclerView)
        messageEditText = findViewById(id.message_input)
        sendButton = findViewById(id.send_button)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("messages")

        messagesAdapter = MessagesAdapter(this, mutableListOf())
        messageRecyclerView.adapter = messagesAdapter


        sendButton.setOnClickListener {
            sendMessage()


        }

        // Listen for changes in the database and update the UI accordingly
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                messagesAdapter.add(message)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage() {
        val messageText = messageEditText.text.toString().trim()
        if (TextUtils.isEmpty(messageText)) {
            return
        }

        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: return

        val message = Message(userId, currentUser.displayName, messageText)
        databaseReference.push().setValue(message)

        // Clear the message input field
        messageEditText.text.clear()
    }
}
