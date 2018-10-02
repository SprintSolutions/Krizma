package com.sprint.krizma.Profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawable
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Transformation
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.makeramen.roundedimageview.RoundedDrawable
import com.sprint.krizma.Adapter.DataModel.Data
import com.sprint.krizma.R
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.fragment_edit_info.*
import kotlinx.android.synthetic.main.fragment_edit_info.view.*
import kotlinx.android.synthetic.main.main_fragment_profile.view.*
import kotlinx.android.synthetic.main.profile_fragment_info.view.*
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*


class EditInfoFragment : Fragment() {

    companion object {
        fun newInstance(): EditInfoFragment {

            return EditInfoFragment()
        }
    }

    var path= ""

    val utils = Utils()


    val REQUEST_CAMERA = 1

    val REQUEST_GALLERY = 2

    val CAMERA_PERMISSION = 10

    val STORAGE_PERMISSION = 11

    var cityAdapter:ArrayAdapter<String>? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val LOCATION_PERMISSION = 5
    var latitude:Double?  = 0.0
    var longitute:Double? = 0.0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_edit_info, container, false)

        fonts(view)

        infoData(view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        locationPermission()


        view.edit_profile_pic.setOnClickListener { view ->
            imageDialog()
        }

        val countries = Data.countries

        val countriesAdapter = ArrayAdapter<String>(context,android.R.layout.simple_dropdown_item_1line,countries)

        view.edit_profile_country_txt.setAdapter(countriesAdapter)

//        view.edit_profile_country_txt.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>, view1:View, pos:Int, id:Long) {
//
//                Utils.cities = ArrayList()
//                getLocalCities(parent.selectedItem.toString())
//                cityAdapter = ArrayAdapter(context,R.layout.spinner_item, Utils.cities)
//                view.edit_profile_city_txt.setAdapter(cityAdapter)
//
//            }
//            override fun onNothingSelected(arg0: AdapterView<*>) {
//
//            }
//        }

        view.edit_profile_country_txt.onItemClickListener = object:AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Utils.cities = ArrayList()
                getLocalCities(view.edit_profile_country_txt.text.toString())
                cityAdapter = ArrayAdapter(context,R.layout.spinner_item, Utils.cities)
                view.edit_profile_city_txt.setAdapter(cityAdapter)
            }

        }



        if(Utils.user.u_user_status!!.isNotEmpty())
        {
            if (Utils.user.u_user_status.equals("Individual"))
            {
                view.edit_profile_status_btn.setImageResource(R.drawable.profile_individual_select_btn)
                Utils.edit_profile_status = "Individual"
//                Utils.edit_profile_status_bool = true
            }
            else if (Utils.user.u_user_status.equals("Bookstore"))
            {
                view.edit_profile_status_btn.setImageResource(R.drawable.profile_bookstore_select_btn)
                Utils.edit_profile_status = "Bookstore"
//                Utils.edit_profile_status_bool = false
            }
        }


//        buttonState(Utils.edit_profile_status_bool,view)



        view.edit_profile_status_btn.setOnClickListener { view ->

//            buttonState(Utils.edit_profile_status_bool,view)

                if (Utils.edit_profile_status_bool) {
                    view.edit_profile_status_btn.setImageResource(R.drawable.profile_individual_select_btn)
                    Utils.edit_profile_status = "Individual"
                    Utils.edit_profile_status_bool = false
                } else {
                    view.edit_profile_status_btn.setImageResource(R.drawable.profile_bookstore_select_btn)
                    Utils.edit_profile_status = "Bookstore"
                    Utils.edit_profile_status_bool = true
                }
            }

