package com.sprint.krizma.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.bumptech.glide.Glide
import com.sprint.krizma.Adapter.DataModel.User
import com.sprint.krizma.R
import com.sprint.krizma.AuthorActivity
import com.sprint.krizma.Utils
import org.json.JSONObject

import android.widget.TextView
import android.widget.RelativeLayout
import com.bumptech.glide.request.RequestOptions
import com.sprint.krizma.MainActivity
import java.util.*


class SearchListAdapter


(internal var context: Context, internal var users: ArrayList<User>, internal var no_data: TextView) : Adapter<SearchListAdapter.SearchViewHolder>() {

    var mInflater:LayoutInflater

    internal var more = false

    init{
        mInflater = LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = mInflater.inflate(R.layout.list_people,parent,false)

        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return users.size
    }



    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = users[position]

        if(users.isEmpty())
        {
            no_data.visibility = View.VISIBLE
        }
        else
        {
            no_data.visibility = View.GONE
        }

        val opened = BooleanArray(users.size)

        holder.name.text = user.u_name
        holder.age.text = user.u_age
        holder.distance.text = user.u_distance+" KM"
        holder.gender.text = user.u_gender
        holder.location.text = user.u_area+", "+user.u_city+", "+user.u_country
        holder.languages.text = user.u_languages
        holder.genres.text = user.u_genres
        holder.authors.text = user.u_authors

        if(user.u_favourite_flag==1)
        {
            holder.favourite.setImageResource(R.drawable.search_favourite_active_btn)
        }

        if(user.u_avatar!!.isNotEmpty()) {
            Glide.with(context).load(user.u_avatar).apply(RequestOptions().placeholder(R.drawable.search_profile_pic)).into(holder.profile)
        }

        else {
            Glide.with(context).load(R.drawable.user_profile_dummy_pic).into(holder.profile)
        }

        holder.see_more.setOnClickListener {
            if(!opened[position]) {
                holder.more_data.visibility = View.VISIBLE
                holder.see_more.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.search_minus_icon, 0)
                opened[position] = true
            }
            else if(opened[position]) {
                holder.more_data.visibility = View.GONE
                holder.see_more.setCompoundDrawablesWithIntrinsicBounds( 0, 0, R.drawable.search_plus_icon, 0)
                opened[position] = false
            }
        }

        holder.favourite.setOnClickListener{
            if(user.u_favourite_flag==0) {
                addToFavourite(user.u_id!!, holder.favourite, holder.progress, position, user)
            }
            else if(user.u_favourite_flag==1) {
                val builder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
                builder.setMessage("Are you sure you want to remove this author from favourites?")
                        .setPositiveButton("Yes") { dialog, id ->
                            removeFromFavourite(user.u_id!!, holder.favourite, holder.progress, position, user)
                        }
                        .setNeutralButton("No") { dialog, id ->
                            dialog.dismiss()
                        }

                builder.create()
                builder.show()
            }

        }
        
        
        holder.list_data.setOnClickListener {
            val intent = Intent(context,AuthorActivity::class.java)
            intent.putExtra("ID",user.u_id)
            intent.putExtra("Name",user.u_name)
            intent.putExtra("Age",user.u_age)
            intent.putExtra("Country",user.u_country)
            intent.putExtra("Gender",user.u_gender)
            intent.putExtra("Image",user.u_avatar)
            intent.putExtra("Language",user.u_languages)
            intent.putExtra("Genre",user.u_genres)
            intent.putExtra("Author",user.u_authors)
            intent.putExtra("Index",position)
            context.startActivity(intent)
        }

    }




     class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         var profile: ImageView
         var name: TextView
         var distance:TextView
         var age_label:TextView
         var age:TextView
         var gender_label:TextView
         var gender:TextView
         var location:TextView
         var see_more:TextView
         var language_label:TextView
         var languages:TextView
         var genre_label:TextView
         var genres:TextView
         var author_label:TextView
         var authors:TextView
         var books_label:TextView
         var books:TextView
         var more_data: RelativeLayout
         var favourite: ImageView
         var progress: ProgressBar
         var list_data: RelativeLayout


         init {
             name = view.findViewById(R.id.list_name)
             distance = view.findViewById(R.id.list_distance)
             age_label = view.findViewById(R.id.list_age_label)
             age = view.findViewById(R.id.list_age_txt)
             gender_label = view.findViewById(R.id.list_gender_label)
             gender = view.findViewById(R.id.list_gender_txt)
             location = view.findViewById(R.id.list_location_txt)
             see_more = view.findViewById(R.id.list_see_more)
             language_label = view.findViewById(R.id.list_language_label)
             languages = view.findViewById(R.id.list_language_txt)
             genre_label = view.findViewById(R.id.list_genre_label)
             genres = view.findViewById(R.id.list_genre_txt)
             author_label = view.findViewById(R.id.list_author_label)
             authors = view.findViewById(R.id.list_author_txt)
             books_label = view.findViewById(R.id.list_books_label)
             books = view.findViewById(R.id.list_books_txt)
             profile = view.findViewById(R.id.list_image)
             more_data = view.findViewById(R.id.list_more_data)
             favourite = view.findViewById(R.id.list_favourite_btn)
             progress = view.findViewById(R.id.search_progress)
             list_data = view.findViewById(R.id.search_list_layout)

        }
    }

    fun addToFavourite(id:Int, image:ImageView, progress: ProgressBar, index: Int, user: User) {

        val request = JSONObject()

        val sharedPref = context.getSharedPreferences("user", FragmentActivity.MODE_PRIVATE)
        val u_id = sharedPref.getString("user_id", "")

        request.put("u_id", u_id)
        request.put("author_id",id)

        progress.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(context)
        val url = "http://sprintsols.com/krizma/public/addFavouriteUser"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    progress.visibility = View.GONE

                    val code = response.getInt("code")

                    if (code == 101) {

                        image.setImageResource(R.drawable.search_favourite_active_btn)
                        Utils.users[index].u_favourite_flag = 1
                            Utils.favouriteUsers.add(user)

                        Toast.makeText(context,"Author has been added into your favourites.",Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(context, "something went wrong", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    progress.visibility = View.GONE
                    Toast.makeText(context,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }

        ) {
        }
        queue.add(postRequest)
    }

    fun removeFromFavourite(id:Int, image:ImageView, progress: ProgressBar, index:Int, user: User) {

        val request = JSONObject()

        val sharedPref = context.getSharedPreferences("user", FragmentActivity.MODE_PRIVATE)
        val u_id = sharedPref.getString("user_id", "")

        request.put("u_id", u_id)
        request.put("author_id",id)

        progress.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(context)
        val url = "http://sprintsols.com/krizma/public/removeFavouriteUser"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    progress.visibility = View.GONE

                    val code = response.getInt("code")

                    if (code == 101) {

                        image.setImageResource(R.drawable.search_favourite_inactive_btn)
                        Utils.users[index].u_favourite_flag = 0
                        Utils.favouriteUsers.remove(user)

                        Toast.makeText(context,"Author has been removed from your favourites.",Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(context, "something went wrong", Toast.LENGTH_LONG).show()
                    }

                },
                Response.ErrorListener {
                    progress.visibility = View.GONE
                    Toast.makeText(context,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }

        ) {

        }
        queue.add(postRequest)
    }


    fun filter(charText:String) {
       val charText = charText.toLowerCase(Locale.getDefault())
        Utils.users.clear()
        if (charText.isEmpty())
        {
            Utils.users.addAll(users)
        }
        else
        {
            for (user in users)
            {
                if (user.u_name!!.toLowerCase(Locale.getDefault()).contains(charText))
                {
                    Utils.users.add(user)
                }
            }
        }
        notifyDataSetChanged()
    }

}




