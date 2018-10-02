package com.sprint.krizma

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.sprint.krizma.Adapter.DataModel.Book
import com.sprint.krizma.Adapter.DataModel.Data
import com.sprint.krizma.Adapter.DataModel.User
import kotlinx.android.synthetic.main.activity_advance_search.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.text.Charsets.UTF_8

class AdvanceSearchActivity : AppCompatActivity() {

    var cityAdapter:ArrayAdapter<String>? = null
    var location:String = "Other Location"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    val LOCATION_PERMISSION = 5
    var latitude:Double?  = 0.0
    var longitute:Double? = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_advance_search)

        Utils.countries = Data.countries

        val languages = Data.languages

        val genres = Data.genres

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationPermission()

        val languageAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, languages)

        val genreAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, genres)

//        adv_search_language_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
//        adv_search_genre_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)

        adv_search_language_txt.setAdapter(languageAdapter)

        adv_search_genre_txt.setAdapter(genreAdapter)

        adv_search_location.setOnCheckedChangeListener{radioGroup, i ->
            if(i==R.id.adv_search_near_btn)
            {
                adv_search_near_btn.setButtonDrawable(R.drawable.adv_search_radio_active)
                location = adv_search_near_btn.text.toString()
                adv_search_other_btn.setButtonDrawable(R.drawable.adv_search_radio_inactive)
                nearMe()
                adv_search_near_btn.isChecked = true
                adv_search_other_btn.isChecked = false

            }
            else if(i==R.id.adv_search_other_btn)
            {
                adv_search_other_btn.setButtonDrawable(R.drawable.adv_search_radio_active)
                location = adv_search_other_btn.text.toString()
                adv_search_near_btn.setButtonDrawable(R.drawable.adv_search_radio_inactive)
                otherLocation()
                adv_search_near_btn.isChecked = false
                adv_search_other_btn.isChecked = true
            }
        }


        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        val countryAdapter = ArrayAdapter<String>(this,R.layout.spinner_item, Utils.countries)
        countryAdapter.setDropDownViewResource(R.layout.spinner_item)

        adv_search_country_txt.adapter = countryAdapter

        adv_search_country_txt.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent:AdapterView<*>, view:View, pos:Int, id:Long) {

                Utils.cities = ArrayList()
                Utils.cities.add("All Cities")
                getLocalCities(parent.selectedItem.toString())
                cityAdapter = ArrayAdapter(this@AdvanceSearchActivity,R.layout.spinner_item, Utils.cities)
                adv_search_city_txt.adapter = cityAdapter

            }
            override fun onNothingSelected(arg0:AdapterView<*>) {

            }
        }

        adv_search_search_txt.setOnClickListener {
            search()
        }


        search_advance_activity.setOnTouchListener { view, motionEvent ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }


        adv_search_back_btn.setOnClickListener{view ->
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }
    }

    fun fonts(){

        val montserrat_regular = ResourcesCompat.getFont(this, R.font.montserrat_regular)

        adv_search_location_label.typeface = montserrat_regular

        adv_search_near_btn.typeface = montserrat_regular

        adv_search_other_btn.typeface = montserrat_regular

        adv_search_area_txt.typeface = montserrat_regular

    }


    fun getLocalCities(country: String) {

        adv_search_progress.visibility = View.VISIBLE

        val obj = JSONObject(loadJSONFromAsset(this))

        try {
            val cities = obj.getJSONArray(country)

            for (i in 0 until cities.length()) {
                Utils.cities.add(cities.getString(i))
            }
        }

        catch (e:JSONException)
        {
            e.printStackTrace()
        }


        adv_search_progress.visibility = View.GONE


    }


    fun getCities(country: String) {

        val queue = Volley.newRequestQueue(this)
        val url = "https://raw.githubusercontent.com/David-Haim/CountriesToCitiesJSON/master/countriesToCities.json"

        adv_search_progress.visibility = View.VISIBLE

        val getRequest = object:JsonObjectRequest(Method.GET,url, null,
                Response.Listener<JSONObject> { response ->

                    adv_search_progress.visibility = View.GONE

                    val cities = response.getJSONArray(country)

                    for (i in 0 until cities.length()) {
                        Utils.cities.add(cities.getString(i))
                    }

                    cityAdapter!!.notifyDataSetChanged()

                }, Response.ErrorListener {

            adv_search_progress.visibility = View.GONE

            Toast.makeText(this,"error occurred",Toast.LENGTH_SHORT).show()
        }) {
        }

        queue.add(getRequest)
    }


    fun search(){

        Utils.users = ArrayList()

        val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)

        val u_id = sharedpreferences.getString("user_id", "")
        var lat = sharedpreferences.getString("lat","")
        var long = sharedpreferences.getString("long","")

        if(lat.isEmpty()||long.isEmpty())
        {
            lat = latitude.toString()
            long = longitute.toString()
        }

        var country:String
        var city:String
        var area:String

        if(adv_search_other_btn.isChecked)
        {
            country = adv_search_country_txt.selectedItem.toString()
            city = adv_search_city_txt.selectedItem.toString()
            area = adv_search_area_txt.text.toString()
        }

        else
        {
            country = adv_search_country_txt_text.text.toString()
            city = adv_search_city_txt_text.text.toString()
            area = adv_search_area_txt.text.toString()
        }


        val request = JSONObject()

        if(country=="All Countries")
        {
            country=""
            city=""
            area=""
        }

        else if(city=="All Cities")
        {
            city=""
            area=""
        }


        request.put("lat",lat)
        request.put("long",long)
        request.put("login_id",u_id)
        request.put("loc_flag",0)
        request.put("country",country )
        request.put("city", city)
        request.put("area", area)
        request.put("languages", android.text.TextUtils.join(",",adv_search_language_txt.allChips))
        request.put("genres", android.text.TextUtils.join(",",adv_search_genre_txt.allChips))

        adv_search_progress.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/advanceSearch"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    adv_search_progress.visibility = View.GONE

                    val code = response.getInt("code")

                    if (code == 101) {

                        val usersJson = response.getJSONArray("all_users")

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

                        val intent = Intent(this,MainActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, "something went wrong", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    adv_search_progress.visibility = View.GONE
                    Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }
        ) {
        }
        queue.add(postRequest)
    }



    fun nearMe()
    {
        val geocoder: Geocoder
        val addresses:List<Address>
        geocoder = Geocoder(this, Locale.getDefault())

        val sharedPreferences = this.getSharedPreferences("user",Context.MODE_PRIVATE)

//        val latitude = sharedPreferences.getString("lat","")
//        val longitude = sharedPreferences.getString("long","")

        addresses = geocoder.getFromLocation(latitude!!, longitute!!, 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        try {

            val Laddress = addresses[0].thoroughfare
            val Lcity = addresses[0].locality
            val Lstate = addresses[0].adminArea
            val Lcountry = addresses[0].countryName
            val LpostalCode = addresses[0].postalCode
            val LknownName = addresses[0].adminArea


            adv_search_country_txt_text.setText(Lcountry)
            adv_search_country_txt_text.visibility = View.VISIBLE
            adv_search_country_txt.visibility = View.INVISIBLE

            adv_search_city_txt_text.setText(Lcity)
            adv_search_city_txt_text.visibility = View.VISIBLE
            adv_search_city_txt.visibility = View.INVISIBLE

            adv_search_area_txt.setText(Laddress)
            adv_search_area_txt.inputType = InputType.TYPE_NULL
        }

        catch (e:IndexOutOfBoundsException)
        {
            e.printStackTrace()
            Toast.makeText(this,"Unable to find location",Toast.LENGTH_SHORT).show()
        }

    }

    fun otherLocation()
    {

        adv_search_country_txt_text.visibility = View.GONE
        adv_search_country_txt.visibility = View.VISIBLE


        adv_search_city_txt_text.visibility = View.GONE
        adv_search_city_txt.visibility = View.VISIBLE

        adv_search_area_txt.setText("")
        adv_search_area_txt.inputType = InputType.TYPE_CLASS_TEXT
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


            json = String(buffer, UTF_8)
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
        return json
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
//                        Toast.makeText(this,location?.latitude.toString()+" "+location?.longitude.toString(),Toast.LENGTH_SHORT).show()

                        // Got last known location. In some rare situations this can be null.
                    }
        }

    }
}
