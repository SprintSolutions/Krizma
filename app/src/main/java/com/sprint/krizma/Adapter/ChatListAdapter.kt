package com.sprint.krizma.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Message
import android.support.v4.app.FragmentActivity
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.bumptech.glide.Glide
import com.sprint.krizma.Adapter.DataModel.User
import com.sprint.krizma.R
import com.sprint.krizma.AuthorActivity
import com.sprint.krizma.Utils
import org.json.JSONObject

import java.util.ArrayList
import android.widget.TextView
import android.widget.RelativeLayout
import com.applozic.mobicomkit.Applozic
import com.applozic.mobicomkit.api.conversation.ApplozicConversation
import com.applozic.mobicomkit.channel.service.ChannelService
import com.applozic.mobicomkit.contact.AppContactService
import com.applozic.mobicommons.people.contact.Contact
import java.nio.channels.Channel


class ChatListAdapter



(internal var context: Context, internal var messages: ArrayList<com.applozic.mobicomkit.api.conversation.Message>) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    var mInflater:LayoutInflater

    internal var more = false

    init{
        mInflater = LayoutInflater.from(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = mInflater.inflate(R.layout.list_message,parent,false)

        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val contact: Contact
        val channel: Channel
        val message = com.applozic.mobicomkit.api.conversation.Message()
        if (message.groupId==null)
        {
            contact = AppContactService(context).getContactById(message.contactIds)
            holder.name.text = contact.displayName
//            unreadCountTextView.setText(contact.unreadCount)
            Glide.with(context).load(contact.imageURL).into(holder.profile)
            holder.message.text = message.message

        }
//        else
//        {
//            //this message is for a group. Get the Channel to display its fields
//            channel = ChannelService.getInstance(context).getChannelInfo(message.groupId)
//            displayNameTextView.setText(channel.getName())
//            unreadCountTextView.setText(channel.getUnreadCount())
//            loadImage(profileImageView, channel.getImageUrl())
//        }


//        val contact = contacts[position]
//        holder.name.text = contact.contactIds
//        holder.time.text = contact.timeToLive.toString()
//        holder.message.text = contact.message
//        Glide.with(context).load(contact.).into(holder.profile)

//        holder.list_data.setOnClickListener {
//            val intent = Intent(context,ChatActivity::class.java)
//            context.startActivity(intent)
//        }

    }

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var profile: ImageView
        var name: TextView
        var time: TextView
        var message: TextView
        var list_data: RelativeLayout


        init {
            name = view.findViewById(R.id.chat_name)
            time = view.findViewById(R.id.chat_time)
            message = view.findViewById(R.id.chat_message)
            profile = view.findViewById(R.id.chat_image)
            list_data = view.findViewById(R.id.chat_list_layout)

        }

    }

}




