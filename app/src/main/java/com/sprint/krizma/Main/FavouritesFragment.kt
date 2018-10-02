package com.sprint.krizma.Main


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.sprint.krizma.Adapter.FavouritePagerAdapter

import com.sprint.krizma.R
import kotlinx.android.synthetic.main.main_fragment_favourites.view.*


class FavouritesFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_fragment_favourites, container, false)

        view.setOnTouchListener { view, motionEvent ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        }



        view.favourite_tab.setupWithViewPager(view.favourite_pager)


        val pager = FavouritePagerAdapter(childFragmentManager,activity!!)

        view.favourite_pager.adapter = pager





        return view
    }

}
