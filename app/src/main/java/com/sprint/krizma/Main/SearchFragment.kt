package com.sprint.krizma.Main


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.sprint.krizma.*
import com.sprint.krizma.Adapter.SearchListAdapter

import kotlinx.android.synthetic.main.main_fragment_search.view.*
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.facebook.login.Login
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sprint.krizma.Adapter.DataModel.Book
import com.sprint.krizma.Adapter.DataModel.User
import com.sprint.krizma.Adapter.UserBookListAdapter
import kotlinx.android.synthetic.main.activity_advance_search.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_user_books.*
import kotlinx.android.synthetic.main.main_fragment_search.*
import okhttp3.internal.Util
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.log


class SearchFragment : Fragment() {

    val utils = Utils()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latitude = 0.0
    var longitute = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.main_fragment_search, container, false)

        fonts(view)

        Utils.usersFilterArray = Utils.users

        view.swipe_refresh.setColorSchemeResources(R.color.statusbar)

        view.swipe_refresh.setOnRefreshListener {
          refresh("Search",context!!,view.search_list,view.search_user_empty)
        }

        if(Utils.users.isEmpty()||Utils.usersFilterArray.isEmpty())
        {
            view.search_user_empty.visibility = View.VISIBLE
        }
        else
        {
            view.search_user_empty.visibility = View.GONE
        }

        val data = ArrayList<String>()
        data.add("by Person")
        data.add("by Book")

//        android.R.layout.simple_dropdown_item_1line

        val dropAdapter = ArrayAdapter<String>(activity!!,android.R.layout.simple_dropdown_item_1line, data)
//        dropAdapter.setDropDownViewResource(R.layout.spinner_person)

        view.search_txt_label.adapter = dropAdapter

        view.search_txt_label.onItemSelectedListener = object:AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent:AdapterView<*>, view1:View, pos:Int, id:Long) {
               val value =  parent.getChildAt(0) as TextView
                value.setTextColor(Color.WHITE)
                value.textSize = 13F

            }
            override fun onNothingSelected(arg0:AdapterView<*>) {

            }
        }

