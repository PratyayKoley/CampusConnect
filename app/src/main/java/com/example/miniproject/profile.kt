package com.example.miniproject

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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

class Profile : AppCompatActivity() {

    private var editname : ImageButton = findViewById(R.id.edit_username)
    private var username : TextView = findViewById(R.id.Username)
    private var useremail : TextView = findViewById(R.id.Useremail)
    private var usertype : TextView = findViewById(R.id.Usertype)
    private lateinit var userDp: ImageView
    private lateinit var changeDpButton: ImageButton
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val selectedImageUri = result.data?.data
            // Handle the selected image URI here, e.g., set it to the ImageView
            setRoundedImage(selectedImageUri)

            // Save the image to Firebase Storage and get the download URL
            saveImageToStorage(selectedImageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        userDp = findViewById(R.id.UserDP)
        changeDpButton = findViewById(R.id.changedp)

        changeDpButton.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImage.launch(intent)
    }

    private fun setRoundedImage(imageUri: android.net.Uri?) {
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

    private fun saveImageToStorage(imageUri: android.net.Uri?) {
        if (imageUri != null) {
            val storageReference: StorageReference =
                FirebaseStorage.getInstance().getReference("profileImages")
                    .child(FirebaseAuth.getInstance().currentUser?.uid ?: "")
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
                            }
                        }
                    }
                }
        }
    }

    private fun saveImageUrlToDatabase(downloadUrl: String) {
        // Get the current user's UID
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

        // Check if the UID is not null
        currentUserUid?.let { UID ->
            // Create a reference to the "users" node in the Realtime Database
            val databaseReference = FirebaseDatabase.getInstance().getReference("users")

            // Create a map to update the "profileImage" child
            val updateMap = hashMapOf<String, Any>("profileImage" to downloadUrl)

            // Update the child under the user's UID
            databaseReference.child(UID).updateChildren(updateMap)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // The update was successful
                        // Handle success if needed
                    } else {
                        // The update failed
                        // Handle the error if needed
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
