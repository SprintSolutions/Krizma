package com.sprint.krizma.Main


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.getDrawable
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.sprint.krizma.Adapter.NotificationListAdapter

import com.sprint.krizma.R
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.main_fragment_notification.view.*


class NotificationFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_fragment_notification, container, false)

        view.setOnTouchListener { view, motionEvent ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        }

        val viewManager = LinearLayoutManager(context)

        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(context!!.getDrawable(R.drawable.divider))

        view.notification_list.layoutManager = viewManager
        view.notification_list.addItemDecoration(itemDecorator)
        view.notification_list.adapter = NotificationListAdapter(context!!, Utils.notifications,view.notification_empty)


        return view
    }

}
