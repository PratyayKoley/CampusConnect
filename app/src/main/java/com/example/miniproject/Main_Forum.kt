package com.example.miniproject

import android.content.Intent
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


class Main_Forum : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    var selectedItemIndex = 0
    private val arrItems = arrayOf("Student" , "Alumni")
    var selectedItem = arrItems[selectedItemIndex]
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

        book.setOnClickListener {
            intent = Intent(this , resources::class.java)
            startActivity(intent)
        }

        notify.setOnClickListener {
            intent = Intent(this , calendarView::class.java)
            startActivity(intent)
        }

        search.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Select one to search for")
                .setSingleChoiceItems(arrItems, selectedItemIndex) {dialog, which->
                    selectedItemIndex = which
                    selectedItem = arrItems[which]
                }
                .setPositiveButton("OK") {dialog, which->
                    val intent = when(selectedItem) {
                        "Student" -> Intent(this, search::class.java)
                        "Alumni" -> Intent(this, search::class.java)
                        else -> Intent(this, Main_Forum::class.java)
                    }
                    showSnackbar(it, "$selectedItem Selected" , intent)
                }
                .setNeutralButton("Cancel") {dialog, which->

                }.show()
        }


        auth = FirebaseAuth.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        database =
            FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("messages")


    }

    private fun showSnackbar(view: View, msg: String, intent: Intent) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG)
            .setAction("OK") {
                startActivity(intent)
            }
            .show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showPopupMenu(view: View?) {
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