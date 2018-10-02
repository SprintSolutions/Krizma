package com.sprint.krizma.FireBase

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.applozic.mobicomkit.api.account.register.RegistrationResponse
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference
import com.applozic.mobicomkit.api.account.user.PushNotificationTask
import com.applozic.mobicomkit.api.notification.MobiComPushReceiver
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sprint.krizma.MainActivity
import com.sprint.krizma.R
import org.json.JSONObject

class FcmListenerService:FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage:RemoteMessage) {
        Log.i(TAG, "Message data:" + remoteMessage.data)
        if (remoteMessage.data.isNotEmpty())
        {
            if (MobiComPushReceiver.isMobiComPushNotification(remoteMessage.data))
            {
                Log.i(TAG, "Applozic notification processing...")
                MobiComPushReceiver.processMessageAsync(this, remoteMessage.data)
                return
            }




//            val intent = Intent(this, MainActivity::class.java)
//            intent.putExtra("profile","profile")
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            val pendingIntent = PendingIntent.getActivity(this, 0 , intent, PendingIntent.FLAG_ONE_SHOT)
//
//            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//            val notificationBuilder = NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.app_icon)
//                    .setContentTitle(remoteMessage.data["title"])
//                    .setContentText(remoteMessage.data["body"])
//                    .setAutoCancel(true)
//                    .setSound(defaultSoundUri)
//                    .setContentIntent(pendingIntent)
//
//
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }
    }

    override fun onNewToken(token: String?) {
//        Log.i(TAG, "Token:$p0")
        sendRegistrationToServer(token)
    }

    companion object {
        private val TAG = "ApplozicGcmListener"
    }

    private fun sendRegistrationToServer(token: String?) {
        val request = JSONObject()

        request.put("type", "A")
        request.put("token", token)


        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/getToken"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    //                    val code = response.getInt("code")
//
//                    if (code == 101) {
//
//
//
//                    } else {
//
//                    }

                },
                Response.ErrorListener {

                }

        ) {
        }
        queue.add(postRequest)
    }
}