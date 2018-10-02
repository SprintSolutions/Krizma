package com.sprint.krizma

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import com.applozic.mobicomkit.api.account.user.User
import kotlinx.android.synthetic.main.activity_settings.*
import kotlin.math.log
import android.os.AsyncTask.execute
import com.applozic.mobicomkit.api.account.user.UserLogoutTask



class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        fonts()

        setting_logout.setOnClickListener { logout() }

        setting_about.setOnClickListener {
            val intent = Intent(this,AboutUsActivity::class.java)
            startActivity(intent)
        }

        setting_back_btn.setOnClickListener { super.onBackPressed() }


        setting_notification_switch.setOnCheckedChangeListener { compoundButton, b ->
            if(b)
            {
                setting_notification_switch.thumbDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
                setting_notification_switch.setTrackResource(R.drawable.rounded_corner_switch)
            }

            else
            {
                setting_notification_switch.getThumbDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
                setting_notification_switch.setTrackResource(R.drawable.rounded_corner_switch_off)
            }

        }


        setting_feedback.setOnClickListener {
            val email = "mailto: developer@example.com"
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse(email)
            startActivity(Intent.createChooser(intent,"Send feedback"))
        }

        setting_privacy.setOnClickListener {
            val url = "https://www.google.com"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }

        setting_rate.setOnClickListener {
            val url = "market://details?id="+application.packageName
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }

    fun fonts(){
        val montserrat_regular = ResourcesCompat.getFont(this, R.font.montserrat_regular)

        setting_notification.typeface = montserrat_regular

        setting_invite.typeface = montserrat_regular

        setting_about.typeface = montserrat_regular

        setting_help.typeface = montserrat_regular

        setting_feedback.typeface = montserrat_regular

        setting_rate.typeface = montserrat_regular

        setting_privacy.typeface = montserrat_regular

        setting_logout.typeface = montserrat_regular
    }


    fun logout()
    {
        val builder = AlertDialog.Builder(this,R.style.AlertDialogCustom)
        builder.setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { dialog, id ->


                    val userLogoutTaskListener = object : UserLogoutTask.TaskListener {
                        override fun onSuccess(context: Context) {
                            val intent = Intent(context,LoginActivity::class.java)
                            Utils.user = com.sprint.krizma.Adapter.DataModel.User()
                            Utils.books = ArrayList()
                            Utils.favouriteUsers = ArrayList()
                            Utils.favouriteBooks = ArrayList()
                            Utils.users = ArrayList()
                            //Logout success
                            val sharedpreferences = getSharedPreferences("user", Context.MODE_PRIVATE)
                            val editor = sharedpreferences.edit()
                            editor.clear()
                            editor.commit()

                            startActivity(intent)
                        }

                        override fun onFailure(exception: Exception) {
                            //Logout failure
                        }
                    }
                    val userLogoutTask = UserLogoutTask(userLogoutTaskListener, this)
                    userLogoutTask.execute(null as Void?)


                }
                .setNeutralButton("No") { dialog, id ->
                    dialog.dismiss()
                }

        // Create the AlertDialog object and return it
        builder.create()

        builder.show()
    }
}
