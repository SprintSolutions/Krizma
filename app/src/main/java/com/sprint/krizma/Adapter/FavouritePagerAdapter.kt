package com.sprint.krizma.Adapter

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.sprint.krizma.favourite.FavouriteBookFragment
import com.sprint.krizma.favourite.FavouritePeopleFragment

/**
 * Created by Umar Muzaffar Guraya on 8/1/2018.
 */

class FavouritePagerAdapter(fragmentManager: FragmentManager, private val mContext: Activity) : FragmentPagerAdapter(fragmentManager) {

    private val tabTitles = arrayOf("Users", "Books")

    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> FavouritePeopleFragment.newInstance()
            1 -> FavouriteBookFragment.newInstance()
            else -> null
        }
    }

    override fun getCount(): Int {
        return NUM_ITEMS
    }

    companion object {
        private val NUM_ITEMS = 2
    }
}
