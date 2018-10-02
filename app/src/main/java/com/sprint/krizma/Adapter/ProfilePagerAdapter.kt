package com.sprint.krizma.Adapter

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View

import com.sprint.krizma.Profile.BooksFragment
import com.sprint.krizma.Profile.DescribeFragment
import com.sprint.krizma.Profile.InfoFragment
import com.sprint.krizma.Profile.InterestFragment
import com.sprint.krizma.Profile.VerifyFragment
import com.sprint.krizma.R

/**
 * Created by Umar Muzaffar Guraya on 7/11/2018.
 */

class ProfilePagerAdapter(fragmentManager: FragmentManager, private val mContext: Activity) : FragmentPagerAdapter(fragmentManager) {

    init {

        mContext.findViewById<View>(R.id.profile_back_btn).visibility = View.GONE
    }


    override fun getCount(): Int {
        return NUM_ITEMS
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> VerifyFragment.newInstance()
            1 -> InfoFragment.newInstance()
            2 -> DescribeFragment.newInstance()
            3 -> InterestFragment.newInstance()
            4 -> BooksFragment.newInstance()
            else -> null
        }
    }

    companion object {
        private val NUM_ITEMS = 5
    }


}
