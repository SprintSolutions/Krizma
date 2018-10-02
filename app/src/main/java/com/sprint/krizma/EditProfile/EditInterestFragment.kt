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
import kotlinx.android.synthetic.main.profile_fragment_interest.view.*
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import com.hootsuite.nachos.NachoTextView
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.sprint.krizma.Adapter.DataModel.Data
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.fragment_edit_info.view.*
import kotlinx.android.synthetic.main.fragment_edit_interest.view.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 */
class EditInterestFragment : Fragment() {

    companion object {
        fun newInstance(): EditInterestFragment {
            return EditInterestFragment()
        }
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//
        val view = inflater.inflate(R.layout.fragment_edit_interest, container, false)

        interestData(view)

        fonts(view)

        val languages = Data.languages

        val genres = Data.genres

        val languageAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, languages)

        val genreAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, genres)


        view.edit_profile_language_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
        view.edit_profile_genre_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
        view.edit_profile_author_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)

        view.edit_profile_language_txt.setAdapter(languageAdapter)
        view.edit_profile_genre_txt.setAdapter(genreAdapter)




        view.edit_profile_type_new.setOnClickListener { view1 ->
            view.edit_profile_type_new.setImageResource(R.drawable.profile_new_active)
            view.edit_profile_type_used.setImageResource(R.drawable.profile_used_deactive)
            view.edit_profile_type_both.setImageResource(R.drawable.profile_both_deactive)
            Utils.edit_bookType = "New"
        }

        view.edit_profile_type_used.setOnClickListener { view1 ->
            view.edit_profile_type_new.setImageResource(R.drawable.profile_new_deactive)
            view.edit_profile_type_used.setImageResource(R.drawable.profile_used_active)
            view.edit_profile_type_both.setImageResource(R.drawable.profile_both_deactive)
            Utils.edit_bookType = "Used"
        }

        view.edit_profile_type_both.setOnClickListener { view1 ->
            view.edit_profile_type_new.setImageResource(R.drawable.profile_new_deactive)
            view.edit_profile_type_used.setImageResource(R.drawable.profile_used_deactive)
            view.edit_profile_type_both.setImageResource(R.drawable.profile_both_active)
            Utils.edit_bookType = "Both"
        }




        view.setOnTouchListener { view, motionEvent ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(activity!!.currentFocus!!.windowToken, 0)
        }

        return view
    }

    fun fonts(view: View)
    {
        val montserrat_bold = ResourcesCompat.getFont(activity!!, R.font.montserrat_bold)

        val montserrat_regular = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

        view.edit_profile_interest_label.typeface = montserrat_bold

        view.edit_profile_language_label.typeface = montserrat_regular

//        view.edit_profile_language_txt.typeface = montserrat_regular

        view.edit_profile_genre_label.typeface = montserrat_regular

//        view.edit_profile_genre_txt.typeface = montserrat_regular

        view.edit_profile_author_label.typeface = montserrat_regular

//        view.edit_profile_author_txt.typeface = montserrat_regular

        view.edit_profile_book_label.typeface = montserrat_regular

    }


    fun interestData(view: View)
    {
        if(Utils.user.u_languages!!.isNotEmpty()) {
            val user_languages: List<String> = Utils.user.u_languages!!.trim().split(",")
            view.edit_profile_language_txt.setText(user_languages)
        }

        if(Utils.user.u_genres!!.isNotEmpty()) {
            val user_genres: List<String> = Utils.user.u_genres!!.trim().split(",")
            view.edit_profile_genre_txt.setText(user_genres)
        }

        if(Utils.user.u_authors!!.isNotEmpty()) {
            val user_authors: List<String> = Utils.user.u_authors!!.trim().split(",")
            view.edit_profile_author_txt.setText(user_authors)
        }


        if(Utils.user.u_type=="New")
        {
            view.edit_profile_type_new.setImageResource(R.drawable.profile_new_active)
            view.edit_profile_type_used.setImageResource(R.drawable.profile_used_deactive)
            view.edit_profile_type_both.setImageResource(R.drawable.profile_both_deactive)
            Utils.edit_bookType = "New"
        }
        else if(Utils.user.u_type=="Used")
        {
            view.edit_profile_type_new.setImageResource(R.drawable.profile_new_deactive)
            view.edit_profile_type_used.setImageResource(R.drawable.profile_used_active)
            view.edit_profile_type_both.setImageResource(R.drawable.profile_both_deactive)
            Utils.edit_bookType = "Used"
        }
        else if(Utils.user.u_type=="Both")
        {
            view.edit_profile_type_new.setImageResource(R.drawable.profile_new_deactive)
            view.edit_profile_type_used.setImageResource(R.drawable.profile_used_deactive)
            view.edit_profile_type_both.setImageResource(R.drawable.profile_both_active)
            Utils.edit_bookType = "Both"
        }

    }

}
