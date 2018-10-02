package com.sprint.krizma

import android.Manifest
import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.MediaStore
import android.support.v4.app.FragmentActivity
import android.support.v4.view.ViewPager
import android.view.View
import kotlinx.android.synthetic.main.activity_profile.*
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sprint.krizma.Adapter.ProfilePagerAdapter
import kotlinx.android.synthetic.main.profile_fragment_describe.*
import kotlinx.android.synthetic.main.profile_fragment_info.*
import kotlinx.android.synthetic.main.profile_fragment_interest.*
import kotlinx.android.synthetic.main.profile_fragment_verify.*
import org.json.JSONObject
import android.content.DialogInterface
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Address
import android.location.Geocoder
import android.support.v4.app.ActivityCompat
import android.util.Base64
import android.util.Log
import com.bumptech.glide.util.Util
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.zxing.common.StringUtils
import com.hootsuite.nachos.NachoTextView
import com.sprint.krizma.Adapter.DataModel.ProfileBooks
import com.sprint.krizma.Adapter.DataModel.ProfileInfo
import com.sprint.krizma.Adapter.DataModel.ProfileInterest
import com.sprint.krizma.Adapter.DataModel.User
import kotlinx.android.synthetic.main.profile_fragment_books.*
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
//import org.apache.commons.lang3.StringUtils


class ProfileActivity : FragmentActivity() {

    val REQUEST_CAMERA = 1

    val REQUEST_GALLERY = 2

    val CAMERA_PERMISSION = 10

    val STORAGE_PERMISSION = 11

    val utils = Utils()

    var book = 1

    var books = 1

    var bookType = "Both"

    var booksType = "Both"


    var profilePicRequest = false

    var bookImageRequest = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)



        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        fonts()

        profile_activity.setOnTouchListener { view, motionEvent ->
            utils.hideSoftKeyboard(this)
            return@setOnTouchListener false
        }

        val pager = ProfilePagerAdapter(supportFragmentManager, this)

        profile_fragment.adapter = pager
        profile_fragment.beginFakeDrag()

        val intent:Bundle = intent.extras

        if(!intent.isEmpty)
        {
            if(intent.get("external")==1)
            {
                profile_fragment.currentItem = profile_fragment.currentItem + 1
            }
        }


        profile_skip_txt.setOnClickListener { view ->
            if(profile_fragment.currentItem==0||profile_fragment.currentItem==4)
            {
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
            else
            {
                skipDialog()
            }

        }

        profile_next_btn.setOnClickListener{

            when {
                profile_fragment.currentItem==0 -> verifyEmail()
                profile_fragment.currentItem==1 -> {profileInfo()
                    profilePicRequest = false}
                profile_fragment.currentItem==2 -> profileDescribe()
                profile_fragment.currentItem==3 -> profileInterest()
                profile_fragment.currentItem==4 -> {val intent = Intent(this,MainActivity::class.java)
                                                    startActivity(intent)}

//                profile_fragment.currentItem==0 -> profile_fragment.currentItem = profile_fragment.currentItem + 1
//                profile_fragment.currentItem==1 -> profile_fragment.currentItem = profile_fragment.currentItem + 1
//                profile_fragment.currentItem==2 -> profile_fragment.currentItem = profile_fragment.currentItem + 1
//                profile_fragment.currentItem==3 -> profile_fragment.currentItem = profile_fragment.currentItem + 1
            }
        }

        profile_back_btn.setOnClickListener {
            profile_fragment.currentItem = profile_fragment.currentItem - 1
        }

        profile_fragment.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) =


                    when (position) {
                        0 -> {
                            profile_back_btn.visibility = View.INVISIBLE
                            profile_basic_txt.text = "VERIFY EMAIL"
                            profile_next_btn.setImageResource(R.drawable.profile_next_btn)
//                            verifyEmail()


                        }
                        1 -> {
                            profile_back_btn.visibility = View.INVISIBLE
                            profile_basic_txt.text = "Basic Info"
                            profile_skip_txt.setTextColor(Color.parseColor("#ffffff"))
                            profile_next_btn.setImageResource(R.drawable.profile_next_btn)
//                            profileInfo()
                        }
                        2 -> {


                            profile_skip_txt.setTextColor(Color.parseColor("#1f97f3"))
                            profile_back_btn.visibility = View.VISIBLE
                            profile_next_btn.setImageResource(R.drawable.profile_next_btn)
//                            profileDescribe()
                        }
                        3 -> {

                            profile_skip_txt.setTextColor(Color.parseColor("#1f97f3"))
                            profile_back_btn.visibility = View.VISIBLE
                            profile_next_btn.setImageResource(R.drawable.profile_finished)
//                            profileInterest()
                        }
                        4 -> {

                            profile_skip_txt.setTextColor(Color.parseColor("#1f97f3"))
                            profile_back_btn.visibility = View.VISIBLE
                            profile_next_btn.setImageResource(R.drawable.profile_finished)

//                            profileBooks()
                        }
                        else ->
                        {
                            profile_skip_txt.setTextColor(Color.parseColor("#1f97f3"))
                            profile_back_btn.visibility = View.VISIBLE

                        }
                    }

            override fun onPageSelected(position: Int) {
//
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
    {
        if (requestCode == CAMERA_PERMISSION) {
            // Request for camera permission.
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
//                camera()
                Utils.camera(this)

            } else {
                // Permission request was denied.

            }
        }
        else if(requestCode == STORAGE_PERMISSION)
        {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
//                gallery()
                Utils.gallery(this)

            } else {
                // Permission request was denied.

            }
        }
    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
