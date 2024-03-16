package com.example.miniproject

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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

private const val TAG = "ProfileActivity"
class Profile : AppCompatActivity() {

    private lateinit var editname: ImageButton
    private lateinit var username: EditText
    private lateinit var useremail: TextView
    private lateinit var usertype: TextView
    private lateinit var userDp: ImageView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance("https://mini-project-62a72-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users")
        storage = FirebaseStorage.getInstance()

        // Initialize views
        editname = findViewById(R.id.edit_username)
        username = findViewById(R.id.Username)
        useremail = findViewById(R.id.Useremail)
        usertype = findViewById(R.id.Usertype)
        userDp = findViewById(R.id.UserDP)
        changeDpButton = findViewById(R.id.changedp)

        loadProfileImageAndType()
        changeDpButton.setOnClickListener {
            openImagePicker()
        }
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
                Log.d(TAG, "User type fetched from database: $userType") // Log user type

                // Load the image into the userDp ImageView using the URL
                if (!profileImageUrl.isNullOrEmpty()) {
                    Glide.with(this /* context */)
                        .load(profileImageUrl)
                        .into(userDp)
                }

                // Set the user type in the usertype TextView
                usertype.text = userType
                useremail.text = currentUserEmail.toString()
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
                    .child("image.jpg")

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

        // Toggle editability of EditText
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
        }
    }


    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }
}
