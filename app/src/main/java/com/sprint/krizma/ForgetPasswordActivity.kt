package com.sprint.krizma

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_add_book.*
import kotlinx.android.synthetic.main.activity_forget_password.*
import org.json.JSONObject

class ForgetPasswordActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        forget_password_btn.setOnClickListener {
            if(forget_password_email_txt.text.isNotEmpty()) {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(forget_password_email_txt.text).matches())
                {
                    forgetPassword(forget_password_email_txt.text.toString())
                }
                else{
                    Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                }
            }

            else{
                Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun forgetPassword(email: String)
    {

        forget_password_progress.visibility = View.VISIBLE

        val request = JSONObject()
        request.put("email",email)

        val queue = Volley.newRequestQueue(this)
        val url = "http://sprintsols.com/krizma/public/forgotPassword"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                Response.Listener<JSONObject> { response ->

                    forget_password_progress.visibility = View.GONE

                    val code = response.getInt("code")

                    if (code == 101) {
                        Toast.makeText(this, "Email sent successfully", Toast.LENGTH_LONG).show()

                    } else {
                        Toast.makeText(this, "something went wrong", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener {
                    forget_password_progress.visibility = View.GONE
                    Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                }
        ) {
        }
        queue.add(postRequest)
    }
}
