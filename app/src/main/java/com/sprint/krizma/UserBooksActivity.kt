package com.sprint.krizma

import android.app.TaskStackBuilder
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sprint.krizma.Adapter.AuthorBookListAdapter
import com.sprint.krizma.Adapter.UserBookListAdapter
import com.sprint.krizma.Main.SearchFragment
import kotlinx.android.synthetic.main.activity_user_books.*

class UserBooksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_books)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        SearchFragment().refresh("Books",this,book_list,user_book_empty)
        UserBookListAdapter(this,Utils.books,user_book_empty).notifyDataSetChanged()


        val viewManager = LinearLayoutManager(this)

        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(getDrawable(R.drawable.divider))

        book_list.layoutManager = viewManager
        book_list.addItemDecoration(itemDecorator)
        book_list.adapter = UserBookListAdapter(this,Utils.books,user_book_empty)




        book_add_btn.setOnClickListener { view ->
            val intent = Intent(this,AddBookActivity::class.java)
            startActivity(intent)
        }

        user_book_back.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("profile","profile")
            startActivity(intent)
        }
    }

}
