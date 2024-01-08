
package com.example.miniproject.adapter
import com.example.miniproject.R


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.miniproject.model.Message

class MessagesAdapter(context: Context, resource: Int, messages: List<Message>) :
    ArrayAdapter<Message>(context, resource, messages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(resource, parent, false)

        val messageTextView: TextView = view.findViewById(R.id.messageTextView)

        val message = getItem(position)
        messageTextView.text = message?.text

        return view
    }
}
