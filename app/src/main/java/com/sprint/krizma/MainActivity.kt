package com.sprint.krizma

import android.app.Fragment
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.ConversationFragment
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComConversationFragment
import com.applozic.mobicommons.people.channel.Conversation
import com.sprint.krizma.Main.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fonts()

        val extras = intent.extras

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        if(extras!= null && extras.containsKey("setting"))
        {
            user()
        }
        else if(extras!= null && extras.containsKey("profile"))
        {
            user()
        }
        else
        {
            search()
        }


        main_profile_btn.setOnClickListener { view ->
            user()
        }

        main_search_btn.setOnClickListener { view ->
            search()
        }

        main_message_btn.setOnClickListener { view ->
            messages()
        }

        main_notification_btn.setOnClickListener { view ->
            notification()
        }

        main_favourite_btn.setOnClickListener { view ->
            favourites()
        }

    }

    override fun onBackPressed() {

    }

    fun search()
    {
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)
        unselectAll()
        main_toolbar.visibility = View.VISIBLE
        main_toolbar_text.text = "BROWSE"
        main_search_btn.setImageResource(R.drawable.main_search_active)
        val fragment = SearchFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun user()
    {
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)
        unselectAll()
        main_profile_btn.setImageResource(R.drawable.main_profile_active)
        main_toolbar.visibility = View.GONE
        val fragment = UserFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun unselectAll()
    {
        main_search_btn.setImageResource(R.drawable.main_search_inactive)
        main_favourite_btn.setImageResource(R.drawable.main_favourite_inactive)
        main_message_btn.setImageResource(R.drawable.main_message_inactive)
        main_profile_btn.setImageResource(R.drawable.main_profile_inactive)
        main_notification_btn.setImageResource(R.drawable.main_notification_inactive)
    }

    fun notification()
    {
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)
        unselectAll()
        main_toolbar.visibility = View.VISIBLE
        main_toolbar_text.text = "NOTIFICATIONS"
        main_notification_btn.setImageResource(R.drawable.main_notification_active)
        val fragment = NotificationFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun favourites()
    {
        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)
        unselectAll()
        main_toolbar_text.text = "FAVOURITES"
        main_toolbar.visibility = View.VISIBLE
        main_favourite_btn.setImageResource(R.drawable.main_favourite_active)
        val fragment = FavouritesFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_fragment, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun messages()
    {
//        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)
//        unselectAll()
//        main_toolbar_text.text = "MESSAGES"
//        main_toolbar.visibility = View.VISIBLE
//        main_message_btn.setImageResource(R.drawable.main_message_active)
//        val fragment = MessageFragment()
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.main_fragment, fragment)
//        fragmentTransaction.addToBackStack(null)
//        fragmentTransaction.commit()

        val intent = Intent(this,ConversationActivity::class.java)
        startActivity(intent)
    }


    fun fonts()
    {
        val montserrat_bold = ResourcesCompat.getFont(this, R.font.montserrat_bold)
        main_toolbar_text.typeface = montserrat_bold
    }
}
