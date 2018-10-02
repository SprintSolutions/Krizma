package com.sprint.krizma.Profile


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sprint.krizma.R
import kotlinx.android.synthetic.main.profile_fragment_verify.view.*
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v4.content.res.ResourcesCompat
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.profile_fragment_verify.*
import org.json.JSONObject


/**
 * A simple [Fragment] subclass.
 */
class VerifyFragment : Fragment() {


    companion object {
        fun newInstance(): VerifyFragment {
            return VerifyFragment()
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.profile_fragment_verify, container, false)

        fonts(view)

        view.verify_ch1.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                view.verify_ch2.requestFocus()

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                // TODO Auto-generated method stub

            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

        })

        view.verify_ch2.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                view.verify_ch3.requestFocus()

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                // TODO Auto-generated method stub

            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

        })

        view.verify_ch3.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                view.verify_ch4.requestFocus()

            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                // TODO Auto-generated method stub

            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

        })

        view.verify_ch4.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                val imm = getActivity()!!.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(getActivity()!!.currentFocus!!.windowToken, 0)


            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
                // TODO Auto-generated method stub

            }

            override fun afterTextChanged(s: Editable) {
                // TODO Auto-generated method stub
            }

        })

        view.verify_resend_txt.setOnClickListener { view ->
            resendCode()
        }

        view.setOnTouchListener { view, motionEvent ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        }

        return view
    }

    fun fonts(view: View)
    {
        val montserrat_regular = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

        val opensans_regular = ResourcesCompat.getFont(activity!!, R.font.opensans_regular)

        view.verify_text.typeface = montserrat_regular

        view.verify_expire_txt.typeface = opensans_regular

        view.verify_timer.typeface = opensans_regular

        view.verify_no_code_txt.typeface = opensans_regular

        view.verify_resend_txt.typeface = opensans_regular

    }


    fun resendCode()
    {
        val request = JSONObject()

        val sharedPref = activity!!.getSharedPreferences("user",Context.MODE_PRIVATE)
        val u_email = Utils.user.u_email

//        val u_email = "hassaan@sprintsols.com"

        request.put("email",u_email)


//        Toast.makeText(activity!!,request.toString(), Toast.LENGTH_LONG).show()

        profile_verify_progress.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity!!)
        val url = "http://sprintsols.com/krizma/public/resendCode"
        val postRequest = object : JsonObjectRequest(Request.Method.POST, url,request,
                Response.Listener<JSONObject> { response ->

                    profile_verify_progress.visibility = View.INVISIBLE
//                    Toast.makeText(activity!!,response.toString(), Toast.LENGTH_LONG).show()

                    val code = response.getInt("code")

                    if(code==101)
                    {
                        Toast.makeText(activity!!,"You will receive an email soon", Toast.LENGTH_LONG).show()
                    }
                    else
                    {
                        Toast.makeText(activity!!,"Something went wrong", Toast.LENGTH_LONG).show()
                    }


                },
                Response.ErrorListener {
                    profile_verify_progress.visibility = View.INVISIBLE
                    Toast.makeText(activity!!,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()

                }
        ) {


        }
        queue.add(postRequest)
    }

}
