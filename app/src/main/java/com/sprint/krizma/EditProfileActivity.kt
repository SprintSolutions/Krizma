package com.sprint.krizma

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.maps.model.LatLng
import com.hootsuite.nachos.NachoTextView
import com.sprint.krizma.Adapter.DataModel.ProfileInfo
import com.sprint.krizma.Adapter.DataModel.ProfileInterest
import com.sprint.krizma.Adapter.EditProfilePagerAdapter
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.fragment_edit_description.*
import kotlinx.android.synthetic.main.fragment_edit_info.*
import kotlinx.android.synthetic.main.fragment_edit_info.view.*
import kotlinx.android.synthetic.main.fragment_edit_interest.*
import kotlinx.android.synthetic.main.profile_fragment_interest.view.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class EditProfileActivity : AppCompatActivity() {

    val REQUEST_CAMERA = 1

    val REQUEST_GALLERY = 2

    val CAMERA_PERMISSION = 10

    val STORAGE_PERMISSION = 11

    val utils = Utils()

    var edit_profile_status = "Individual"




    var edit_book = 1

    var edit_bookType = "Both"




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)


//        infoData()

        edit_profile_skip_txt.setOnClickListener {
            skipDialog()
        }


        val pager = EditProfilePagerAdapter(supportFragmentManager, this)

        edit_profile_fragment.adapter = pager
        edit_profile_fragment.beginFakeDrag()

        edit_profile_next_btn.setOnClickListener{

            when {

                edit_profile_fragment.currentItem==0 -> profileInfo()
                edit_profile_fragment.currentItem==1 -> profileDescribe()
                edit_profile_fragment.currentItem==2 -> profileInterest()
            }
        }

        edit_profile_back_btn.setOnClickListener {
            if(edit_profile_fragment.currentItem==0) {
                skipDialog()
            }
            else {
                edit_profile_fragment.currentItem = edit_profile_fragment.currentItem - 1
            }
        }

        edit_profile_fragment.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) =

                    when (position) {
                        0 -> {
                            edit_profile_back_btn.visibility = View.INVISIBLE
                            edit_profile_next_btn.setImageResource(R.drawable.profile_next_btn)
//                            profileInfo()
                            edit_profile_back_btn.visibility = View.VISIBLE
                        }
                        1 -> {
                            edit_profile_back_btn.visibility = View.VISIBLE
                            edit_profile_basic_txt.text = "Basic Info"
                            edit_profile_skip_txt.setTextColor(Color.parseColor("#ffffff"))
//                            profileDescribe()
                            edit_profile_next_btn.setImageResource(R.drawable.profile_next_btn)
                        }
                        2 ->
                        {
                            edit_profile_skip_txt.setTextColor(Color.parseColor("#1f97f3"))
                            edit_profile_back_btn.visibility = View.VISIBLE
//                            profileInterest()
                            edit_profile_next_btn.setImageResource(R.drawable.profile_finished)
                        }

                        else ->
                        {
                            edit_profile_skip_txt.setTextColor(Color.parseColor("#1f97f3"))
                            edit_profile_back_btn.visibility = View.VISIBLE
                            edit_profile_next_btn.setImageResource(R.drawable.profile_next_btn)


                        }
                    }

            override fun onPageSelected(position: Int) {
//
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }




    fun profileInfo()
    {

//
    val drawable = edit_profile_pic.drawable as? BitmapDrawable
    val edit_profilePic = drawable?.bitmap

        val address = edit_profile_country_txt.text.toString()+" "+edit_profile_city_txt.text.toString()+" "+edit_profile_area_txt.text.toString()

    val lat = getLocationFromAddress(this,address)?.latitude
    val long = getLocationFromAddress(this,address)?.longitude

    val edit_info = ProfileInfo(edit_profile_name_txt.text.toString(),
           Utils.profilePic,
            Utils.edit_profile_status,
            Utils.edit_profile_gender,
            edit_profile_age_txt.text.toString(),
            Utils.edit_profile_location,
            edit_profile_country_txt.text.toString(),
            edit_profile_city_txt.text.toString(),
            edit_profile_area_txt.text.toString(),
            lat,
            long)

    utils.profile_info = edit_info

    edit_profile_fragment.currentItem = edit_profile_fragment.currentItem + 1

    }


    fun profileDescribe()
    {
        utils.profile_description = edit_profile_describe_txt.text.toString()
        edit_profile_fragment.currentItem = edit_profile_fragment.currentItem+1
    }


    fun profileInterest()
    {
        edit_profile_type_new.setOnClickListener { view1 ->
            edit_profile_type_new.setImageResource(R.drawable.profile_new_active)
            edit_profile_type_used.setImageResource(R.drawable.profile_used_deactive)
            edit_profile_type_both.setImageResource(R.drawable.profile_both_deactive)
            edit_book = 2
            edit_bookType = "New"
        }

        edit_profile_type_used.setOnClickListener { view1 ->
            edit_profile_type_new.setImageResource(R.drawable.profile_new_deactive)
            edit_profile_type_used.setImageResource(R.drawable.profile_used_active)
            edit_profile_type_both.setImageResource(R.drawable.profile_both_deactive)
            edit_book = 2
            edit_bookType = "Used"
        }

        edit_profile_type_both.setOnClickListener { view1 ->
            edit_profile_type_new.setImageResource(R.drawable.profile_new_deactive)
            edit_profile_type_used.setImageResource(R.drawable.profile_used_deactive)
            edit_profile_type_both.setImageResource(R.drawable.profile_both_active)
            edit_book = 2
            edit_bookType = "Both"
        }


        val edit_languages = android.text.TextUtils.join(",",edit_profile_language_txt.allChips)//;profile_language_txt.allChips.toString()
        val edit_authors = android.text.TextUtils.join(",",edit_profile_author_txt.allChips)
        val edit_genres = android.text.TextUtils.join(",",edit_profile_genre_txt.allChips)

        val edit_interest = ProfileInterest(edit_languages,
                                            edit_genres,
                                            edit_authors,
                                            Utils.edit_bookType)

                    utils.profile_interest = edit_interest


                    val sharedPref = getSharedPreferences("user", FragmentActivity.MODE_PRIVATE)
                    val u_id = sharedPref.getString("user_id", "")

                    val request = JSONObject()

                    request.put("u_id",u_id)
                    request.put("name",utils.profile_info.name)
                    request.put("image",utils.profile_info.image)
                    request.put("user_status",utils.profile_info.status)
                    request.put("gender",utils.profile_info.gender)
                    request.put("age",utils.profile_info.age)
                    request.put("location",utils.profile_info.location)
                    request.put("country",utils.profile_info.country)
                    request.put("city",utils.profile_info.city)
                    request.put("area",utils.profile_info.area)
                    request.put("description",utils.profile_description)
                    request.put("genres",utils.profile_interest.genres)
                    request.put("languages",utils.profile_interest.languages)
                    request.put("authors",utils.profile_interest.authors)
                    request.put("type",utils.profile_interest.type)


                    val queue = Volley.newRequestQueue(this)
                    val url = "http://sprintsols.com/krizma/public/updateUserDetail"
                    val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                            Response.Listener<JSONObject> { response ->

                                val code = response.getInt("code")

                                if(code==101)
                                {
                                    Utils.user.u_name = utils.profile_info.name
                                    Utils.user.u_age = utils.profile_info.age
                                    Utils.user.u_gender = utils.profile_info.gender
                                    Utils.user.u_avatar = response.getString("image")
                                    Utils.user.u_country = utils.profile_info.country
                                    Utils.user.u_genres = utils.profile_interest.genres
                                    Utils.user.u_authors = utils.profile_interest.authors
                                    Utils.user.u_languages = utils.profile_interest.languages


                                    Toast.makeText(this,"Data updated successfully.", Toast.LENGTH_LONG).show()

                                    val intent = Intent(this,MainActivity::class.java)
                                        intent.putExtra("profile","profile")
                                        startActivity(intent)

                                }

                                else
                                {
                                    Toast.makeText(this,"Something went wrong", Toast.LENGTH_LONG).show()
                                }

                            },
                            Response.ErrorListener {
                                Toast.makeText(this,it.message.toString(), Toast.LENGTH_LONG).show()
                            }
                    ) {
                    }
                    queue.add(postRequest)
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


    fun getLocationFromAddress(context: Context, strAddress:String): LatLng? {
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


