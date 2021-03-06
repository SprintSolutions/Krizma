package com.sprint.krizma

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import android.view.MotionEvent
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.applozic.audiovideo.activity.AudioCallActivityV2
import com.applozic.audiovideo.activity.VideoActivity
import com.applozic.mobicomkit.Applozic
import com.applozic.mobicomkit.ApplozicClient
import com.applozic.mobicomkit.api.account.register.RegistrationResponse
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference
import com.applozic.mobicomkit.api.account.user.PushNotificationTask
import com.applozic.mobicomkit.api.account.user.UserLoginTask
import com.applozic.mobicomkit.uiwidgets.ApplozicSetting
import com.facebook.*
import org.json.JSONObject
import com.facebook.login.LoginResult
import com.facebook.login.LoginManager
import java.util.*
import com.facebook.GraphRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.linkedin.platform.APIHelper
import com.linkedin.platform.DeepLinkHelper
import com.linkedin.platform.LISession
import com.linkedin.platform.LISessionManager
import com.linkedin.platform.errors.LIApiError
import com.linkedin.platform.errors.LIAuthError
import com.linkedin.platform.errors.LIDeepLinkError
import com.linkedin.platform.listeners.ApiListener
import com.linkedin.platform.listeners.ApiResponse
import com.linkedin.platform.listeners.AuthListener
import com.linkedin.platform.listeners.DeepLinkListener
import com.linkedin.platform.utils.Scope
import com.sprint.krizma.Adapter.DataModel.Book
import com.sprint.krizma.Adapter.DataModel.ProfileBooks
import com.sprint.krizma.Adapter.DataModel.User
import com.sprint.krizma.Profile.VerifyFragment
import org.json.JSONException
import kotlin.collections.ArrayList


class LoginActivity : AppCompatActivity() {




    var callbackManager:CallbackManager? = null
    val RC_SIGN_IN = 9001
    var mGoogleSignInClient:GoogleSignInClient? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val LOCATION_PERMISSION = 5
    val utils = Utils()
    var latitude:Double?  = 0.0
    var longitute:Double? = 0.0

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPref = getSharedPreferences("user", FragmentActivity.MODE_PRIVATE)


        login_forget_password_txt.setOnClickListener {
            val intent = Intent(this,ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermission()

        callbackManager = CallbackManager.Factory.create()

         val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        fonts()

        login_facebook_btn.setOnClickListener { view ->
            facebook()
        }


        login_gplus_btn.setOnClickListener{ view ->
            googlePlus()
        }


        login_linkedin_btn.setOnClickListener { view ->
            linkedIn()
        }


        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)


        login_activity.setOnTouchListener { view, motionEvent ->
            val utils = Utils()
            utils.hideSoftKeyboard(this)
            return@setOnTouchListener false
        }

            login_sign_in_btn.setOnClickListener { view ->

            if(login_email_txt.text.isNotEmpty())
            {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(login_email_txt.text).matches()) {
                    if (login_password_txt.text.length >= 6)
                    {
                        if (utils.isOnline(this))
                        {
                            login(login_email_txt.text.toString(), login_password_txt.text.toString())
                        }
                        else
                        {
                            Toast.makeText(this, "Internet Connection Problem. Please check your Wifi or Data network.", Toast.LENGTH_SHORT).show()
                        }

                    }
                    else
                    {
                        Toast.makeText(this, "Password must be six characters.", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show()
            }
        }

        login_sign_up_txt.setOnClickListener { view ->
            val mainIntent = Intent(this, SignUpActivity::class.java)
            startActivity(mainIntent)

        }

    }


    fun login(email:String, password:String)
    {
//        val location = LocationManager.g

        login_progress.visibility = View.VISIBLE

        val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        val u_id = sharedpreferences.getString("user_id", "")
        val lat = sharedpreferences.getString("lat","")
        val long = sharedpreferences.getString("long","")

        val request = JSONObject()
        request.put("email",email)
        request.put("password",password)
        request.put("lat",lat)
        request.put("long",long)

        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/userLogin"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                Response.Listener<JSONObject> { response ->

                    Log.d("response",response.toString())

                    login_progress.visibility = View.GONE

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
                                    pushNotificationTask = PushNotificationTask( Applozic.getInstance(this@LoginActivity).deviceRegistrationId, listener, this@LoginActivity)
                                    pushNotificationTask.execute(null as Void?)
                                }


                            }
                            override fun onFailure(registrationResponse: RegistrationResponse, exception:Exception) {
                                // If any failure in registration the callback will come here
                                Toast.makeText(this@LoginActivity,"failed",Toast.LENGTH_SHORT).show()
                            }
                        }
                        ApplozicClient.getInstance(this@LoginActivity).enableNotification()
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

