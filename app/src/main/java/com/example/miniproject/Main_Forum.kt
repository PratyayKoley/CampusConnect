package com.example.miniproject

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Main_Forum : AppCompatActivity() {
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_forum)

        val moreImageView: ImageView = findViewById(R.id.More)
        val notify: ImageView = findViewById(R.id.imageButton7)
        val search: ImageView = findViewById(R.id.search)
        val book: ImageView = findViewById(R.id.books)

        moreImageView.setOnClickListener { view ->
            showPopupMenu(view)
        }

        notify.setOnClickListener {
            intent = Intent(this, calendarView::class.java)
            startActivity(intent)
        }

        book.setOnClickListener {
            intent = Intent(this, Book::class.java)
            startActivity(intent)
        }


            messageRecyclerView = findViewById(R.id.chatRecyclerView)
            messageEditText = findViewById(R.id.message_input)
            sendButton = findViewById(R.id.send_button)

            auth = FirebaseAuth.getInstance()
            databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("messages")

            messagesAdapter = MessagesAdapter(this, mutableListOf())
            messageRecyclerView.adapter = messagesAdapter

            sendButton.setOnClickListener {
                Toast.makeText(this, "Sent Successful", Toast.LENGTH_LONG).show()
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

    fun sendMessage() {
        Log.d("Button", "Button clicked")
        val messageText = messageEditText.text.toString().trim()
        if (TextUtils.isEmpty(messageText)) {
            return
        }

        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("SendMessage", "Current user is null")
            return
        }

        val userId = currentUser.uid

        val message = Message(userId, currentUser.displayName, messageText)
        val messageKey = databaseReference.push().key

        if (messageKey != null) {
            databaseReference.child(messageKey).setValue(message)
                .addOnSuccessListener {
                    Log.d("SendMessage", "Message sent successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("SendMessage", "Error sending message", e)
                }
        } else {
            Log.e("SendMessage", "Message key is null")
        }

        // Clear the message input field
        messageEditText.text.clear()
    }

    private fun showPopupMenu(view: View?) {
        auth = FirebaseAuth.getInstance()
        Log.d("Debug", "More ImageView clicked")
        val popupMenu = PopupMenu(this, view)
        val inflater = popupMenu.menuInflater
        inflater.inflate(R.menu.menu_item, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item -> {
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

data class Message(
    val userId: String? = null,
    val displayName: String? = null,
    val messageText: String? = null
)

class MessagesAdapter(private val context: Context, private val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2
    // Update the MessageViewHolder class to include the user name TextView
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.textMessage)
        val userName: TextView = itemView.findViewById(R.id.Name)
    }

    // Update onCreateViewHolder method to inflate the correct layout based on the view type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val layoutResId = if (viewType == VIEW_TYPE_RECEIVED) {
            R.layout.item_container_received_message
        } else {
            R.layout.item_container_sent_message
        }
        val view = LayoutInflater.from(context).inflate(layoutResId, parent, false)
        return MessageViewHolder(view)
    }

    // Update onBindViewHolder method to set data based on the view type
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]

        if (getItemViewType(position) == VIEW_TYPE_RECEIVED) {
            // This is a received message, set user name and message text
            holder.userName.text = message.displayName
            holder.messageText.text = message.messageText
        } else {
            // This is a sent message, set only message text
            holder.messageText.text = message.messageText
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        return if (message.userId == auth.currentUser?.uid) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }


    override fun getItemCount(): Int {
        return messages.size
    }

    fun add(message: Message?) {
        if (message != null) {
            messages.add(message)
            notifyDataSetChanged()
        }
    }
}
