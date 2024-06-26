package com.example.miniproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Context
import android.net.Uri
import android.provider.CallLog
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.miniproject.UserOnlineTracker

data class Message(
    val userId: String? = null,
    val displayName: String? = null,
    val messageText: String? = null,
    val timestamp: Long? = null,
)
class Main_Forum : AppCompatActivity() {
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    var selectedItemIndex = 0
    private val arrItems = arrayOf("Student" , "Alumni")
    var selectedItem = arrItems[selectedItemIndex]
    private lateinit var messagesAdapter: MessagesAdapter
    private val userOnlineTracker = UserOnlineTracker()

    override fun onResume() {
        super.onResume()
        userOnlineTracker.updateUserStatus(true)
    }

    override fun onPause() {
        super.onPause()
        userOnlineTracker.updateUserStatus(false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_forum)
        progressBar = findViewById(R.id.progressBar)
        val moreImageView: ImageView = findViewById(R.id.More)
        val notify: ImageView = findViewById(R.id.imageButton7)
        val search: ImageView = findViewById(R.id.search)
        val book: ImageView = findViewById(R.id.books)
        val profile: ImageView = findViewById(R.id.profile)
        val calls: ImageView = findViewById(R.id.calls)
        val search_msg: ImageButton = findViewById(R.id.search_message)
        val home_button: ImageView = findViewById(R.id.home_button)

        profile.setOnClickListener{
            intent = Intent(this,Profile::class.java)
            startActivity(intent)
        }

        moreImageView.setOnClickListener { view ->
            showPopupMenu(view)
        }

        home_button.setOnClickListener{
            intent = Intent(this,Main_Forum::class.java)
            startActivity(intent)
        }

        search.setOnClickListener {
            MaterialAlertDialogBuilder(this, R.style.AlertDialogCustomStyle)
                .setTitle("Select one to search for")
                .setSingleChoiceItems(arrItems, selectedItemIndex) { dialog, which ->
                    selectedItemIndex = which
                    selectedItem = arrItems[which]
                }
                .setPositiveButton("OK") { dialog, which ->
                    val intent = when(selectedItem) {
                        "Student", "Alumni" -> Intent(this, Search::class.java).apply {
                            putExtra("selectedRole", selectedItem) // Put extra here
                        }
                        else -> Intent(this, Main_Forum::class.java)
                    }
                    startActivity(intent)
                }
                .setNeutralButton("Cancel") { dialog, which -> }
                .show()
        }

        search_msg.setOnClickListener {
            // Show a dialog or search bar to input search query
            showSearchDialog()
        }

        notify.setOnClickListener {
            intent = Intent(this, calendarView::class.java)
            startActivity(intent)
        }

        calls.setOnClickListener {
            val url = "https://meet.google.com/"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
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
                    showProgressBar()
                    val message = snapshot.getValue(Message::class.java)
                    messagesAdapter.add(message)
                    hideProgressBar()
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onChildRemoved(snapshot: DataSnapshot) {}

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    hideProgressBar()
                }
            })
        }

    private fun showSearchDialog() {
        val searchEditText = EditText(this)
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Search Messages")
            .setView(searchEditText)
            .setPositiveButton("Search") { _, _ ->
                val searchText = searchEditText.text.toString().trim()
                searchMessages(searchText)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun searchMessages(query: String) {
        val filteredMessages = mutableListOf<Message>()

        for (message in messagesAdapter.messages) {
            if (message.messageText?.contains(query, ignoreCase = true) == true) {
                filteredMessages.add(message)
            }
        }

        // Update the adapter with filtered messages
        messagesAdapter.updateMessages(filteredMessages)
    }

    private fun showProgressBar() {
        progressBar.visibility = ProgressBar.VISIBLE
    }
    private fun hideProgressBar() {
        progressBar.visibility = ProgressBar.GONE
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
        val userReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("users").child(userId)

        userReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val displayName = snapshot.child("username").value as? String
                val timestamp = System.currentTimeMillis()

                val message = Message(userId, displayName, messageText, timestamp,)
                val messageKey = databaseReference.push().key

                if (messageKey != null) {
                    databaseReference.child(messageKey).setValue(message)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("SendMessage", "Message sent successfully")
                            } else {
                                Log.e("SendMessage", "Error sending message", task.exception)
                            }

                            // Scroll to the last item in the RecyclerView (whether send is successful or not)
                            messageRecyclerView.smoothScrollToPosition(messagesAdapter.itemCount - 1)
                        }
                } else {
                    Log.e("SendMessage", "Message key is null")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessagesAdapter", "Failed to fetch user name", error.toException())
            }
        })

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
                        userOnlineTracker.updateUserStatus(false)
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

class MessagesAdapter(private val context: Context, val messages: MutableList<Message>) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2
    // Update the MessageViewHolder class to include the user name TextView
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userinfo: RelativeLayout? = itemView.findViewById(R.id.UserInfo)
        val timestamp: TextView = itemView.findViewById(R.id.textDateTime)
        val messageText: TextView = itemView.findViewById(R.id.textMessage)
        val userName: TextView? = itemView.findViewById(R.id.Name)
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
            holder.messageText.text = message.messageText
            holder.timestamp.text = formatTimestamp(message.timestamp)
            holder.userName?.text = message.displayName ?: "Username"
        } else {
            // This is a sent message, set only message text
            holder.messageText.text = message.messageText
            holder.timestamp.text = formatTimestamp(message.timestamp)
        }

        holder.userName?.setOnClickListener {
            // Handle click on username
            // Navigate to a different layout or activity
            val intent = Intent(context, ProfileView::class.java).apply {
                putExtra("username", holder.userName.text.toString())
            }
            context.startActivity(intent)
        }

        holder.userinfo?.setOnClickListener {
            val intent = Intent(context, ProfileView::class.java).apply {
                putExtra("username", holder.userName?.text.toString())
            }
            context.startActivity(intent)
        }
    }

    private fun formatTimestamp(timestamp: Long?): String {
        if (timestamp == null) {
            return ""
        }
        val sdf = SimpleDateFormat("EEE dd hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
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

    fun updateMessages(newMessages: List<Message>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

}
