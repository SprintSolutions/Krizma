package com.sprint.krizma

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.sprint.krizma.Adapter.AuthorBookListAdapter
import kotlinx.android.synthetic.main.activity_author_books.*

class AuthorBooksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author_books)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)


        val index = intent.getIntExtra("index",0)

        if(Utils.users[index].u_books.isEmpty())
        {
            author_book_empty.visibility = View.VISIBLE
        }

        author_book_back.setOnClickListener { super.onBackPressed() }

        val viewManager = LinearLayoutManager(this)

        val itemDecorator = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(getDrawable(R.drawable.divider))

        author_book_list.layoutManager = viewManager
        author_book_list.addItemDecoration(itemDecorator)
        author_book_list.adapter = AuthorBookListAdapter(this,Utils.users[index].u_books)
    }
}
