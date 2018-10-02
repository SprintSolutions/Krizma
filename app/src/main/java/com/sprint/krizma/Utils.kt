package com.sprint.krizma
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toolbar
//import com.applozic.mobicomkit.api.conversation.AlConversation
import com.applozic.mobicomkit.api.conversation.Message
import com.sprint.krizma.Adapter.DataModel.*
import java.io.ByteArrayOutputStream


class Utils{

    var profile_info = ProfileInfo()
    var profile_description = ""
    var profile_interest = ProfileInterest()
    var profile_books = ProfileBooks()

    var path = ""

    var user_image:Bitmap? = null

    var profilePicChanged = false

    companion object {

        val REQUEST_CAMERA = 1

        val REQUEST_GALLERY = 2

        val CAMERA_PERMISSION = 10

        val STORAGE_PERMISSION = 11

        var users = ArrayList<User>()

        var usersFilterArray = ArrayList<User>()

        var user = User()

        var books = ArrayList<Book>()

        var messages = ArrayList<Message>()

        var favouriteUsers = ArrayList<User>()

        var favouriteBooks = ArrayList<Book>()

        var notifications = ArrayList<Notification>()

        var profilePic = ""

        var countries = ArrayList<String>()

        var cities = ArrayList<String>()

        var profile_gender = "Male"

        var profile_location = "Near Me"

        var profile_status = "Individual"

        var profile_status_bool = false

        var edit_profile_status = "Individual"

        var edit_profile_status_bool = false

        var edit_profile_gender = "Male"

        var edit_profile_location = "Near Me"

        var bookType = "Both"

        var edit_bookType = "Both"


        fun cameraPermission(activity: Activity)
        {

            if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                                Manifest.permission.CAMERA)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                camera(activity)
            }

        }

        fun storagePermission(activity: Activity)
        {
            if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            STORAGE_PERMISSION)

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
                gallery(activity)
            }
        }

        fun camera(activity: Activity)
        {
            val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (camera.resolveActivity(activity.packageManager) != null) {
                activity.startActivityForResult(camera, REQUEST_CAMERA)
            }
        }

        fun gallery(activity: Activity)
        {
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            activity.startActivityForResult(gallery, REQUEST_GALLERY)
        }

        fun getStringImage(bmp: Bitmap): String {
            val baos = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageBytes = baos.toByteArray()
            return Base64.encodeToString(imageBytes, Base64.DEFAULT)

        }

    }



    fun hideSoftKeyboard(activity: Activity) {
        val inputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
                activity.currentFocus!!.windowToken, 0)
    }

//    fun toolbarColor(activity: Activity)
//    {
//        val toolbar = Toolbar(activity)
//
//    }

//    fun internetConnected(context:Context): Boolean
//    {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
//
//        return activeNetwork?.isConnectedOrConnecting == true
//
//    }

    fun isOnline(context:Context): Boolean {
//        try {
            val connectivityManager = context
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val networkInfo = connectivityManager.activeNetworkInfo
            val connected = networkInfo != null && networkInfo.isAvailable &&
                    networkInfo.isConnected

        return connected

    }


}