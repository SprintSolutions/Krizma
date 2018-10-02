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
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.fragment_edit_description.view.*
import kotlinx.android.synthetic.main.profile_fragment_describe.view.*


/**
 * A simple [Fragment] subclass.
 */
class EditDescribeFragment : Fragment() {
    companion object {
        fun newInstance(): EditDescribeFragment {
            return EditDescribeFragment()
    }
}



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_edit_description, container, false)

        fonts(view)

        describeData(view)

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

        view.edit_profile_describe_label.typeface = montserrat_bold

        view.edit_profile_optional_txt.typeface = montserrat_light

        view.edit_profile_describe_txt.typeface = montserrat_regular

    }

    fun describeData(view: View)
    {
        view.edit_profile_describe_txt.setText(Utils.user.u_name)
    }


}
