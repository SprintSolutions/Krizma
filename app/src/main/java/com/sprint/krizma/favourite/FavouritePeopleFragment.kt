package com.sprint.krizma.favourite


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sprint.krizma.Adapter.FavouriteUserAdapter
import com.sprint.krizma.Adapter.SearchListAdapter

import com.sprint.krizma.R
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.favourite_fragment_person.*
import kotlinx.android.synthetic.main.favourite_fragment_person.view.*
import kotlinx.android.synthetic.main.main_fragment_search.view.*


class FavouritePeopleFragment : Fragment() {

    companion object {
        fun newInstance(): FavouritePeopleFragment {
            return FavouritePeopleFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.favourite_fragment_person, container, false)

        val viewManager = LinearLayoutManager(activity)

        if(Utils.favouriteUsers.isEmpty())
        {
            view.favourites_user_empty.visibility = View.VISIBLE
        }

        val itemDecorator = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(activity!!.getDrawable(R.drawable.divider))

        view.favourites_person_list.layoutManager = viewManager
        view.favourites_person_list.addItemDecoration(itemDecorator)

        view.favourites_person_list.adapter = FavouriteUserAdapter(activity!!,Utils.favouriteUsers)

        return view
    }

}
