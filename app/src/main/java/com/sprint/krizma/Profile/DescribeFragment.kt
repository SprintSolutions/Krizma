package com.sprint.krizma.Profile


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.sprint.krizma.R
import kotlinx.android.synthetic.main.profile_fragment_describe.view.*


/**
 * A simple [Fragment] subclass.
 */
class DescribeFragment : Fragment() {
    companion object {
        fun newInstance(): DescribeFragment {
            return DescribeFragment()
    }
}



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.profile_fragment_describe, container, false)

        fonts(view)

        view.setOnTouchListener { view, motionEvent ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        }

        return view
    }

    fun fonts(view: View)
    {
        val montserrat_bold = ResourcesCompat.getFont(activity!!, R.font.montserrat_bold)

        val montserrat_light = ResourcesCompat.getFont(activity!!, R.font.montserrat_light)

        val montserrat_regular = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

        view.profile_describe_label.typeface = montserrat_bold

        view.profile_optional_txt.typeface = montserrat_light

        view.profile_describe_txt.typeface = montserrat_regular

    }



}
