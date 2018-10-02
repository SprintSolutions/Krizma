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
import kotlinx.android.synthetic.main.profile_fragment_interest.*


/**
 * A simple [Fragment] subclass.
 */
class InterestFragment : Fragment() {

    var book = 1

    var bookType = "Both"

    companion object {
        fun newInstance(): InterestFragment {
            return InterestFragment()
        }
    }


    val languages = Data.languages

    val genres = Data.genres

//    val authors = arrayOf("J.B Turner","Dr.Seuss","Scott Pratt","Stephen King","J.K. Rowling")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//
        val view = inflater.inflate(R.layout.profile_fragment_interest, container, false)

        fonts(view)

        view.profile_language_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_CURRENT_TOKEN)
        view.profile_genre_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)
        view.profile_author_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)

//        profile_language_txt.setText()

        view.profile_type_new.setOnClickListener { view1 ->
            view.profile_type_new.setImageResource(R.drawable.profile_new_active)
            view.profile_type_used.setImageResource(R.drawable.profile_used_deactive)
            view.profile_type_both.setImageResource(R.drawable.profile_both_deactive)
                    book = 2
                    Utils.bookType = "New"
                }

        view.profile_type_used.setOnClickListener { view1 ->
            view.profile_type_new.setImageResource(R.drawable.profile_new_deactive)
            view.profile_type_used.setImageResource(R.drawable.profile_used_active)
            view.profile_type_both.setImageResource(R.drawable.profile_both_deactive)
            book = 2
            bookType = "Used"
        }

        view.profile_type_both.setOnClickListener { view1 ->
            view.profile_type_new.setImageResource(R.drawable.profile_new_deactive)
            view.profile_type_used.setImageResource(R.drawable.profile_used_deactive)
            view.profile_type_both.setImageResource(R.drawable.profile_both_active)
            book = 2
            bookType = "Both"
        }

        val languageAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, languages)

        val genreAdapter = ArrayAdapter<String>(activity,
                android.R.layout.simple_dropdown_item_1line, genres)
//
//        val authorAdapter = ArrayAdapter<String>(activity,
//                android.R.layout.simple_dropdown_item_1line, authors)
//
//
//
//        view.profile_author_txt.adapter = languageAdapter
        view.profile_language_txt.setAdapter(languageAdapter)
//        view.profile_language_txt.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        view.profile_genre_txt.setAdapter(genreAdapter)
//        view.profile_genre_txt.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
//
//        view.profile_author_txt.setAdapter(authorAdapter)
//        view.profile_author_txt.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())


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

        view.profile_interest_label.typeface = montserrat_bold

        view.profile_language_label.typeface = montserrat_regular

//        view.profile_language_txt.typeface = montserrat_regular

        view.profile_genre_label.typeface = montserrat_regular

//        view.profile_genre_txt.typeface = montserrat_regular

        view.profile_author_label.typeface = montserrat_regular

//        view.profile_author_txt.typeface = montserrat_regular

        view.profile_book_label.typeface = montserrat_regular

    }




}
