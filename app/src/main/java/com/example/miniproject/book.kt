package com.example.miniproject

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.flexbox.FlexboxLayout

class Book : AppCompatActivity() {

    private lateinit var flexboxLayout: FlexboxLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        val items = listOf("Sem Question Papers", "UT Question Papers", "Study Materials", "Books")

        val autoComplete: AutoCompleteTextView = findViewById(R.id.select_resource)
        flexboxLayout = findViewById(R.id.flexboxLayout)

        val adapter = ArrayAdapter(this, R.layout.list_item, items)

        autoComplete.setAdapter(adapter)
        autoComplete.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, i, l ->

            val itemSelected = adapterView.getItemAtPosition(i).toString()
            Toast.makeText(this, itemSelected, Toast.LENGTH_SHORT).show()

            when (itemSelected) {
                "Sem Question Papers" -> showSemFlexbox()
                "UT Question Papers" -> showUTFlexbox()
                "Study Materials" -> showMaterialFlexbox()
                "Books" -> showBookFlexbox()
            }
        })
    }

    private fun showBookFlexbox() {
        clearFlexbox()

        val math1 = createFlexboxImageButton(R.drawable.mathsem1,"www.google.com",200,200)
        flexboxLayout.addView(math1)

        val math2 = createFlexboxImageButton(R.drawable.mathsem2,"www.google.com",200,200)
        flexboxLayout.addView(math2)

        val math3 = createFlexboxImageButton(R.drawable.mathsem3,"www.google.com",200,200)
        flexboxLayout.addView(math3)

        val math4 = createFlexboxImageButton(R.drawable.mathsem4,"www.google.com",200,200)
        flexboxLayout.addView(math4)

        val cormen = createFlexboxImageButton(R.drawable.cormen,"www.google.com",200,200)
        flexboxLayout.addView(cormen)

        val c = createFlexboxImageButton(R.drawable.letusc,"www.google.com",200,200)
        flexboxLayout.addView(c)

    }

    private fun showMaterialFlexbox() {
        clearFlexbox()

        val drafter = createFlexboxImageButton(R.drawable.drafter,"www.google.com",200,200)
        flexboxLayout.addView(drafter)

        val container = createFlexboxImageButton(R.drawable.container,"www.google.com",200,200)
        flexboxLayout.addView(container)

        val roller = createFlexboxImageButton(R.drawable.roller,"www.google.com",200,200)
        flexboxLayout.addView(roller)
    }

    private fun showSemFlexbox() {
        clearFlexbox()

        val sem1Button = createFlexboxImageButton(R.drawable.sem1,"www.google.com",200,200)
        flexboxLayout.addView(sem1Button)

        val sem2Button = createFlexboxImageButton(R.drawable.sem2,"www.google.com",200,200)
        flexboxLayout.addView(sem2Button)

        val sem3Button = createFlexboxImageButton(R.drawable.sem3,"www.google.com",200,200)
        flexboxLayout.addView(sem3Button)

        val sem4Button = createFlexboxImageButton(R.drawable.sem4,"www.google.com",200,200)
        flexboxLayout.addView(sem4Button)

        val sem5Button = createFlexboxImageButton(R.drawable.sem5,"www.google.com",200,200)
        flexboxLayout.addView(sem5Button)

        val sem6Button = createFlexboxImageButton(R.drawable.sem6,"www.google.com",200,200)
        flexboxLayout.addView(sem6Button)

        val sem7Button = createFlexboxImageButton(R.drawable.sem7,"www.google.com",200,200)
        flexboxLayout.addView(sem7Button)

        val sem8Button = createFlexboxImageButton(R.drawable.sem8,"www.google.com",200,200)
        flexboxLayout.addView(sem8Button)
    }

    private fun showUTFlexbox() {
        clearFlexbox()

        val ut1Button = createFlexboxImageButton(R.drawable.ut1,"www.google.com",200,200)
        flexboxLayout.addView(ut1Button)

        val ut2Button = createFlexboxImageButton(R.drawable.ut2,"www.google.com",200,200)
        flexboxLayout.addView(ut2Button)
    }

    private fun clearFlexbox() {
        flexboxLayout.removeAllViews()
    }

    private fun createFlexboxImageButton(imageResId: Int,linkUrl: String, widthDp: Int, heightDp: Int): ImageButton {
        val imageButton = ImageButton(this)

        // Convert dp to pixels for width
        val widthInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            widthDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        // Convert dp to pixels for height
        val heightInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            heightDp.toFloat(),
            resources.displayMetrics
        ).toInt()

        // Set layout parameters with the calculated width and height
        val layoutParams = ViewGroup.LayoutParams(widthInPixels, heightInPixels)

        imageButton.layoutParams = layoutParams

        // Set scaleType to fitCenter
        imageButton.scaleType = ImageView.ScaleType.FIT_CENTER

        // Set image resource
        imageButton.setImageResource(imageResId)

        // Set the link
        imageButton.setOnClickListener{
            try {
                // Create an Intent with ACTION_VIEW and the link URL
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))

                // Start the activity for viewing the URL
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                // Handle the case where there is no application to handle the intent
                Toast.makeText(this, "No application found to handle the URL", Toast.LENGTH_SHORT).show()
            }
        }

        return imageButton
    }

}