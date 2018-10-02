package com.sprint.krizma.FireBase

import android.util.Log
import com.applozic.mobicomkit.Applozic
import com.applozic.mobicomkit.api.account.register.RegisterUserClientService
import com.applozic.mobicomkit.api.account.register.RegistrationResponse
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
/**
 * Created by sunil on 9/4/16.
 */
class FcmInstanceIDListenerService:FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val registrationId = FirebaseInstanceId.getInstance().getToken()
        Log.i(TAG, "Found Registration Id:$registrationId")
        Applozic.getInstance(this).deviceRegistrationId = registrationId
        if (MobiComUserPreference.getInstance(this).isRegistered)
        {
            try
            {
                val registrationResponse = RegisterUserClientService(this).updatePushNotificationId(registrationId)
            }
            catch (e:Exception) {
                e.printStackTrace()
            }
        }
    }
    companion object {
        private val TAG = "FcmInstanceIDListener"
    }
}