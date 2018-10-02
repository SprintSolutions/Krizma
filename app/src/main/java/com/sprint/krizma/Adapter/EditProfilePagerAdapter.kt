package com.sprint.krizma.Adapter

/**
 * Created by Umar Muzaffar Guraya on 8/8/2018.
 */

import android.app.Activity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import android.widget.ImageButton

import com.sprint.krizma.Profile.EditDescribeFragment
import com.sprint.krizma.Profile.EditInfoFragment
import com.sprint.krizma.Profile.EditInterestFragment
import com.sprint.krizma.R


/**
 * Created by Umar Muzaffar Guraya on 7/11/2018.
 */

class EditProfilePagerAdapter(fragmentManager: FragmentManager, private val mContext: Activity) : FragmentPagerAdapter(fragmentManager) {

    init {

        mContext.findViewById<ImageButton>(R.id.edit_profile_back_btn).visibility = View.GONE
    }


    override fun getCount(): Int {
        return NUM_ITEMS
    }

    override fun getItem(position: Int): Fragment? {
        return when (position) {
            0 -> EditInfoFragment.newInstance()
            1 -> EditDescribeFragment.newInstance()
            2 -> EditInterestFragment.newInstance()
            else -> null
        }
    }

    companion object {
        private val NUM_ITEMS = 3
    }


}

