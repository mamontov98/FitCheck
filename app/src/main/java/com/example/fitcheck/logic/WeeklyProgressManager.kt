package com.example.fitcheck.logic

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import com.example.fitcheck.R
import com.example.fitcheck.utilities.ImageLoader
import java.text.SimpleDateFormat
import java.util.*

class WeeklyProgressManager(private val activity: Activity) {

    private val storageRef = Firebase.storage.reference//pointer to root packet
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var currentFileUri: Uri? = null//var to save pic
    private lateinit var imageView: ImageView
    private lateinit var onImageUploaded: () -> Unit
    private lateinit var pickMediaLauncher: ActivityResultLauncher<PickVisualMediaRequest>

    private var currentWeekDate: Date = getStartOfWeek(Date())

    // Sets the launcher instance from Activity for image picking
    fun setPickMediaLauncher(launcher: ActivityResultLauncher<PickVisualMediaRequest>) {
        this.pickMediaLauncher = launcher
    }

    // Called when the user picks an image from gallery
    fun onMediaPicked(uri: Uri?) {
        Log.d("WeeklyProgressManager", "onMediaPicked called with uri: $uri")
        if (uri != null) {
            Log.d("WeeklyProgressManager", "Selected URI: $uri")
            currentFileUri = uri
            uploadImageForWeek(currentWeekDate)
        } else {
            Log.d("WeeklyProgressManager", "No media selected")
        }
    }
    //Sets the target ImageView to display images in UI
    fun setImageView(targetImageView: ImageView) {
        this.imageView = targetImageView
    }
    // Sets the callback to be called when an image is uploaded
    fun setOnImageUploadedCallback(callback: () -> Unit) {
        this.onImageUploaded = callback
    }
    //Updates the "current week" used for upload/download logic
    fun updateWeek(date: Date) {
        currentWeekDate = getStartOfWeek(date)
    }
    // Checks if the manager is set to the current week
    fun isCurrentWeek(): Boolean {
        val now = getStartOfWeek(Date())
        val calendar1 = Calendar.getInstance().apply { time = now }
        val calendar2 = Calendar.getInstance().apply { time = currentWeekDate }
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.WEEK_OF_YEAR) == calendar2.get(Calendar.WEEK_OF_YEAR)
    }



    // Loads the progress photo for the current week from Firebase Storage
    fun loadImageForCurrentWeek(userId: String) {
        val folder = dateFormatter.format(currentWeekDate)
        val ref = storageRef.child("progressPhotos/$userId/$folder.jpg")//create path

        ref.downloadUrl
            //try to download
            .addOnSuccessListener { uri ->
                ImageLoader.getInstance().loadImage(uri.toString(), imageView)
            }
            .addOnFailureListener {
                imageView.setImageResource(R.drawable.unavailable_photo)
            }
    }

    //Launches the gallery picker to choose an image
    fun pickImageFromGallery() {
        pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    // Uploads the currently selected image for the given week
    private fun uploadImageForWeek(date: Date) {
        Log.d("WeeklyProgressManager", "uploadImageForWeek called, currentFileUri: $currentFileUri")
        val uri = currentFileUri ?: return
        val folder = dateFormatter.format(date)
        val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = storageRef.child("progressPhotos/$userId/$folder.jpg")

        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    Log.d("WeeklyProgressManager", "Upload failed: ${task.exception}")
                    task.exception?.let { throw it }
                }
                ref.downloadUrl
            }
            .addOnCompleteListener { task ->
                Log.d("WeeklyProgressManager", "Upload task complete, success: ${task.isSuccessful}")
                if (task.isSuccessful) {
                    val downloadUrl = task.result.toString()
                    Log.d("WeeklyProgressManager", "Download URL: $downloadUrl")
                    ImageLoader.getInstance().loadImage(downloadUrl, imageView)
                    Toast.makeText(activity, "העלאה הצליחה", Toast.LENGTH_SHORT).show()
                    onImageUploaded()
                    currentFileUri = null
                } else {
                    Log.d("WeeklyProgressManager", "Upload failed in addOnCompleteListener: ${task.exception}")
                    Toast.makeText(activity, "העלאה נכשלה", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // gets the start date (Sunday) of the week for a given date
    private fun getStartOfWeek(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.firstDayOfWeek = Calendar.SUNDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        return cal.time
    }

    //Returns a formatted string for the week range
    fun getFormattedWeekRange(): String {
        val end = Calendar.getInstance().apply {
            time = currentWeekDate
            add(Calendar.DAY_OF_MONTH, 6)
        }.time
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return "${formatter.format(currentWeekDate)} - ${formatter.format(end)}"
    }

    //Returns the date representing the start of the next week
    fun getNextWeek(): Date {
        return Calendar.getInstance().apply {
            time = currentWeekDate
            add(Calendar.WEEK_OF_YEAR, 1)
        }.time
    }

    //Returns the date representing the start of the previous week
    fun getPreviousWeek(): Date {
        return Calendar.getInstance().apply {
            time = currentWeekDate
            add(Calendar.WEEK_OF_YEAR, -1)
        }.time
    }
}
