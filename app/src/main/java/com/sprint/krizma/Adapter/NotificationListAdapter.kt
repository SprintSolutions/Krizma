package com.sprint.krizma.Adapter

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.sprint.krizma.Adapter.DataModel.Notification

import com.sprint.krizma.R
import com.sprint.krizma.Utils

/**
 * Created by Umar Muzaffar Guraya on 7/19/2018.
 */

class NotificationListAdapter

(internal var context: Context, internal var notifications: ArrayList<Notification>, internal var no_data:TextView ) : RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder>() {

    var mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = mInflater.inflate(R.layout.list_books,parent,false)

        return NotificationListAdapter.NotificationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        val notification = notifications[position]

        if(notifications.isEmpty())
        {
            no_data.visibility = View.VISIBLE
        }
        else
        {
            no_data.visibility = View.GONE
        }

        holder.image.setImageResource(R.drawable.app_icon)
        holder.name.text = Utils.user.u_name
        holder.type.text = "send a message"
        holder.notification.text = notification.message
        holder.time.text = notification.date

//        if(book.b_image!="") {
//            Glide.with(context).load(book.b_image).into(holder.image)
//        }
//        else{
//            Glide.with(context).load(R.drawable.user_book_bg).into(holder.image)
//        }

//        holder.background.setOnClickListener {
//            val intent = Intent(context, AddBookActivity::class.java)
//            intent.putExtra("index",position)
//            intent.putExtra("id",Utils.books[position].b_id)
//            intent.putExtra("image",Utils.books[position].b_image)
//            intent.putExtra("name",Utils.books[position].b_name)
//            intent.putExtra("author",Utils.books[position].b_author)
//            intent.putExtra("language",Utils.books[position].b_language)
//            intent.putExtra("type",Utils.books[position].b_type)
//            context.startActivity(intent)
//        }


    }





    class NotificationViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var image: ImageView
        var name: TextView
        var type: TextView
        var notification: TextView
        var time: TextView


        init {
            image = view.findViewById(R.id.list_notification_image)
            name = view.findViewById(R.id.notification_user)
            type = view.findViewById(R.id.notification_type)
            notification = view.findViewById(R.id.notification)
            time = view.findViewById(R.id.notification_time)
        }
    }


}