package com.sprint.krizma.favourite


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sprint.krizma.Adapter.FavouriteBookListAdapter
import com.sprint.krizma.Adapter.UserBookListAdapter

import com.sprint.krizma.R
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.activity_author_books.*
import kotlinx.android.synthetic.main.favourite_fragment_book.view.*



class FavouriteBookFragment : Fragment() {

    companion object {
        fun newInstance(): FavouriteBookFragment {
            return FavouriteBookFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.favourite_fragment_book, container, false)

        if(Utils.favouriteBooks.isEmpty())
        {
            view.favourites_book_empty.visibility = View.VISIBLE
        }

        val viewManager = LinearLayoutManager(activity)

        val itemDecorator = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(activity!!.getDrawable(R.drawable.divider))

        view.favourites_book_list.layoutManager = viewManager
        view.favourites_book_list.addItemDecoration(itemDecorator)

        view.favourites_book_list.adapter = FavouriteBookListAdapter(activity!!,Utils.favouriteBooks)

        return view
    }



}
