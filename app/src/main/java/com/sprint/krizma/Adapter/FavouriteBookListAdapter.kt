package com.sprint.krizma.Adapter

import android.app.AlertDialog
import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.bumptech.glide.Glide
import com.sprint.krizma.Adapter.DataModel.Book
import com.sprint.krizma.R
import com.sprint.krizma.Utils
import org.json.JSONObject

import java.util.ArrayList


class FavouriteBookListAdapter

(internal var context: Context, internal var books: ArrayList<Book>) : RecyclerView.Adapter<FavouriteBookListAdapter.BookViewHolder>() {

    var mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = mInflater.inflate(R.layout.list_books,parent,false)

        return FavouriteBookListAdapter.BookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {



        val book = books[position]

        holder.name.text = book.b_name
        holder.language.text = book.b_language
        holder.author.text = book.b_author

        if(book.b_image!="") {
            Glide.with(context).load(book.b_image).into(holder.image)
        }
        else{
            Glide.with(context).load(R.drawable.user_book_bg).into(holder.image)
        }

        if(book.favourite_flag==1)
        {
            holder.favourite.setImageResource(R.drawable.search_favourite_active_btn)
        }


        holder.favourite.setOnClickListener{
            val builder = AlertDialog.Builder(context, R.style.AlertDialogCustom)
            builder.setMessage("Are you sure you want to remove this book from favourites?")
                    .setPositiveButton("Yes", { dialog, id ->
                        removeFromFavourite(book.b_id, holder.favourite, holder.progress, position, book)
                    })
                    .setNeutralButton("No", { dialog, id ->
                        dialog.dismiss()
                    })

            builder.create()
            builder.show()
        }

    }











    class BookViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var image: ImageView
        var name: TextView
        var language: TextView
        var author: TextView
        var favourite: ImageView
        var progress: ProgressBar

        init {
            image = view.findViewById(R.id.list_book_image)
            name = view.findViewById(R.id.list_book_name)
            language = view.findViewById(R.id.list_book_language_txt)
            author = view.findViewById(R.id.list_book_author_txt)
            favourite = view.findViewById(R.id.list_book_favourite_btn)
            progress = view.findViewById(R.id.list_book_progress)
        }
    }


    fun removeFromFavourite(id:Int, image:ImageView, progress: ProgressBar, index:Int, book: Book) {

        val request = JSONObject()

        val sharedPref = context.getSharedPreferences("user", FragmentActivity.MODE_PRIVATE)
        val u_id = sharedPref.getString("user_id", "")

        request.put("u_id", u_id)
        request.put("b_id",id)

        progress.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(context)
        val url = "http://sprintsols.com/krizma/public/removeFavouriteBook"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    progress.visibility = View.GONE

                    val code = response.getInt("code")

                    if (code == 101) {

                        image.setImageResource(R.drawable.search_favourite_inactive_btn)

                        book.favourite_flag = 0

                        Utils.favouriteBooks.remove(book)

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
}