//        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
//            try {
//                val photo = data.extras.get("data") as Bitmap
//
//                val resized = Bitmap.createScaledBitmap(
//                        photo, 222, 222, false)
//
//                if(profilePicRequest)
//                {
//                    profile_pic.setImageBitmap(resized)
//                    profilePicChanged = true
//                }
//
//                else if(bookImageRequest)
//                {
//                    profile_book_btn.setImageBitmap(resized)
//                    bookImageChanged = true
//                }
//
//
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//        else if(requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
//            try {
//                val imageUri = data.data
//                val imageStream = contentResolver.openInputStream(imageUri)
//                val selectedImage = BitmapFactory.decodeStream(imageStream)
//                val resized = Bitmap.createScaledBitmap(
//                        selectedImage, 222, 222, false)
//
//                if(profilePicRequest)
//                {
//                    profile_pic.setImageBitmap(resized)
//                    profilePicChanged = true
//                }
//                else if(bookImageRequest)
//                {
//                    profile_book_btn.setImageBitmap(resized)
//                    bookImageChanged = true
//                }
//
//
//
//            } catch (e: FileNotFoundException) {
//                e.printStackTrace()
//            }
//        }
//
//    }


    fun fonts()
    {
        val montserrat_bold = ResourcesCompat.getFont(this, R.font.montserrat_bold)

        val montserrat_regular = ResourcesCompat.getFont(this, R.font.montserrat_regular)

        profile_basic_txt.typeface = montserrat_bold

        profile_skip_txt.typeface = montserrat_regular
    }



    fun verifyEmail()
    {
        val sharedPref = getSharedPreferences("user",Context.MODE_PRIVATE)
        val user_id = sharedPref.getString("user_id","")

        profile_verify_progress.visibility = View.VISIBLE

        val pin = verify_ch1.text.toString()+verify_ch2.text.toString()+verify_ch3.text.toString()+verify_ch4.text.toString()

        val request = JSONObject()

        request.put("u_id",user_id)
        request.put("pin",pin)




//        Toast.makeText(this,request.toString(),Toast.LENGTH_LONG).show()


        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/verifyEmail"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                Response.Listener<JSONObject> { response ->

                    profile_verify_progress.visibility = View.INVISIBLE

                    val code = response.getInt("code")

                    if(code==101)
                    {
                        val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor = sharedpreferences.edit()
                        editor.putBoolean("user_verified", true)
                        editor.commit()

                        profile_fragment.currentItem = profile_fragment.currentItem + 1
                    }
                    else
                    {
                        Toast.makeText(this,"Please enter a valid code", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    profile_verify_progress.visibility = View.INVISIBLE
                    Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()

                }
        ) {


        }
        queue.add(postRequest)
    }


    fun profileInfo()
    {
//        profile_pic.setOnClickListener { view ->
//            imageDialog(1)
//        }
//
//
//        profile_status_btn.setOnClickListener { view ->
//
//            var profile_status_bool = true
//
//            if(profile_status_bool)
//            {
//                profile_status_btn.setImageResource(R.drawable.profile_bookstore_select_btn)
//                profile_status = "Bookstore"
//                profile_status_bool = false
//            }
//            else
//            {
//                profile_status_btn.setImageResource(R.drawable.profile_individual_select_btn)
//                profile_status = "Individual"
//                profile_status_bool = true
//            }
//
//        }
//
//        profile_female_btn.setOnClickListener { view ->
//
//            profile_female_btn.isChecked = true
//            profile_male_btn.isChecked = false
//            profile_gender = "Female"
//            profile_female_btn.setButtonDrawable(R.drawable.profile_radio_active)
//            profile_male_btn.setButtonDrawable(R.drawable.profile_radio_inactive)
//        }
//
//        profile_male_btn.setOnClickListener { view ->
//
//            profile_male_btn.isChecked = true
//            profile_female_btn.isChecked = false
//            profile_gender = "Male"
//            profile_male_btn.setButtonDrawable(R.drawable.profile_radio_active)
//            profile_female_btn.setButtonDrawable(R.drawable.profile_radio_inactive)
//        }
//
//        profile_other_btn.setOnClickListener { view ->
//
//            profile_other_btn.isChecked = true
//            profile_near_btn.isChecked = false
//            profile_location = "Location"
//            profile_other_btn.setButtonDrawable(R.drawable.profile_radio_active)
//            profile_near_btn.setButtonDrawable(R.drawable.profile_radio_inactive)
//        }
//
//        profile_near_btn.setOnClickListener { view ->
//
//            profile_near_btn.isChecked = true
//            profile_other_btn.isChecked = false
//            profile_location = "Near Me"
//            profile_near_btn.setButtonDrawable(R.drawable.profile_radio_active)
//            profile_other_btn.setButtonDrawable(R.drawable.profile_radio_inactive)
//        }

//        utils.profile_name     = profile_name_txt.text.toString()
//        utils.profile_status   = profile_status
//        utils.profile_gender   = profile_gender
//        utils.profile_age      = profile_age_txt.text.toString()
//        utils.profile_location = profile_location
//        utils.profile_country  = profile_country_txt.text.toString()
//        utils.profile_city     = profile_city_txt.text.toString()
//        utils.profile_area     = profile_area_txt.text.toString()




        if(profile_name_txt.text.isNotEmpty())
        {
            if(profile_age_txt.text.isNotEmpty())
            {
                if(profile_country_txt.text.isNotEmpty())
                {
                    if(profile_city_txt.text.isNotEmpty())
                    {
                        if(profile_area_txt.text.isNotEmpty())
                        {
//                            if(profilePicChanged)
//                            {

//                                val drawable = profile_pic.drawable as BitmapDrawable
//                                val profilePic = drawable.bitmap
//
//                                val userImage = getStringImage(profilePic)
                                val address = profile_country_txt.text.toString()+" "+profile_city_txt.text.toString()+" "+profile_area_txt.text.toString()

                                val lat = getLocationFromAddress(this,address)?.latitude
                                val long = getLocationFromAddress(this,address)?.longitude

                                val info = ProfileInfo(profile_name_txt.text.toString(),
                                        Utils.profilePic,
                                        Utils.profile_status,
                                        Utils.profile_gender,
                                        profile_age_txt.text.toString(),
                                        Utils.profile_location,
                                        profile_country_txt.text.toString(),
                                        profile_city_txt.text.toString(),
                                        profile_area_txt.text.toString(),
                                        lat,
                                        long)

                                utils.profile_info = info

                                profile_fragment.currentItem = profile_fragment.currentItem + 1
//                            }
//                            else
//                            {
//                                Toast.makeText(this,"please upload Profile picture",Toast.LENGTH_LONG).show()
//                            }

                        }

                        else
                        {
                            Toast.makeText(this,"Please enter your area.",Toast.LENGTH_LONG).show()
                        }
                    }

                    else

                    {
                        Toast.makeText(this,"Please enter your city.",Toast.LENGTH_LONG).show()
                    }
                }

                else
                {
                    Toast.makeText(this,"Please enter your country.",Toast.LENGTH_LONG).show()
                }
            }

            else
            {
                Toast.makeText(this,"Please enter your age.",Toast.LENGTH_LONG).show()
            }
        }

        else
        {
            Toast.makeText(this,"Please enter your name.",Toast.LENGTH_LONG).show()
        }

    }


    fun profileDescribe()
    {
        utils.profile_description = profile_describe_txt.text.toString()
        profile_fragment.currentItem = profile_fragment.currentItem+1
    }


    fun profileInterest()
    {
//        profile_book_type_btn.setOnClickListener { view ->
//            when (book) {
//                1 -> {
//                    profile_book_type_btn.setImageResource(R.drawable.profile_both_active_btn)
//                    book = 2
//                    bookType = "Both"
//                }
//                2 -> {
//                    profile_book_type_btn.setImageResource(R.drawable.profile_used_active_bg)
//                    book = 3
//                    bookType = "Used"
//                }
//                3 -> {
//                    profile_book_type_btn.setImageResource(R.drawable.profile_new_active_btn)
//                    book = 1
//                    bookType = "New"
//                }
//            }
//        }

//        utils.profile_languages = profile_language_txt.text.toString()
//        utils.profile_genres    = profile_genre_txt.text.toString()
//        utils.profile_authors   = profile_author_txt.text.toString()

//
//        if(profile_language_txt.text.isNotEmpty())
//        {
//            if(profile_genre_txt.text.isNotEmpty())
//            {
//                if (profile_author_txt.text.isNotEmpty())
//
//          {

        val profile_language_txt = findViewById<NachoTextView>(R.id.profile_language_txt)
        val profile_genre_txt = findViewById<NachoTextView>(R.id.profile_genre_txt)
        val profile_author_txt = findViewById<NachoTextView>(R.id.profile_author_txt)

                    val languages = android.text.TextUtils.join(",",profile_language_txt.allChips)//;profile_language_txt.allChips.toString()
                    val authors = android.text.TextUtils.join(",",profile_author_txt.allChips)
                    val genres = android.text.TextUtils.join(",",profile_genre_txt.allChips)

//                    if (languages.endsWith(",")) {
//                        languages = languages.substring(0, languages.length-1)
//                    }

//                    if (authors.endsWith(",")) {
//                        authors = authors.substring(0, authors.length-1)
//                    }

//                    if (genres.endsWith(",")) {
//                        genres = genres.substring(0, genres.length-1)
//                    }

                    val interest = ProfileInterest(languages,
                            genres,
                            authors,
                            bookType)

                    utils.profile_interest = interest

                    profile_interest_progress.visibility = View.VISIBLE


                    val sharedPref = getSharedPreferences("user",MODE_PRIVATE)
                    val u_id = sharedPref.getString("user_id", "")

                    if(!utils.profilePicChanged)
                    {
                        utils.profile_info.image = ""
                    }

                    val request = JSONObject()

                    request.put("u_id",u_id)
                    request.put("name",utils.profile_info.name)
                    request.put("image",Utils.profilePic)
                    request.put("user_status",utils.profile_info.status)
                    request.put("gender",utils.profile_info.gender)
                    request.put("age",utils.profile_info.age)
                    request.put("location",utils.profile_info.location)
                    request.put("country",utils.profile_info.country)
                    request.put("city",utils.profile_info.city)
                    request.put("area",utils.profile_info.area)
                    request.put("lat",utils.profile_info.lat)
                    request.put("long",utils.profile_info.long)
                    request.put("description",utils.profile_description)
                    request.put("genres",utils.profile_interest.genres)
                    request.put("languages",utils.profile_interest.languages)
                    request.put("authors",utils.profile_interest.authors)
                    request.put("type",utils.profile_interest.type)


                    val queue = Volley.newRequestQueue(this)
                    val url = "http://sprintsols.com/krizma/public/updateUserDetail"
                    val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                            Response.Listener<JSONObject> { response ->

                                Log.d("response",response.toString())

                                profile_interest_progress.visibility = View.GONE
                                //                    Toast.makeText(this,response.toString(), Toast.LENGTH_LONG).show()

                                val code = response.getInt("code")

                                if(code==101)
                                {
                                    Utils.user.u_avatar = utils.profile_info.image

                                    Utils.user.u_name = utils.profile_info.name
                                    Utils.user.u_age = utils.profile_info.age
                                    Utils.user.u_country = utils.profile_info.country
                                    Utils.user.u_gender = utils.profile_info.gender

                                    Utils.user.u_languages = utils.profile_interest.languages
                                    Utils.user.u_genres = utils.profile_interest.genres
                                    Utils.user.u_authors = utils.profile_interest.authors

                                    Toast.makeText(this,"Data saved successfully.",Toast.LENGTH_LONG).show()
                                    profile_fragment.currentItem = profile_fragment.currentItem + 1

                                }

                                else
                                {
                                    Toast.makeText(this,"Something went wrong.",Toast.LENGTH_LONG).show()
                                }

                            },
                            Response.ErrorListener {
                                profile_interest_progress.visibility = View.GONE
                                Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                            }
                    ) {
                    }
                    queue.add(postRequest)
