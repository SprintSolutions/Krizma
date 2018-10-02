package com.sprint.krizma.Main


import android.content.Context
import android.os.Bundle
import android.provider.Telephony
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.applozic.mobicomkit.api.conversation.*
import com.applozic.mobicomkit.api.conversation.database.ConversationDatabaseService
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationListView
import com.sprint.krizma.Adapter.ChatListAdapter
import com.applozic.mobicomkit.exception.ApplozicException
//import com.applozic.mobicomkit.listners.ConversationListHandler
import com.applozic.mobicomkit.listners.MessageListHandler
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity
import com.applozic.mobicomkit.uiwidgets.conversation.fragment.MobiComConversationFragment
import com.applozic.mobicommons.people.channel.Conversation
import com.sprint.krizma.R
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.main_fragment_message.view.*


/**
 * A simple [Fragment] subclass.
 */
class MessageFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view =  inflater.inflate(R.layout.main_fragment_message, container, false)

        val viewManager = LinearLayoutManager(activity)

        val itemDecorator = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(activity!!.getDrawable(R.drawable.divider))


        view.chat_list.layoutManager = viewManager
        view.chat_list.addItemDecoration(itemDecorator)

        ApplozicConversation.getLatestMessageList(context, false) { messageList, e ->
            if (e == null) {
                Utils.messages.addAll(messageList)
                view.chat_list.adapter = ChatListAdapter(activity!!,Utils.messages)
                ChatListAdapter(activity!!,Utils.messages).notifyDataSetChanged()

            } else {
                //Error in fetching messages.
                e.printStackTrace()
            }
        }



        return view
    }

}