//        view.setOnTouchListener { view, motionEvent ->
//            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
//        }

        val viewManager = LinearLayoutManager(activity)

        val itemDecorator = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(activity!!.getDrawable(R.drawable.divider))

        view.search_list.layoutManager = viewManager
        view.search_list.addItemDecoration(itemDecorator)
        view.search_list.adapter = SearchListAdapter(activity!!,Utils.usersFilterArray, view.search_user_empty)

        view.search_advance_btn.setOnClickListener { view ->
            val intent = Intent(activity,AdvanceSearchActivity::class.java)
            startActivity(intent)
        }


        view.search_txt.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(s: Editable) {
                SearchListAdapter(activity!!,Utils.users,view.search_user_empty).notifyDataSetChanged()
            }
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int) {

            }
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                if(s.isNotEmpty()) {
                    find(s)
                }
                else if(s.isEmpty())
                {

                    Utils.usersFilterArray = Utils.users
                    view!!.search_list.adapter = SearchListAdapter(activity!!,Utils.usersFilterArray,view.search_user_empty)
                    SearchListAdapter(activity!!,Utils.usersFilterArray,view.search_user_empty).notifyDataSetChanged()

                    if(Utils.users.isEmpty()||Utils.usersFilterArray.isEmpty())
                    {
                        view.search_user_empty.visibility = View.VISIBLE
                    }
                    else
                    {
                        view.search_user_empty.visibility = View.GONE
                    }
                }

            }
        })


        view.search_near_txt.setOnClickListener {
            view.search_near_txt.setTextColor(ContextCompat.getColor(activity!!,R.color.blue))
            view.search_recommended_label.setTextColor(ContextCompat.getColor(activity!!,R.color.customGrey))
            nearMe()
        }

        view.search_recommended_label.setOnClickListener {
            view.search_recommended_label.setTextColor(ContextCompat.getColor(activity!!,R.color.blue))
            view.search_near_txt.setTextColor(ContextCompat.getColor(activity!!,R.color.customGrey))
            recommended()
        }



        return view
    }

    fun fonts(view: View){
        val montserrat_regular = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

        val montserrat_medium = ResourcesCompat.getFont(activity!!, R.font.montserrat_medium)

//        view.search_txt.typeface = montserrat_regular
//        view.search_txt_label.typeface = montserrat_regular


        view.search_label_txt.typeface = montserrat_medium
        view.search_near_txt.typeface = montserrat_medium
        view.search_recommended_label.typeface = montserrat_medium
    }

    fun refresh(page:String, context: Context, list: RecyclerView, no_data:TextView)
    {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        Utils.users = ArrayList()
        Utils.books = ArrayList()
        Utils.favouriteBooks = ArrayList()
        Utils.favouriteUsers = ArrayList()


        val sharedpreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)

        if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        latitude = location!!.latitude
                        longitute = location!!.longitude

                    }
        }

        val u_id = sharedpreferences.getString("user_id", "")
        val lat = sharedpreferences.getString("lat","")
        val long = sharedpreferences.getString("long","")

        val request = JSONObject()
        request.put("u_id",u_id)
        request.put("lat",latitude.toString())
        request.put("long",longitute.toString())

        val queue = Volley.newRequestQueue(context)
        val url = "http://sprintsols.com/krizma/public/userLogin"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                Response.Listener<JSONObject> { response ->

                    Log.d("response",response.toString())

                    val code = response.getInt("code")

                    if(code==101)
                    {
                        if (ContextCompat.checkSelfPermission(context,
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


                        for (i in 0 until usersJson.length())
                        {
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


                            currentUser.u_books = books

                            Utils.users.add(currentUser)

                            }

                            catch (e:JSONException)
                            {
                                e.printStackTrace()
                            }

                        }


                        Log.d("books", Utils.users.toString())


                        try {

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
                        }

                    catch (e:JSONException)
                    {
                        e.printStackTrace()
                    }

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


                        val lat = userJson.getString("u_lat")
                        val long = userJson.getString("u_long")

                        val sharedpreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
                        val editor = sharedpreferences.edit()

                        editor.putString("user_id", userJson.getInt("u_id").toString())
                        editor.putString("lat",latitude.toString())
                        editor.putString("long",longitute.toString())
                        editor.commit()


                        if(page=="Search") {
                            view!!.swipe_refresh.isRefreshing = false

                            list.adapter = SearchListAdapter(context, Utils.users,no_data )
                            SearchListAdapter(context, Utils.users, view!!.search_user_empty).notifyDataSetChanged()
                        }

                        else if(page=="Books")
                        {
                            list.adapter = UserBookListAdapter(context,Utils.books,no_data)
                            UserBookListAdapter(context,Utils.books,no_data).notifyDataSetChanged()
                        }

                    }

                    else if(code==103)
                    {
                        Utils.user.u_id = response.getInt("u_id")
                        Utils.user.u_email = response.getString("email")

                    }

                    else if(code==102)
                    {
                        Toast.makeText(context,"Invalid username or password.", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    Toast.makeText(context,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }
        ) {

        }
        queue.add(postRequest)

        Utils.usersFilterArray = Utils.users
    }


    fun find(name:CharSequence)
    {

        Utils.usersFilterArray = ArrayList()



        for (i in 0 until Utils.users.size)
        {
            if(view!!.search_txt_label.selectedItem.toString()=="by Person")
            {
                if (Utils.users[i].u_name!!.contains(name, true))
                {
                    Utils.usersFilterArray.add(Utils.users[i])
                    view!!.search_list.adapter = SearchListAdapter(activity!!, Utils.usersFilterArray, view!!.search_user_empty)
                    SearchListAdapter(activity!!, Utils.usersFilterArray, view!!.search_user_empty).notifyDataSetChanged()
                }
            }
            else if(view!!.search_txt_label.selectedItem.toString()=="by Book")
            {
                if (Utils.users[i].u_books.toString().contains(name, true))
                {
                    Utils.usersFilterArray.add(Utils.users[i])
                    view!!.search_list.adapter = SearchListAdapter(activity!!, Utils.usersFilterArray, view!!.search_user_empty)
                    SearchListAdapter(activity!!, Utils.usersFilterArray, view!!.search_user_empty).notifyDataSetChanged()
                }
            }
        }
    }


    fun nearMe()
    {

        Utils.users = ArrayList()

        val sharedpreferences = context!!.getSharedPreferences("user", Context.MODE_PRIVATE)

        val u_id = sharedpreferences.getString("user_id", "")
        val lat = sharedpreferences.getString("lat","")
        val long = sharedpreferences.getString("long","")

        val request = JSONObject()

        request.put("lat",lat)
        request.put("long",long)
        request.put("login_id",u_id)
        request.put("loc_flag",1)


        view!!.search_progress.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(context)
        val url = "http://sprintsols.com/krizma/public/advanceSearch"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    view!!.search_progress.visibility = View.GONE

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

                            currentUser.u_books = books

                            Utils.users.add(currentUser)

                        }

                        view!!.search_list.adapter = SearchListAdapter(activity!!,Utils.users,view!!.search_user_empty)
                        SearchListAdapter(activity!!,Utils.users,view!!.search_user_empty).notifyDataSetChanged()


                        if(Utils.users.isEmpty()||Utils.usersFilterArray.isEmpty())
                        {
                            view!!.search_user_empty.visibility = View.VISIBLE
                        }
                        else
                        {
                            view!!.search_user_empty.visibility = View.GONE
                        }

                    } else {
                        Toast.makeText(context, "something went wrong", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    adv_search_progress.visibility = View.GONE
                    Toast.makeText(context,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }
        ) {
        }
        queue.add(postRequest)
    }


    fun recommended()
    {

        Utils.users = ArrayList()

        val test = ArrayList<User>()

        val books = ArrayList<String>()

        for (i in 0 until Utils.books.size)
        {
            books.add(Utils.books[i].b_name)
        }

        val sharedpreferences = context!!.getSharedPreferences("user", Context.MODE_PRIVATE)

        val u_id = sharedpreferences.getString("user_id", "")
        val lat = sharedpreferences.getString("lat","")
        val long = sharedpreferences.getString("long","")

        val request = JSONObject()

        request.put("lat",lat)
        request.put("long",long)
        request.put("login_id",u_id)
        request.put("loc_flag",0)
        request.put("languages", Utils.user.u_languages)
        request.put("genres", Utils.user.u_genres)
        request.put("books", android.text.TextUtils.join(",",books)
                )


        view!!.search_progress.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(context)
        val url = "http://sprintsols.com/krizma/public/recomendedSearch"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    view!!.search_progress.visibility = View.GONE

                    val code = response.getInt("code")

                    if (code == 101) {

                        val usersJson = response.getJSONArray("all_users")

                        var flag = true
                        val ids = ArrayList<Int>()

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


                            for (j in 0 until allBooks.length()) {
                                books.add(Book(allBooks.getJSONObject(j).getInt("b_id"),
                                        allBooks.getJSONObject(j).getInt("u_id"),
                                        allBooks.getJSONObject(j).getString("b_name"),
                                        allBooks.getJSONObject(j).getString("b_author"),
                                        allBooks.getJSONObject(j).getString("b_language"),
                                        allBooks.getJSONObject(j).getString("b_type"),
                                        allBooks.getJSONObject(j).getString("b_image"),
                                        allBooks.getJSONObject(j).getInt("favourite_flag")
                                ))
                            }

                            currentUser.u_books = books

                            test.add(currentUser)

                            for(k in 0 until ids.size)
                            {
                                val id = ids[k]
                                if(id==currentUser.u_id)
                                {
                                    flag = false
                                    break
                                }
                            }

                            if(flag)
                            {
                                Utils.users.add(currentUser)
                                ids.add(currentUser.u_id!!)
                            }

                        }


                        view!!.search_list.adapter = SearchListAdapter(activity!!,Utils.users,view!!.search_user_empty)
                        SearchListAdapter(activity!!,Utils.users,view!!.search_user_empty).notifyDataSetChanged()

                        if(Utils.users.isEmpty()||Utils.usersFilterArray.isEmpty())
                        {
                            view!!.search_user_empty.visibility = View.VISIBLE
                        }
                        else
                        {
                            view!!.search_user_empty.visibility = View.GONE
                        }


                    } else {
                        Toast.makeText(context, "something went wrong", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    adv_search_progress.visibility = View.GONE
                    Toast.makeText(context,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }
        ) {
        }
        queue.add(postRequest)
    }
}

