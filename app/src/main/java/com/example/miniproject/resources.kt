package com.example.miniproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast

class resources : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)

        val items = listOf("Sem Question Papers", "UT Question Papers", "Study Materials", "Books")

        val autoComplete : AutoCompleteTextView = findViewById(R.id.select_resource)

        val adapter = ArrayAdapter(this, R.layout.list_item,items)

        autoComplete.setAdapter(adapter)

        autoComplete.onItemClickListener = AdapterView.OnItemClickListener {
                adapterView, view, i, l ->

            val itemSelected = adapterView.getItemAtPosition(i)
            Toast.makeText(this, "$itemSelected", Toast.LENGTH_SHORT).show()
        }
    }
}