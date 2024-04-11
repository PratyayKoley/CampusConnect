package com.example.miniproject

import android.annotation.SuppressLint
import android.app.Activity
import android.app.VoiceInteractor.Request
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import kotlin.math.min
import android.view.View
import com.bumptech.glide.Glide
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions

private const val TAG = "ProfileActivity"
class Profile : AppCompatActivity() {

    private lateinit var editname: ImageButton
    private lateinit var username: EditText
    private lateinit var useremail: TextView
    private lateinit var usertype: TextView
    private lateinit var userDp: ImageView
    private lateinit var user_mode: TextView
    private lateinit var link: TextView
    private lateinit var changeDpButton: ImageButton
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage


    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            setRoundedImage(selectedImageUri)
            saveImageToStorage(selectedImageUri)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
        storage = FirebaseStorage.getInstance()

        // Initialize views
        user_mode = findViewById(R.id.User_mode)
        editname = findViewById(R.id.edit_username)
        username = findViewById(R.id.Username)
        useremail = findViewById(R.id.Useremail)
        usertype = findViewById(R.id.Usertype)
        userDp = findViewById(R.id.UserDP)
        changeDpButton = findViewById(R.id.changedp)
        link = findViewById(R.id.link_to_forum)

        loadProfileImageAndType()
        changeDpButton.setOnClickListener {
            openImagePicker()
        }
        user_mode.setOnClickListener {
            // Call a method to change the theme
            changeTheme()
        }