                        editor.putString("user_id", userJson.getInt("u_id").toString())
                        editor.putBoolean("loggedIn",true)
                        editor.putString("lat",latitude.toString())
                        editor.putString("long",longitute.toString())
                        editor.commit()


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
                        Toast.makeText(this,"Invalid username or password.",Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    login_progress.visibility = View.GONE
                    Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }
        ) {

        }
        queue.add(postRequest)
    }


    override fun onBackPressed()
    {

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
    {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager?.onActivityResult(requestCode, resultCode, data)


        LISessionManager.getInstance(applicationContext).onActivityResult(this, requestCode, resultCode, data)


        if (requestCode == RC_SIGN_IN)
        {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }


    fun handleSignInResult(completedTask:Task<GoogleSignInAccount> )
    {
        try
        {
            val account:GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            val id    = account.id
            val name  = account.displayName
            val email = account.email
            var image = account.photoUrl.toString()

            if(image.isNullOrEmpty())
            {
                image = ""
            }

            externalLogin(id!!,3, name!!, email!!, image)


//            Toast.makeText(this,"success",Toast.LENGTH_SHORT).show()
        }
        catch (e:ApiException)
        {
            Toast.makeText(this,e.message+e.statusCode+e.localizedMessage,Toast.LENGTH_SHORT).show()
        }
    }


    fun fonts()
    {

        val opensans_regular = ResourcesCompat.getFont(this, R.font.opensans_regular)

        val montserrat_regular = ResourcesCompat.getFont(this, R.font.montserrat_regular)

        val opensans_semi_bold = ResourcesCompat.getFont(this, R.font.opensans_semi_bold)

        login_email_txt.typeface = montserrat_regular

        login_password_txt.typeface = montserrat_regular

        login_txt.typeface = opensans_semi_bold

        login_no_account_txt.typeface = opensans_regular

        login_sign_up_txt.typeface = opensans_regular

        login_forget_password_txt.typeface = opensans_regular
    }


    fun facebook()
    {

        LoginManager.getInstance().logInWithReadPermissions(
                this,
                Arrays.asList("public_profile", "email"))


        LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {

//                        val locationRequest =

//                        "https//graph.facebook.com/me/picture?access_token=<your_access_token_here>"

//                        https://graph.facebook.com/820882001277849
//                        ?fields=about,fan_count,website

                        val request = GraphRequest.newMeRequest(
                                loginResult.accessToken
                        ) { `object`, response ->
//
                            val profile = Profile.getCurrentProfile()

                            val id = `object`.getInt("id")
                            val email = `object`.getString("email")
                            val name = `object`.getString("name")

                            val image = profile.getProfilePictureUri(200,200)

                            externalLogin(id.toString(),1, name, email , image.toString())
                        }

                        val parameters = Bundle()
                        parameters.putString("fields", "id,name,email")
                        request.parameters = parameters
                        request.executeAsync()

                            }

                    override fun onCancel() {
                        // App code
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                    }
                })
    }


    fun externalLogin(id: String,key:Int, name: String, email: String, image: String)
    {
        login_progress.visibility = View.VISIBLE

        val request = JSONObject()
        //                    Toast.makeText(this,response.toString(),Toast.LENGTH_LONG).show()
        when (key) {
            1 -> request.put("fb_id", id)
            2 -> request.put("li_id", id)
            3 -> request.put("gp_id", id)
        }
        request.put("key", key)
        request.put("name", name)
        request.put("email", email)
        request.put("image", image)

        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/signupWithDifferentAccount"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    login_progress.visibility = View.GONE

                    Log.d("response", response.toString())

                    val code = response.getInt("code")

                    if (code == 101) {

                        val flag = response.getInt("flag")

                        val userJson = response.getJSONObject("user")

                        val usersJson = response.getJSONArray("all_users")

                        val favouriteUsers = userJson.getJSONArray("favourite_users")

                        val favouriteBooks = userJson.getJSONArray("favourite_books")


                        for (i in 0 until usersJson.length())
                        {
                            val allBooks = usersJson.getJSONObject(i).getJSONArray("user_books")

                            val books = ArrayList<Book>()

                            val currentUser =  User(usersJson.getJSONObject(i).getInt("u_id"),
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


                            try {

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
                            }

                            catch (e:JSONException)
                            {
                                e.printStackTrace()
                            }


//                            Utils.distance.add(usersJson.getJSONObject(i).getString("dist_str"))

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



                        try {

                            for (i in 0 until userBooks.length()) {
                                Utils.books.add(Book(userBooks.getJSONObject(i).getInt("b_id"),
                                        userBooks.getJSONObject(i).getInt("u_id"),
                                        userBooks.getJSONObject(i).getString("b_name"),
                                        userBooks.getJSONObject(i).getString("b_author"),
                                        userBooks.getJSONObject(i).getString("b_language"),
                                        userBooks.getJSONObject(i).getString("b_type"),
                                        userBooks.getJSONObject(i).getString("b_image")))
                            }
                        }

                        catch (e:JSONException)
                        {
                            e.printStackTrace()
                        }



                        try {

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
                        }

                        catch (e:JSONException)
                        {
                            e.printStackTrace()
                        }


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
                                    val listener = object: PushNotificationTask.TaskListener {
                                        override fun onSuccess(registrationResponse: RegistrationResponse) {
                                        }
                                        override fun onFailure(registrationResponse: RegistrationResponse, exception:Exception) {
                                        }
                                    }
                                    pushNotificationTask = PushNotificationTask( Applozic.getInstance(this@LoginActivity).deviceRegistrationId, listener, this@LoginActivity)
                                    pushNotificationTask.execute(null as Void?)
                                }

                                val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                                val editor = sharedpreferences.edit()

                                editor.putString("user_id", userJson.getInt("u_id").toString())
                                editor.putBoolean("loggedIn",true)
//                        editor.putString("lat",latitude.toString())
//                        editor.putString("long",longitute.toString())
                                editor.commit()

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)


                            }
                            override fun onFailure(registrationResponse: RegistrationResponse, exception:Exception) {
                                // If any failure in registration the callback will come here
//                                Toast.makeText(this@SplashActivity,"failed",Toast.LENGTH_SHORT).show()
                            }
                        }
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
                        UserLoginTask(user, listener, this).execute(null as Void?)






                        if(flag==1)
                        {
                            val intent = Intent(this,ProfileActivity::class.java)
                            intent.putExtra("external",1)
                            startActivity(intent)
                        }

                        else if(flag==0)
                        {
                            val intent = Intent(this,MainActivity::class.java)
                            startActivity(intent)
                        }

