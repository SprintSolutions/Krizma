package com.sprint.krizma

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.applozic.audiovideo.activity.AudioCallActivityV2
import com.applozic.audiovideo.activity.VideoActivity
//import com.applozic.audiovideo.activity.AudioCallActivityV2
//import com.applozic.audiovideo.activity.VideoActivity
import com.applozic.mobicomkit.Applozic
import com.applozic.mobicomkit.ApplozicClient
import com.applozic.mobicomkit.api.account.register.RegistrationResponse
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference
import com.applozic.mobicomkit.api.account.user.PushNotificationTask
import com.applozic.mobicomkit.api.account.user.UserLoginTask
//import com.applozic.mobicomkit.api.conversation.AlConversation
import com.applozic.mobicomkit.api.conversation.ApplozicConversation
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.sprint.krizma.Adapter.DataModel.Book
import com.sprint.krizma.Adapter.DataModel.Notification
import com.sprint.krizma.Adapter.DataModel.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONException
import org.json.JSONObject


class SplashActivity : AppCompatActivity() {

    val SPLASH_DISPLAY_LENGTH: Long = 2000
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude = 0.0
    var longitute = 0.0
    val LOCATION_PERMISSION = 5


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

//        locationPermission()

        val sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE)
        val loggedIn = sharedPref.getBoolean("loggedIn",false)

        val location = sharedPref.getBoolean("location",false)

//        if (location)
//        {
//
//            return
//        }

        if(loggedIn)
        {

            loginOld()
            return
        }

