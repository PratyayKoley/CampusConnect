package com.example.miniproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class calendarView : AppCompatActivity() {
    private lateinit var calendar: CalendarView
    private lateinit var edittext : EditText
    private var stringDateSelected: String? = null
    private lateinit var databaseReference: DatabaseReference
    private lateinit var save: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_view)
        calendar = findViewById(R.id.calendarView)
        edittext = findViewById(R.id.enterevent)
        save = findViewById(R.id.button6)

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            stringDateSelected = "$dayOfMonth${month + 1}$year"
            calendarClicked()
        }

        databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("calendar")

        save.setOnClickListener {
            val date = stringDateSelected.orEmpty()
            val textToSave = edittext.text.toString()
            Log.d("Database", "Date: $date, Text: $textToSave")
            databaseReference.child(date).setValue(edittext.text.toString())
        }
    }

    private fun calendarClicked() {
        val date = stringDateSelected.orEmpty()
        databaseReference.child(date).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    edittext.setText(snapshot.value.toString())
                } else {
                    edittext.setText("")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled as needed
            }
        })
    }
}