//                }

//                else
//                {
//                    Toast.makeText(this,"Authors should not be empty",Toast.LENGTH_LONG).show()
//                }
//            }

//            else
//            {
//                Toast.makeText(this,"Genres should not be empty",Toast.LENGTH_LONG).show()
//            }
//        }

//        else
//        {
//            Toast.makeText(this,"Languages should not be empty",Toast.LENGTH_LONG).show()
//        }

    }

    fun profileBooks()
    {
//        profile_book_btn.setOnClickListener { view ->
//            imageDialog(4)
//        }
//
//        profile_books_btn.setOnClickListener { view ->
//            when (books) {
//                1 -> {
//                    profile_books_btn.setImageResource(R.drawable.profile_both_active_btn)
//                    books = 2
//                    booksType = "Both"
//                }
//                2 -> {
//                    profile_books_btn.setImageResource(R.drawable.profile_used_active_bg)
//                    books = 3
//                    booksType = "Used"
//                }
//                3 -> {
//                    profile_books_btn.setImageResource(R.drawable.profile_new_active_btn)
//                    books = 1
//                    booksType = "New"
//                }
//            }
//        }

        profile_add_books.setOnClickListener { view ->
//            if (bookImageChanged) {
                if (profile_book_name_txt.text.isNotEmpty()) {
                    if (profile_book_author_txt.text.isNotEmpty()) {
                        if (profile_book_language_txt.text.isNotEmpty()) {
                            val drawable = profile_book_btn.drawable as BitmapDrawable
                            val book = drawable.bitmap

                            val bookImage = getStringImage(book)


                            val books = ProfileBooks(bookImage, profile_book_name_txt.text.toString(),
                                    profile_book_author_txt.text.toString(),
                                    profile_book_language_txt.text.toString(),
                                    booksType)

                            utils.profile_books = books


                            profile_book_progress.visibility = View.VISIBLE


                            val request = JSONObject()

                            request.put("u_id", "156")
                            request.put("name", utils.profile_books.book_name)
                            request.put("author", utils.profile_books.book_author)
                            request.put("language", utils.profile_books.book_language)
                            request.put("type", utils.profile_books.book_type)
                            request.put("image", utils.profile_books.book_image)


                            val queue = Volley.newRequestQueue(this)
                            val url = "http://sprintsols.com/krizma/public/addBook"
                            val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                                    Response.Listener<JSONObject> { response ->

                                        profile_book_progress.visibility = View.GONE
                                        //                    Toast.makeText(this,response.toString(), Toast.LENGTH_LONG).show()

                                        val code = response.getInt("code")

                                        if (code == 101) {
                                            Toast.makeText(this, "Book added successfully", Toast.LENGTH_LONG).show()

                                            val intent = Intent(this,MainActivity::class.java)
                                                startActivity(intent)

//                                            profile_book_author_txt.setText("")
//                                            profile_book_language_txt.setText("")
//                                            profile_book_name_txt.setText("")
//                                            profile_book_btn.setImageResource(R.drawable.profile_add_pic_btn)


                                        } else {
                                            Toast.makeText(this, "something went wrong", Toast.LENGTH_LONG).show()
                                        }

                                    },
                                    Response.ErrorListener {
                                        profile_book_progress.visibility = View.GONE
                                        Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                                    }


                            ) {
                            }
                            queue.add(postRequest)


                        }
                        else
                        {
                            Toast.makeText(this, "Language should not be empty", Toast.LENGTH_LONG).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(this, "Author should not be empty", Toast.LENGTH_LONG).show()
                    }
                }
                else
                {
                    Toast.makeText(this, "Name should not be empty", Toast.LENGTH_LONG).show()
                }
//            } else {
//                Toast.makeText(this, "Image should not be empty", Toast.LENGTH_LONG).show()
//            }

        }



    }

    fun imageDialog(page:Int)
    {
        if (page==1)
        {
            profilePicRequest = true
        }

        else if(page==4)
        {
            bookImageRequest = true
        }

        val builder = AlertDialog.Builder(this,R.style.AlertDialogCustom)
        builder.setMessage("Select Camera or Gallery")
                .setPositiveButton("Camera") { dialog, id ->
                    Utils.cameraPermission(this)
                }
                .setNeutralButton("Gallery") { dialog, id ->
                    Utils.storagePermission(this)
                }

        // Create the AlertDialog object and return it
        builder.create()

        builder.show()
    }

    fun skipDialog()
    {
        val builder = AlertDialog.Builder(this,R.style.AlertDialogCustom)
        builder.setMessage("Are you sure you want to discard profile changes?")
                .setPositiveButton("Yes") { dialog, id ->
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                .setNeutralButton("No") { dialog, id ->
                    dialog.dismiss()
                }

        builder.create()

        builder.show()
    }

    fun cameraPermission()
    {

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            camera()
        }

    }

    fun storagePermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            gallery()
        }
    }

    fun camera()
    {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (camera.resolveActivity(packageManager) != null) {
            startActivityForResult(camera, REQUEST_CAMERA)
        }
    }

    fun gallery()
    {
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.type = "image/*"
        startActivityForResult(gallery, REQUEST_GALLERY)
    }

    fun getStringImage(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


    fun getLocationFromAddress(context:Context, strAddress:String): LatLng? {
        val coder = Geocoder(context)
        val address:List<Address>
        var p1: LatLng? = null
        try
        {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null)
            {
                Toast.makeText(this,"Unable to find your location. Please enter valid Country/City/Area.",Toast.LENGTH_SHORT).show()
            }
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)

        }
        catch (ex:Exception) {
            ex.printStackTrace()
        }
        return p1
    }

}