//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "something went wrong", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    login_progress.visibility = View.GONE
                    Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
//                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                }
        ) {

        }
        queue.add(postRequest)
    }


    fun googlePlus()
    {
        val signInIntent = mGoogleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        if (requestCode == LOCATION_PERMISSION)
        {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {

            }
            else
            {
                // Permission request was denied.

            }

        }

    }


    fun locationPermission()
    {

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        LOCATION_PERMISSION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        latitude = location?.latitude
                        longitute =location?.longitude

                        val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor = sharedpreferences.edit()

                        editor.putString("lat",latitude.toString())
                        editor.putString("long",longitute.toString())
                        editor.commit()
                    }
        }
    }



    fun linkedIn()
    {
        LISessionManager.getInstance(applicationContext).init(this, buildScope(), object: AuthListener {
            override fun onAuthSuccess() {

                val url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,picture-url,email-address)"
                val apiHelper = APIHelper.getInstance(applicationContext)
                APIHelper.getInstance(this@LoginActivity).getRequest(this@LoginActivity, url, object: ApiListener {
                    override fun onApiSuccess(apiResponse: ApiResponse) {
                        // Success
                       val response =  apiResponse.responseDataAsJson

                        val id = response.getString("id")
                        val name = response.getString("firstName")+" "+response.getString("lastName")
                        val email = response.getString("emailAddress")
                        val image = response.getString("pictureUrl")

                        externalLogin(id,2,name,email,image)

                    }
                    override fun onApiError(liApiError: LIApiError) {
                        // Error making GET request!
                        Toast.makeText(this@LoginActivity,liApiError.message,Toast.LENGTH_LONG).show()
                    }
                })

            }
            override fun onAuthError(error: LIAuthError) {
                Toast.makeText(this@LoginActivity,error.toString(),Toast.LENGTH_SHORT).show()
            }
        }, true)
    }


    fun buildScope(): Scope {
        return Scope.build(Scope.R_EMAILADDRESS,Scope.R_BASICPROFILE)
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

        val sharedPref = getSharedPreferences("user",Context.MODE_PRIVATE)
//        val u_email = sharedPref.getString("user_email","")

        val u_email = Utils.user.u_email

        request.put("email",u_email)


//        Toast.makeText(activity!!,request.toString(), Toast.LENGTH_LONG).show()

        login_progress.visibility =View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/resendCode"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                Response.Listener<JSONObject> { response ->

                    login_progress.visibility =View.INVISIBLE
                    //                    Toast.makeText(activity!!,response.toString(), Toast.LENGTH_LONG).show()

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

                    login_progress.visibility =View.INVISIBLE
                }
        ) {


        }
        queue.add(postRequest)
    }



}