//        }

        view.edit_profile_gender.setOnCheckedChangeListener{radioGroup, i ->
            if(i==R.id.edit_profile_female_btn)
            {
                Utils.edit_profile_gender = "Female"
                view.edit_profile_female_btn.setButtonDrawable(R.drawable.profile_radio_active)
                view.edit_profile_male_btn.setButtonDrawable(R.drawable.profile_radio_inactive)
                view.edit_profile_female_btn.isChecked = true
                view.edit_profile_male_btn.isChecked = false
            }
            else if(i==R.id.edit_profile_male_btn)
            {
                Utils.edit_profile_gender = "Male"
                view.edit_profile_male_btn.setButtonDrawable(R.drawable.profile_radio_active)
                view.edit_profile_female_btn.setButtonDrawable(R.drawable.profile_radio_inactive)
                view.edit_profile_female_btn.isChecked = false
                view.edit_profile_male_btn.isChecked = true
            }
        }


        view.edit_profile_location.setOnCheckedChangeListener{radioGroup, i ->
            if(i==R.id.edit_profile_other_btn)
            {
                Utils.edit_profile_location = "Other Location"
                view.edit_profile_other_btn.setButtonDrawable(R.drawable.profile_radio_active)
                view.edit_profile_near_btn.setButtonDrawable(R.drawable.profile_radio_inactive)
                view.edit_profile_other_btn.isChecked = true
                view.edit_profile_near_btn.isChecked = false
                otherLocation(view)
            }
            else if(i==R.id.edit_profile_near_btn)
            {
                Utils.edit_profile_location = "Near Me"
                view.edit_profile_near_btn.setButtonDrawable(R.drawable.profile_radio_active)
                view.edit_profile_other_btn.setButtonDrawable(R.drawable.profile_radio_inactive)
                view.edit_profile_other_btn.isChecked = false
                view.edit_profile_near_btn.isChecked = true
                nearMe(view)
            }
        }


        view.setOnTouchListener { view, motionEvent ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        }

        return view

    }

    fun otherLocation(view: View) {
        view.edit_profile_country_txt.setText("")
        view.edit_profile_city_txt.setText("")
        view.edit_profile_area_txt.setText("")
    }

    fun fonts(view: View)
    {
        val montserrat_regular = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

        view.edit_status_txt.typeface = montserrat_regular

        view.edit_profile_gender_txt.typeface = montserrat_regular

        view.edit_profile_male_btn.typeface = montserrat_regular

        view.edit_profile_female_btn.typeface = montserrat_regular

        view.edit_profile_location_txt.typeface = montserrat_regular

        view.edit_profile_near_btn.typeface = montserrat_regular

        view.edit_profile_other_btn.typeface = montserrat_regular

        view.edit_profile_name_txt.typeface = montserrat_regular

        view.edit_profile_age_txt.typeface = montserrat_regular

        view.edit_profile_country_txt.typeface = montserrat_regular

        view.edit_profile_city_txt.typeface = montserrat_regular

        view.edit_profile_area_txt.typeface = montserrat_regular

    }


    fun infoData(view: View)
    {
        Glide.with(this).asBitmap().load(Utils.user.u_avatar).into(object: SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                view.edit_profile_pic.setImageBitmap(resource)
            }})

        view.edit_profile_name_txt.setText(Utils.user.u_name)

        view.edit_profile_age_txt.setText(Utils.user.u_age)

        view.edit_profile_country_txt.setText(Utils.user.u_country)

        view.edit_profile_city_txt.setText(Utils.user.u_city)

        view.edit_profile_area_txt.setText(Utils.user.u_area)




        if(Utils.user.u_gender.equals("Male"))
        {
            view.edit_profile_male_btn.setButtonDrawable(R.drawable.profile_radio_active)
            Utils.edit_profile_gender = "Male"
            view.edit_profile_male_btn.isChecked = true
            view.edit_profile_female_btn.isChecked = false
        }
        else if(Utils.user.u_gender.equals("Female"))
        {
            view.edit_profile_female_btn.setButtonDrawable(R.drawable.profile_radio_active)
            Utils.edit_profile_gender = "Female"
            view.edit_profile_female_btn.isChecked = true
            view.edit_profile_male_btn.isChecked = false
        }


        if(Utils.user.u_location.equals("Near Me"))
        {
            view.edit_profile_near_btn.setButtonDrawable(R.drawable.profile_radio_active)
            Utils.edit_profile_location = "Near Me"
            view.edit_profile_near_btn.isChecked = true
            view.edit_profile_other_btn.isChecked = false
        }
        else if(Utils.user.u_location.equals("Other Location"))
        {
            view.edit_profile_other_btn.setButtonDrawable(R.drawable.profile_radio_active)
            Utils.edit_profile_location = "Other Location"
            view.edit_profile_near_btn.isChecked = false
            view.edit_profile_other_btn.isChecked = true
        }
    }

    fun imageDialog()
    {

        val builder = AlertDialog.Builder(activity,R.style.AlertDialogCustom)
        builder.setMessage("Select Camera or Gallery")
                .setPositiveButton("Camera") { dialog, id ->
                    cameraPermission()
                }
                .setNeutralButton("Photo Library") { dialog, id ->
                    storagePermission()
                }

        // Create the AlertDialog object and return it
        builder.create()

        builder.show()
    }

    fun cameraPermission()
    {

        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
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
        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
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
        if (camera.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(camera, REQUEST_CAMERA)
        }
    }

    fun gallery()
    {
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.type = "image/*"
        startActivityForResult(gallery, REQUEST_GALLERY)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                val photo = data!!.extras.get("data") as Bitmap

                utils.path = data.data.path

                val resized = Bitmap.createScaledBitmap(
                        photo, 250, 250, false)

                view!!.edit_profile_pic.setImageBitmap(resized)
                utils.profilePicChanged = true

                val drawable = view!!.edit_profile_pic.drawable as BitmapDrawable
                val profilePic = drawable.bitmap

                Utils.profilePic = getStringImage(profilePic)


            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        else if(requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = data!!.data
                val imageStream = activity!!.contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val resized = Bitmap.createScaledBitmap(
                        selectedImage, 250, 250, false)

                utils.path = data.data.path

                view!!.edit_profile_pic.setImageBitmap(resized)
                utils.profilePicChanged = true


                val drawable = view!!.edit_profile_pic.drawable as BitmapDrawable
                val profilePic = drawable.bitmap

                Utils.profilePic = getStringImage(profilePic)


            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun getStringImage(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }


    fun getAddress(view: View)
    {
        val geocoder: Geocoder
        val addresses:List<Address>
        geocoder = Geocoder(activity, Locale.getDefault())

        val sharedPreferences = activity!!.getSharedPreferences("user",Context.MODE_PRIVATE)

        val latitude = sharedPreferences.getString("lat","0.00")
        val longitude = sharedPreferences.getString("long","0.00")



        addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        val Laddress = addresses[0].thoroughfare
        val Lcity = addresses[0].locality
        val Lstate = addresses[0].adminArea
        val Lcountry = addresses[0].countryName
        val LpostalCode = addresses[0].postalCode
        val LknownName = addresses[0].adminArea

        view.edit_profile_country_txt.setText(Lcountry)
        view.edit_profile_city_txt.setText(Lcity)
        view.edit_profile_area_txt.setText(Laddress)

    }

    fun getLocationFromAddress(context:Context, strAddress:String): LatLng {
        val coder = Geocoder(context)
        val address:List<Address>
        var p1: LatLng? = null
        try
        {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null)
            {
                Toast.makeText(activity!!,"Unable to find your location. Please enter valid Country/City/Area.", Toast.LENGTH_SHORT).show()
            }
            val location = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        }
        catch (ex:IOException) {
            ex.printStackTrace()
        }
        return p1!!
    }


//    fun locationPermission()
//    {
//
//        if (ContextCompat.checkSelfPermission(activity!!,
//                        Manifest.permission.ACCESS_COARSE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            // Permission is not granted
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
//                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(activity!!,
//                        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
//                        LOCATION_PERMISSION)
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        } else {
//            fusedLocationClient.lastLocation
//                    .addOnSuccessListener { location : Location? ->
//                        latitude = location?.latitude
//                        longitute =location?.longitude
////                        Toast.makeText(this,location?.latitude.toString()+" "+location?.longitude.toString(),Toast.LENGTH_SHORT).show()
//
//                        // Got last known location. In some rare situations this can be null.
//                    }
//        }
//
//    }

    fun nearMe(view: View)
    {
        val geocoder: Geocoder
        val addresses:List<Address>
        geocoder = Geocoder(context, Locale.getDefault())

        val sharedPreferences = context!!.getSharedPreferences("user",Context.MODE_PRIVATE)

        var lat = sharedPreferences.getString("lat","")
        var long = sharedPreferences.getString("long","")

        if(lat=="0.0"||long=="0.0")
        {
            locationPermission()

            lat = latitude.toString()
            long = longitute.toString()
        }

        addresses = geocoder.getFromLocation(lat.toDouble(), long.toDouble(), 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        try {

            val Laddress = addresses[0].thoroughfare
            val Lcity = addresses[0].locality
            val Lstate = addresses[0].adminArea
            val Lcountry = addresses[0].countryName
            val LpostalCode = addresses[0].postalCode
            val LknownName = addresses[0].adminArea


            view.edit_profile_country_txt.setText(Lcountry)

            view.edit_profile_city_txt.setText(Lcity)

            view.edit_profile_area_txt.setText(Laddress)
        }
        catch (e:IndexOutOfBoundsException)
        {
            e.printStackTrace()
            Toast.makeText(activity,"Unable to find location",Toast.LENGTH_SHORT).show()
        }

    }

    fun locationPermission()
    {

        if (ContextCompat.checkSelfPermission(context!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
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

                        val sharedpreferences = context!!.getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor = sharedpreferences.edit()

                        editor.putString("lat",latitude.toString())
                        editor.putString("long",longitute.toString())
                        editor.commit()
                    }
        }
    }

    fun getLocalCities(country: String) {

//        adv_search_progress.visibility = View.VISIBLE

        val obj = JSONObject(loadJSONFromAsset(context!!))

        try {
            val cities = obj.getJSONArray(country)

            for (i in 0 until cities.length()) {
                Utils.cities.add(cities.getString(i))
            }
        }

        catch (e: JSONException)
        {
            e.printStackTrace()
        }


//        cityAdapter!!.notifyDataSetChanged()

//        adv_search_progress.visibility = View.GONE


//        adv_search_progress.visibility = View.GONE

//        Toast.makeText(this,"error occurred",Toast.LENGTH_SHORT).show()



    }


    fun loadJSONFromAsset(context:Context): String? {
        val json:String
        try
        {
            val cities = resources.openRawResource(R.raw.countries)

            val size = cities.available()

            val buffer = ByteArray(size)

            cities.read(buffer)

            cities.close()


            json = String(buffer, Charsets.UTF_8)
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
    }


    fun buttonState(boolean: Boolean,view: View)
    {
        if(boolean)
        {
            view.edit_profile_status_btn.setImageResource(R.drawable.profile_individual_select_btn)
            Utils.edit_profile_status = "Individual"
            Utils.edit_profile_status_bool = true
        }
        else
        {
            view.edit_profile_status_btn.setImageResource(R.drawable.profile_bookstore_select_btn)
            Utils.edit_profile_status = "Bookstore"
            Utils.edit_profile_status_bool = false
        }
    }
}
