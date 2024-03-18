package com.example.miniproject

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView

class Calls : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calls)
        val addButton = findViewById<ImageButton>(R.id.add_link)
        addButton.setOnClickListener {
            showLinkInputDialog()
        }
    }

    private fun showLinkInputDialog() {
        val dialog = Dialog(this, R.style.RoundedCornersDialog)
        dialog.setContentView(R.layout.dialog_prompt)

        val window = dialog.window
        window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Find and set up the EditText and Button components
        val linkEditText = dialog.findViewById<EditText>(R.id.linkEditText)
        val confirmButton = dialog.findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener {
            val link = linkEditText.text.toString()
            // Handle the link input here
            Log.d("LinkInput", "Entered link: $link")
            dialog.dismiss()
        }

        dialog.show()
    }
}