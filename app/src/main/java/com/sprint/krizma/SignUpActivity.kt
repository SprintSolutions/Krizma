package com.sprint.krizma

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.android.volley.Request
import kotlinx.android.synthetic.main.activity_sign_up.*
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import com.android.volley.toolbox.JsonObjectRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.sprint.krizma.Adapter.DataModel.User
import org.json.JSONException
import java.util.prefs.Preferences


class SignUpActivity : AppCompatActivity() {

    val utils = Utils()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val LOCATION_PERMISSION = 5
    var latitude:Double?  = 0.0
    var longitute:Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        fonts()

//        locationPermission()


        sign_up_activity.setOnTouchListener { view, motionEvent ->
            val utils = Utils()
            utils.hideSoftKeyboard(this)
            return@setOnTouchListener false
        }


        sign_up_sign_btn.setOnClickListener { view ->

            if (sign_up_username_txt.text.isNotEmpty())
            {
                if(sign_up_email_txt.text.isNotEmpty()) {
                    if (android.util.Patterns.EMAIL_ADDRESS.matcher(sign_up_email_txt.text).matches()) {
                        if (sign_up_password_txt.text.length >= 6) {
                            if (sign_up_password_txt.text.toString() == sign_up_password_confirm_txt.text.toString()) {
                                if (utils.isOnline(this))
                                {
                                    signUp(sign_up_username_txt.text.toString(), sign_up_email_txt.text.toString(), sign_up_password_txt.text.toString())
                                }
                                else
                                {
                                    Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show()
                                }

                            }
                            else
                            {
                                Toast.makeText(this, "Password and confirm password does not match.", Toast.LENGTH_SHORT).show()
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

            else
            {
                Toast.makeText(this,"Please enter your name.", Toast.LENGTH_SHORT).show()
            }


        }



        sign_up_sign_txt.setOnClickListener { view ->
            val mainIntent = Intent(this,LoginActivity::class.java)
            startActivity(mainIntent)
        }
    }

    fun signUp(name:String, email:String, password:String)
    {

        sign_up_progress.visibility = View.VISIBLE

        val request = JSONObject()
        request.put("name",name)
        request.put("email",email)
        request.put("password",password)

        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/signup"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                Response.Listener<JSONObject> { response ->

                    sign_up_progress.visibility = View.GONE

                    val code = response.getInt("code")

                    when (code) {
                        101 -> {

                            val userJson = response.getJSONObject("user")

                            val sharedpreferences = getSharedPreferences("user",MODE_PRIVATE)
                            val editor = sharedpreferences.edit()
                            val user_id  = userJson.getInt("u_id")
                            editor.putString("user_id",user_id.toString())
                            editor.apply()

                            try {

                                 Utils.user = User(
                                        userJson.getInt("u_id"),
                                        userJson.getString("u_name"),
                                        userJson.getString("u_email"),
                                        userJson.getString("u_password"),
                                        userJson.getString("status"),
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
//                                        userJson.getString("created_at"),
//                                        userJson.getString("updated_at")
 )


                                val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                                val editor = sharedpreferences.edit()

                                editor.putString("user_id", userJson.getInt("u_id").toString())
                                editor.commit()

                            }

                            catch (e:JSONException)
                            {
                                e.printStackTrace()
                            }
//                            Toast.makeText(this,"Sign up successful",Toast.LENGTH_LONG).show()

                            val intent = Intent(this,ProfileActivity::class.java)
                            startActivity(intent)

                        }
                        102 -> Toast.makeText(this,"The network connection was lost. Please try again.",Toast.LENGTH_LONG).show()
                        103 -> Toast.makeText(this,"This email belongs to another user.",Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                  sign_up_progress.visibility = View.GONE
//    Toast.makeText(this,it.printStackTrace().toString(),Toast.LENGTH_LONG).show()
//                    Log.d("Error.Response", it.printStackTrace().toString())
                }
        ) {

        }
        queue.add(postRequest)
    }

    fun test()
    {
        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/signup"

// Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->


                    Toast.makeText(this,response.toString(),Toast.LENGTH_LONG).show()
                },
                Response.ErrorListener {  Toast.makeText(this,"That didn't work!",Toast.LENGTH_LONG).show() })

// Add the request to the RequestQueue.
        queue.add(stringRequest)

    }


    fun fonts()
    {
        val opensans_regular = ResourcesCompat.getFont(this, R.font.opensans_regular)
        val montserrat_regular = ResourcesCompat.getFont(this, R.font.montserrat_regular)

        sign_up_username_txt.typeface = montserrat_regular
        sign_up_email_txt.typeface = montserrat_regular
        sign_up_password_txt.typeface = montserrat_regular
        sign_up_password_confirm_txt.typeface = montserrat_regular

        sign_up_account_txt.typeface = opensans_regular
        sign_up_sign_txt.typeface = opensans_regular
    }

    override fun onBackPressed() {

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

                        val sharedPreferences = getSharedPreferences("user",Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("lat",latitude.toString())
                        editor.putString("long",longitute.toString())
                        editor.commit()

//                        Toast.makeText(this,location?.latitude.toString()+" "+location?.longitude.toString(),Toast.LENGTH_SHORT).show()

                        // Got last known location. In some rare situations this can be null.
                    }
        }

    }


}

