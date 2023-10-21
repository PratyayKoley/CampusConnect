package com.example.miniproject
import android.content.Context
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase




class Main_Forum : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_forum)

        auth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        database = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("messages")
        class Message{
            var message:String?=null
            var senderId:String?=null
            constructor(){}
            constructor(message: String?,senderId:String?){
                this.message=message
                this.senderId=senderId


            }
        }
        class MessageAdapter (val context: Context,messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            }

            override fun getItemCount(): Int {

            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
              if(holder.javaClass==SentViewHolder::class.java) {
                  val viewHolder = holder as SentViewHolder
              }else{
                  val viewHolder =holder as ReceiveViewHolder
              }
              }
            }
            // ViewHolder for sent messages
            class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                // Define views in your SentViewHolder here
                val sentMessage=itemView.findViewById<TextView>(R.id.textMessage)
            }

            // ViewHolder for received messages
            class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
                // Define views in your ReceiveViewHolder here
                val sentMessage=itemView.findViewById<TextView>(R.id.textMessage)
            }
        }

    }
}