package com.sprint.krizma.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.sprint.krizma.AddBookActivity
import com.sprint.krizma.R
import com.sprint.krizma.Utils
import org.json.JSONObject

import java.util.ArrayList


class UserBookListAdapter

(internal var context: Context, internal var books: ArrayList<Book>, internal var no_data:TextView ) : RecyclerView.Adapter<UserBookListAdapter.BookViewHolder>() {

    var mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = mInflater.inflate(R.layout.list_books,parent,false)

        return UserBookListAdapter.BookViewHolder(view)
    }

    override fun getItemCount(): Int {
        return books.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {

        val book = books[position]

        if(books.isEmpty())
        {
            no_data.visibility = View.VISIBLE
        }
        else
        {
            no_data.visibility = View.GONE
        }

        holder.name.text = book.b_name
        holder.language.text = book.b_language
        holder.author.text = book.b_author

        if(book.b_image!="") {
            Glide.with(context).load(book.b_image).into(holder.image)
        }
        else{
            Glide.with(context).load(R.drawable.user_book_bg).into(holder.image)
        }

        holder.background.setOnClickListener {
            val intent = Intent(context, AddBookActivity::class.java)
            intent.putExtra("index",position)
            intent.putExtra("id",Utils.books[position].b_id)
            intent.putExtra("image",Utils.books[position].b_image)
            intent.putExtra("name",Utils.books[position].b_name)
            intent.putExtra("author",Utils.books[position].b_author)
            intent.putExtra("language",Utils.books[position].b_language)
            intent.putExtra("type",Utils.books[position].b_type)
            context.startActivity(intent)
        }


    }





    class BookViewHolder(view: View):RecyclerView.ViewHolder(view) {
        var image: ImageView
        var name: TextView
        var language: TextView
        var author: TextView
        var favourite: ImageView
        var progress: ProgressBar
        var background: RelativeLayout

        init {
            image = view.findViewById(R.id.list_book_image)
            name = view.findViewById(R.id.list_book_name)
            language = view.findViewById(R.id.list_book_language_txt)
            author = view.findViewById(R.id.list_book_author_txt)
            favourite = view.findViewById(R.id.list_book_favourite_btn)
            progress = view.findViewById(R.id.list_book_progress)
            background = view.findViewById(R.id.list_book_background)
        }
    }


}