        link.setOnClickListener {
            changeView()
        }
    }

    private fun changeView() {
        val enteredUsername = username.text.toString().trim()
        if (enteredUsername.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
        } else {
            val intent = Intent(this, Main_Forum::class.java)
            startActivity(intent)
        }
    }

    private fun changeTheme() {
        // Get the current night mode setting
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        // Toggle between light and dark mode
        val newNightMode = if (currentNightMode == Configuration.UI_MODE_NIGHT_NO) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        // Apply the new theme
        AppCompatDelegate.setDefaultNightMode(newNightMode)
    }

    private fun loadProfileImageAndType() {
        // Get the current user's UID
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        // Check if the UID is not null
        currentUserUid?.let { UID ->
            // Retrieve the profile image URL and user type from the database
            databaseReference.child(UID).get().addOnSuccessListener { dataSnapshot ->
                val profileImageUrl = dataSnapshot.child("profileImage").getValue(String::class.java)
                val userType = dataSnapshot.child("Role").getValue(String::class.java)
                val name = dataSnapshot.child("username").getValue(String::class.java) // Get the username
                Log.d(TAG, "User type fetched from database: $userType") // Log user type

                // Load the image into the userDp ImageView using the URL
                if (!profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this /* context */)
                        .load(profileImageUrl)
                        .apply(RequestOptions.bitmapTransform(CircleCrop()))
                        .apply(RequestOptions.overrideOf(300,300))
                        .apply(RequestOptions.formatOf(DecodeFormat.PREFER_ARGB_8888))
                        .into(userDp)
                }

                // Set the user type in the usertype TextView
                usertype.text = userType
                useremail.text = currentUserEmail.toString()
                if (!TextUtils.isEmpty(name)) {
                    username.text = Editable.Factory.getInstance().newEditable(name)
                } else {
                    // Handle case where username is empty or null
                    // You can set a default value or handle it according to your application's logic
                }
            }.addOnFailureListener { e ->
                // Handle any errors while fetching data from the database
                Log.e(TAG, "Error fetching profile image URL and user type: $e")
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun setRoundedImage(imageUri: Uri?) {
        if (imageUri != null) {
            val inputStream = contentResolver.openInputStream(imageUri)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream)

            // Get the minimum dimension (width or height) to make a square bitmap
            val size = min(selectedBitmap.width, selectedBitmap.height)

            // Crop the selected image to make it square
            val croppedBitmap = Bitmap.createBitmap(selectedBitmap, 0, 0, size, size)

            // Create a rounded bitmap
            val roundedBitmap = getRoundedCornerBitmap(croppedBitmap)

            // Set the rounded bitmap to the ImageView
            userDp.setImageBitmap(roundedBitmap)
        }
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(output)
        val color = -0xbdbdbe // Light gray
        val paint = android.graphics.Paint()
        val rect = android.graphics.Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = android.graphics.RectF(rect)

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, bitmap.width.toFloat(), bitmap.height.toFloat(), paint)
        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    private fun saveImageToStorage(imageUri: Uri?) {
        if (imageUri != null) {
            val storageReference: StorageReference =
                storage.reference.child("profileImages")
                    .child(auth.currentUser?.uid ?: "")
                    .child("image.png")

            val inputStream = contentResolver.openInputStream(imageUri)
            val selectedBitmap = BitmapFactory.decodeStream(inputStream)

            // Get the minimum dimension (width or height) to make a square bitmap
            val size = min(selectedBitmap.width, selectedBitmap.height)

            // Crop the selected image to make it square
            val croppedBitmap = Bitmap.createBitmap(selectedBitmap, 0, 0, size, size)

            // Create a rounded bitmap
            val roundedBitmap = getRoundedCornerBitmap(croppedBitmap)

            // Convert the rounded bitmap to a byte array
            val byteArray = bitmapToByteArray(roundedBitmap)

            // Upload the byte array to Firebase Storage
            storageReference.putBytes(byteArray)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Image upload successful, get the download URL
                        storageReference.downloadUrl.addOnCompleteListener { urlTask ->
                            if (urlTask.isSuccessful) {
                                // URL of the uploaded image
                                val downloadUrl = urlTask.result.toString()

                                // Save the download URL to the Realtime Database under the user's node
                                saveImageUrlToDatabase(downloadUrl)
                            } else {
                                // Handle failure to get download URL
                                // Log an error or display a message to the user
                            }
                        }
                    } else {
                        // Handle failure to upload image
                        // Log an error or display a message to the user
                    }
                }
        }
    }

    private fun saveImageUrlToDatabase(downloadUrl: String) {
        // Get the current user's UID
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        // Check if the UID is not null
        currentUserUid?.let { uid ->
            // Create a map to update the "profileImage" child
            val updateMap = hashMapOf<String, Any>("profileImage" to downloadUrl)

            // Update the child under the user's UID
            databaseReference.child(uid).updateChildren(updateMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Image URL saved to Realtime Database")
                    } else {
                        Log.e(TAG, "Failed to save image URL to Realtime Database")
                    }
                }
        }
    }

    fun onEditUsernameClick(view: View) {
        val editText = findViewById<EditText>(R.id.Username)
        val editButton = findViewById<ImageButton>(R.id.edit_username)

        // Toggle edit ability of EditText
        editText.isEnabled = !editText.isEnabled

        if (editText.isEnabled) {
            editText.requestFocus()
            // Show soft keyboard
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

            // Change button icon to indicate editing mode
            editButton.setImageResource(R.drawable.ok_icon) // Replace with your tick icon
        } else {
            // Hide soft keyboard
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editText.windowToken, 0)

            // Change button icon to indicate non-editing mode
            editButton.setImageResource(R.drawable.write) // Replace with your original icon

            // Get the new username
            val newUsername = editText.text.toString().trim()

            // Update the username in the database
            if (newUsername.isNotEmpty()) {
                updateUsernameAndEmailInDatabase(newUsername)
            } else {
                // Display a message to the user indicating that the username cannot be empty
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUsernameAndEmailInDatabase(newUsername: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val currentUserUid = currentUser?.uid
        val currentUserEmail = currentUser?.email

        currentUserUid?.let { uid ->
            val updateMap = hashMapOf<String, Any>(
                "username" to newUsername,
                "email" to currentUserEmail.orEmpty() // Ensure email is not null
            )

            databaseReference.child(uid).updateChildren(updateMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Username and Email updated in Realtime Database")
                    } else {
                        // Handle failure to update username and email
                        Log.e(TAG, "Failed to update username and email in Realtime Database", task.exception)
                        // Display a message to the user indicating the failure
                        Toast.makeText(this, "Failed to update username and email. Please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }



    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
