package com.sprint.krizma

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_about_us.*
import java.util.*

class AboutUsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        about_us_version.text = "Version "+packageManager.getPackageInfo(packageName,0).versionName

        about_us_year.text = "\u00A9"+Calendar.getInstance().get(Calendar.YEAR)+" KRIZMA"

        about_us_back_btn.setOnClickListener { super.onBackPressed() }

        about_us_logo.setOnClickListener { val url = "http://www.sprintsols.com"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent) }


    }
}