//        locationPermission()

        Handler().postDelayed({
//            locationPermission()
            val mainIntent = Intent(this, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        },SPLASH_DISPLAY_LENGTH)

    }

    fun loginOld()
    {

        splash_progress.visibility = View.VISIBLE

        val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        val u_id = sharedpreferences.getString("user_id", "")
        val lat = sharedpreferences.getString("lat","")
        val long = sharedpreferences.getString("long","")

        val request = JSONObject()
        request.put("u_id",u_id)
        request.put("lat",lat)
        request.put("long",long)

        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/userLogin"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                Listener<JSONObject> { response ->

                    Log.d("response",response.toString())

                    splash_progress.visibility = View.GONE

                    val code = response.getInt("code")

                    if(code==101)
                    {
                        if (ContextCompat.checkSelfPermission(this,
                                        Manifest.permission.READ_CONTACTS)
                                == PackageManager.PERMISSION_GRANTED) {

                            fusedLocationClient.lastLocation
                                    .addOnSuccessListener { location: Location? ->
                                        latitude = location!!.latitude
                                        longitute = location!!.longitude

                                    }
                        }

                        val userJson = response.getJSONObject("user")

                        val usersJson = response.getJSONArray("all_users")

                        val favouriteUsers = userJson.getJSONArray("favourite_users")

                        val favouriteBooks = userJson.getJSONArray("favourite_books")

                        val notifications = response.getJSONArray("notifications")

//                        if(usersJson.length()>0) {

                            for (i in 0 until usersJson.length()) {
                                val allBooks = usersJson.getJSONObject(i).getJSONArray("user_books")

                                val books = ArrayList<Book>()

                                val currentUser = User(usersJson.getJSONObject(i).getInt("u_id"),
                                        usersJson.getJSONObject(i).getString("u_name"),
                                        usersJson.getJSONObject(i).getString("u_email"),
                                        usersJson.getJSONObject(i).getString("u_password"),
                                        usersJson.getJSONObject(i).getString("u_status"),
                                        usersJson.getJSONObject(i).getString("u_user_status"),
                                        usersJson.getJSONObject(i).getString("u_gender"),
                                        usersJson.getJSONObject(i).getString("u_age"),
                                        usersJson.getJSONObject(i).getString("u_lat"),
                                        usersJson.getJSONObject(i).getString("u_long"),
                                        usersJson.getJSONObject(i).getString("u_location"),
                                        usersJson.getJSONObject(i).getString("u_country"),
                                        usersJson.getJSONObject(i).getString("u_city"),
                                        usersJson.getJSONObject(i).getString("u_area"),
                                        usersJson.getJSONObject(i).getString("u_description"),
                                        usersJson.getJSONObject(i).getString("u_avatar"),
                                        usersJson.getJSONObject(i).getString("u_languages"),
                                        usersJson.getJSONObject(i).getString("u_genres"),
                                        usersJson.getJSONObject(i).getString("u_authors"),
                                        usersJson.getJSONObject(i).getString("u_type"),
                                        usersJson.getJSONObject(i).getString("u_token"),
                                        usersJson.getJSONObject(i).getString("u_role"),
                                        usersJson.getJSONObject(i).getString("u_user_type"),
                                        usersJson.getJSONObject(i).getString("u_verify_code"),
                                        usersJson.getJSONObject(i).getString("u_verify_flag"),
                                        usersJson.getJSONObject(i).getInt("favourite_flag"),
                                        usersJson.getJSONObject(i).getString("dist_str")
                                )


                                    for (i in 0 until allBooks.length()) {
                                        books.add(Book(allBooks.getJSONObject(i).getInt("b_id"),
                                                allBooks.getJSONObject(i).getInt("u_id"),
                                                allBooks.getJSONObject(i).getString("b_name"),
                                                allBooks.getJSONObject(i).getString("b_author"),
                                                allBooks.getJSONObject(i).getString("b_language"),
                                                allBooks.getJSONObject(i).getString("b_type"),
                                                allBooks.getJSONObject(i).getString("b_image"),
                                                allBooks.getJSONObject(i).getInt("favourite_flag")
                                        ))
                                    }

                                currentUser.u_books = books

                                Utils.users.add(currentUser)

                            }


                        Log.d("books", Utils.users.toString())



                        Utils.user = User(
                                userJson.getInt("u_id"),
                                userJson.getString("u_name"),
                                userJson.getString("u_email"),
                                userJson.getString("u_password"),
                                userJson.getString("u_status"),
                                userJson.getString("u_user_status"),
                                userJson.getString("u_gender"),
                                userJson.getString("u_age"),
                                userJson.getString("u_lat"),
                                userJson.getString("u_long"),
                                userJson.getString("u_location"),
                                userJson.getString("u_country"),
                                userJson.getString("u_city"),
                                userJson.getString("u_area"),
                                userJson.getString("u_description"),
                                userJson.getString("u_avatar"),
                                userJson.getString("u_languages"),
                                userJson.getString("u_genres"),
                                userJson.getString("u_authors"),
                                userJson.getString("u_type"),
                                userJson.getString("u_token"),
                                userJson.getString("u_role"),
                                userJson.getString("u_user_type"),
                                userJson.getString("u_verify_code"),
                                userJson.getString("u_verify_flag")
//                                    userJson.getString("created_at"),
//                                    userJson.getString("updated_at")
                        )

//                            Utils.user = user

                        val userBooks = userJson.getJSONArray("books")


//                        if(userBooks.length()>0) {

                            for (i in 0 until userBooks.length()) {
                                Utils.books.add(Book(userBooks.getJSONObject(i).getInt("b_id"),
                                        userBooks.getJSONObject(i).getInt("u_id"),
                                        userBooks.getJSONObject(i).getString("b_name"),
                                        userBooks.getJSONObject(i).getString("b_author"),
                                        userBooks.getJSONObject(i).getString("b_language"),
                                        userBooks.getJSONObject(i).getString("b_type"),
                                        userBooks.getJSONObject(i).getString("b_image")))

                            }
//                        }

//                        if(favouriteUsers.length()>0) {

                            for (i in 0 until favouriteUsers.length()) {
                                Utils.favouriteUsers.add(User(
                                        favouriteUsers.getJSONObject(i).getInt("u_id"),
                                        favouriteUsers.getJSONObject(i).getString("u_name"),
                                        favouriteUsers.getJSONObject(i).getString("u_email"),
                                        favouriteUsers.getJSONObject(i).getString("u_password"),
                                        favouriteUsers.getJSONObject(i).getString("u_status"),
                                        favouriteUsers.getJSONObject(i).getString("u_user_status"),
                                        favouriteUsers.getJSONObject(i).getString("u_gender"),
                                        favouriteUsers.getJSONObject(i).getString("u_age"),
                                        favouriteUsers.getJSONObject(i).getString("u_lat"),
                                        favouriteUsers.getJSONObject(i).getString("u_long"),
                                        favouriteUsers.getJSONObject(i).getString("u_location"),
                                        favouriteUsers.getJSONObject(i).getString("u_country"),
                                        favouriteUsers.getJSONObject(i).getString("u_city"),
                                        favouriteUsers.getJSONObject(i).getString("u_area"),
                                        favouriteUsers.getJSONObject(i).getString("u_description"),
                                        favouriteUsers.getJSONObject(i).getString("u_avatar"),
                                        favouriteUsers.getJSONObject(i).getString("u_languages"),
                                        favouriteUsers.getJSONObject(i).getString("u_genres"),
                                        favouriteUsers.getJSONObject(i).getString("u_authors"),
                                        favouriteUsers.getJSONObject(i).getString("u_type"),
                                        favouriteUsers.getJSONObject(i).getString("u_token"),
                                        favouriteUsers.getJSONObject(i).getString("u_role"),
                                        favouriteUsers.getJSONObject(i).getString("u_user_type"),
                                        favouriteUsers.getJSONObject(i).getString("u_verify_code"),
                                        favouriteUsers.getJSONObject(i).getString("u_verify_flag"),
                                        1,
                                        favouriteUsers.getJSONObject(i).getString("dist_str")
                                ))
                            }
//                        }


//                        if(favouriteBooks.length()>0) {

                        try {
                            for (i in 0 until favouriteBooks.length()) {
                                Utils.favouriteBooks.add(Book(userBooks.getJSONObject(i).getInt("b_id"),
                                        favouriteBooks.getJSONObject(i).getInt("u_id"),
                                        favouriteBooks.getJSONObject(i).getString("b_name"),
                                        favouriteBooks.getJSONObject(i).getString("b_author"),
                                        favouriteBooks.getJSONObject(i).getString("b_language"),
                                        favouriteBooks.getJSONObject(i).getString("b_type"),
                                        favouriteBooks.getJSONObject(i).getString("b_image"),
                                        1))
                            }
                        }
                        catch (e:JSONException)
                        {
                            e.printStackTrace()
                        }


                        try
                        {
                            for (i in 0 until notifications.length())
                            {
                                Utils.notifications.add(Notification(notifications.getJSONObject(i).getString("notification"),
                                        notifications.getJSONObject(i).getString("created_at")))
                            }

                        }

                        catch (e:JSONException)
                        {
                            e.printStackTrace()
                        }


//                        }

                        val listener = object: UserLoginTask.TaskListener {
                            override fun onSuccess(registrationResponse: RegistrationResponse, context:Context) {
                                // After successful registration with Applozic server the callback will come here

                                ApplozicClient.getInstance(context).setContextBasedChat(true).setHandleDial(true).isIPCallEnabled = true
                                val activityCallbacks : HashMap<ApplozicSetting.RequestCode, String> = HashMap()
                                activityCallbacks[ApplozicSetting.RequestCode.AUDIO_CALL] = AudioCallActivityV2::class.java.name
                                activityCallbacks[ApplozicSetting.RequestCode.VIDEO_CALL] = VideoActivity::class.java.name
                                ApplozicSetting.getInstance(context).setActivityCallbacks(activityCallbacks)


                                if (MobiComUserPreference.getInstance(context).isRegistered)
                                {
                                    val pushNotificationTask: PushNotificationTask?
                                    val listener = object:PushNotificationTask.TaskListener {
                                        override fun onSuccess(registrationResponse:RegistrationResponse) {
                                        }
                                        override fun onFailure(registrationResponse:RegistrationResponse, exception:Exception) {
                                        }
                                    }
                                    pushNotificationTask = PushNotificationTask( Applozic.getInstance(this@SplashActivity).deviceRegistrationId, listener, this@SplashActivity)
                                    pushNotificationTask.execute(null as Void?)
                                }




                            }
                            override fun onFailure(registrationResponse: RegistrationResponse, exception:Exception) {
                                // If any failure in registration the callback will come here
//                                Toast.makeText(this@SplashActivity,"failed",Toast.LENGTH_SHORT).show()
                            }
                        }
                        ApplozicClient.getInstance(this@SplashActivity).enableNotification()
                        val user = com.applozic.mobicomkit.api.account.user.User()
                        user.userId = Utils.user.u_id.toString() //userId it can be any unique user identifier
                        user.displayName = Utils.user.u_name //displayName is the name of the user which will be shown in chat messages
                        user.email = Utils.user.u_email //optional
                        user.authenticationTypeId = com.applozic.mobicomkit.api.account.user.User.AuthenticationType.APPLOZIC.value //User.AuthenticationType.APPLOZIC.getValue() for password verification from Applozic server and User.AuthenticationType.CLIENT.getValue() for access Token verification from your server set access token as password
                        user.password = "" //optional, leave it blank for testing purpose, read this if you want to add additional security by verifying password from your server https://www.applozic.com/docs/configuration.html#access-token-url
                        user.imageLink = Utils.user.u_avatar//optional,pass your image link
                        val featureList  =  ArrayList<String>()
                        featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_AUDIO_CALL.value)// FOR AUDIO
                        featureList.add(com.applozic.mobicomkit.api.account.user.User.Features.IP_VIDEO_CALL.value)// FOR VIDEO
                        user.features = featureList // ADD FEATURES
                        try {
                            UserLoginTask(user, listener, this).execute(null as Void?)
                        }
                        catch (e:IllegalArgumentException)
                        {
                            e.printStackTrace()
                        }

                            val lat = userJson.getString("u_lat")
                            val long = userJson.getString("u_long")

                            val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                            val editor = sharedpreferences.edit()

                        if(lat.isNotEmpty()&&long.isNotEmpty()) {
                            editor.putString("user_id", userJson.getInt("u_id").toString())
                            editor.putString("lat", lat)
                            editor.putString("long", long)
                            editor.commit()
                        }

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }

                    else if(code==103)
                    {
                        Utils.user.u_id = response.getInt("u_id")
                        Utils.user.u_email = response.getString("email")


                        verifyUserDialog()
                    }

                    else if(code==102)
                    {
                        Toast.makeText(this,"Invalid username or password.", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    splash_progress.visibility = View.GONE
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }
        ) {

        }
        queue.add(postRequest)
    }

    fun verifyUserDialog()
    {
        val builder = AlertDialog.Builder(this,R.style.AlertDialogCustom)
        builder.setMessage("Please verify your email")
                .setPositiveButton("Verify") { dialog, id ->
                    val intent = Intent(this,ProfileActivity::class.java)
                    startActivity(intent)
                }
                .setNeutralButton("Resend Code") { dialog, id ->
                    resendCode()
                }
                .setNegativeButton("Skip") { dialog, id ->
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }

        // Create the AlertDialog object and return it
        builder.create()
        builder.show()

    }


    fun resendCode()
    {
        val request = JSONObject()

        val u_email = Utils.user.u_email

        request.put("email",u_email)

        splash_progress.visibility =View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/resendCode"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                Listener<JSONObject> { response ->

                    splash_progress.visibility =View.INVISIBLE

                    val code = response.getInt("code")

                    if(code==101)
                    {
                        Toast.makeText(this,"You will receive an email soon", Toast.LENGTH_LONG).show()

                        val intent = Intent(this,ProfileActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this,"Something went wrong", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                    splash_progress.visibility =View.INVISIBLE
                }
        ) {

        }
        queue.add(postRequest)
    }

//    fun locationPermission()
//    {
//
//        if (ContextCompat.checkSelfPermission(this,
//                        Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
//
//                locationPermission()
//
//                val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
//                val editor = sharedpreferences.edit()
//
//                editor.putString("lat",latitude.toString())
//                editor.putString("long",longitute.toString())
//                editor.commit()
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(this,
//                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                        LOCATION_PERMISSION)
//
//                locationPermission()
//
//                val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
//                val editor = sharedpreferences.edit()
//
//                editor.putString("lat",latitude.toString())
//                editor.putString("long",longitute.toString())
//                editor.commit()
//
//
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//            fusedLocationClient.lastLocation
//                    .addOnSuccessListener { location : Location? ->
//                        latitude = location!!.latitude
//                        longitute =location.longitude
//
//                        val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
//                        val editor = sharedpreferences.edit()
//
//                        editor.putString("lat",latitude.toString())
//                        editor.putString("long",longitute.toString())
//                        editor.putBoolean("location",true)
//                        editor.commit()
//
//                        val intent = Intent(this,LoginActivity::class.java)
//                        startActivity(intent)
//                    }
//        }
//
//    }

}
