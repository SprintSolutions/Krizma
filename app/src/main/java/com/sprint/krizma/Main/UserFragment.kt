package com.sprint.krizma.Main


import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.antonionicolaspina.textimageview.TextImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.main_fragment_profile.view.*
import com.sprint.krizma.*
import com.sprint.krizma.CustomComponents.CustomImageView
import kotlinx.android.synthetic.main.fragment_edit_info.view.*
import java.util.*


class UserFragment : Fragment() {

    var random = 0

    val colors = arrayOf(R.drawable.rounded_corner_text,R.drawable.rounded_corner_text_2,R.drawable.rounded_corner_text_3)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.main_fragment_profile, container, false)


        view.user_profile_edit.setOnClickListener { view ->
            val intent = Intent(activity,EditProfileActivity::class.java)
            startActivity(intent)
        }

        fonts(view)

        view.user_profile_books_label.setOnClickListener {
            val intent = Intent(activity,UserBooksActivity::class.java)
            startActivity(intent)
        }

        userBooks(view)


        userAuthors(view)


        view.user_profile_language.setOnClickListener { view1 ->
                userLanguages(view)
        }

        view.user_profile_genre.setOnClickListener { view1 ->
                userGenres(view)
        }

        view.user_profile_author.setOnClickListener { view1 ->
                userAuthors(view)
        }


        view.user_profile_books_last.setOnClickListener {
            val intent = Intent(context,UserBooksActivity::class.java)
            startActivity(intent)
        }

        if(Utils.user.u_avatar!!.trim().isNotEmpty())
        {
            Glide.with(this).load(Utils.user.u_avatar).apply(RequestOptions().placeholder(R.drawable.user_dummy_pic)).into(view.user_profile_pic_btn)
        }

        if(Utils.user.u_name!!.isNotEmpty())
        {
            view.user_profile_name_txt.visibility = View.VISIBLE
            view.user_profile_name_txt.text = Utils.user.u_name
        }

        if(Utils.user.u_age!!.isNotEmpty())
        {
            view.user_profile_age_txt.visibility = View.VISIBLE
            view.user_profile_age_txt.text = Utils.user.u_age
        }

        if(Utils.user.u_gender!!.isNotEmpty())
        {
            view.user_profile_gender_txt.visibility = View.VISIBLE
            view.user_profile_gender_txt.text = Utils.user.u_gender
        }

        if(Utils.user.u_country!!.isNotEmpty())
        {
            view.user_profile_country_txt.visibility = View.VISIBLE
            view.user_profile_country_txt.text = Utils.user.u_country
        }


        view.user_profile_setting.setOnClickListener { view ->
            val intent = Intent(activity,SettingsActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    fun fonts(view: View)
    {
        val montserrat_regular = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

        val montserrat_medium = ResourcesCompat.getFont(activity!!, R.font.montserrat_medium)

        view.user_profile_name_txt.typeface = montserrat_medium

        view.user_profile_age_label.typeface = montserrat_regular

        view.user_profile_age_txt.typeface = montserrat_regular

        view.user_profile_country_txt.typeface = montserrat_regular

        view.user_profile_books_label.typeface = montserrat_regular

    }

    fun userLanguages(view: View)
    {
        removeAll(view)

        view.user_profile_language.setImageResource(R.drawable.user_language_active)

        if(Utils.user.u_languages!!.isNotEmpty()) {

            val languages: List<String> = Utils.user.u_languages!!.split(",")

            for (i in 0 until languages.size) {
                random = Random().nextInt(3)

                val textview = TextView(activity)

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)

                params.setMargins(20, 40, 10, 10)

                textview.setPadding(30, 20, 30, 20)

                textview.setTextColor(Color.WHITE)

                textview.textSize = 14F

                textview.layoutParams = params

                textview.text = languages[i]

                textview.setBackgroundResource(colors[random])

                textview.typeface = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

                view.user_profile_interests.addView(textview)

            }
        }

    }

    fun userAuthors(view: View)
    {
        removeAll(view)

        view.user_profile_author.setImageResource(R.drawable.user_author_active)

        if(Utils.user.u_authors!!.isNotEmpty()) {

            val authors: List<String> = Utils.user.u_authors!!.split(",")

            for (i in 0 until authors.size) {
                random = Random().nextInt(3)

                val textview = TextView(activity)

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)

                params.setMargins(20, 40, 10, 10)

                textview.setPadding(30, 20, 30, 20)

                textview.setTextColor(Color.WHITE)

                textview.textSize = 14F

                textview.layoutParams = params

                textview.text = authors[i]

                textview.setBackgroundResource(colors[random])

                textview.typeface = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

                view.user_profile_interests.addView(textview)
            }
        }

    }

    fun userGenres(view: View)
    {
        removeAll(view)

        view.user_profile_genre.setImageResource(R.drawable.user_genre_active)

        if(Utils.user.u_genres!!.isNotEmpty()) {

            val genres: List<String> = Utils.user.u_genres!!.split(",")

            for (i in 0 until genres.size) {
                random = Random().nextInt(3)

                val textview = TextView(activity)

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)

                params.setMargins(20, 40, 10, 10)

                textview.setPadding(30, 20, 30, 20)

                textview.setTextColor(Color.WHITE)

                textview.textSize = 14F

                textview.layoutParams = params

                textview.text = genres[i]

                textview.setBackgroundResource(colors[random])

                textview.typeface = ResourcesCompat.getFont(activity!!, R.font.montserrat_regular)

                view.user_profile_interests.addView(textview)

            }
        }
    }

    fun userBooks(view: View)
    {
        val books = Utils.books

        if(books.isNotEmpty())
        {
            if(books.size<4)
            {
                for (i in 0 until books.size)
                {
                    val imageview = RoundedImageView(activity)

                    imageview.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageview.cornerRadius = 15F
                    imageview.borderWidth = 2F
                    imageview.borderColor = Color.WHITE
                    imageview.isOval = false

                    val params = LinearLayout.LayoutParams(dpToPx(72), dpToPx(72))

                    params.setMargins(20, 40, 10, 10)

                    imageview.setPadding(20, 20, 10, 20)

                    imageview.setBackgroundResource(R.drawable.rounded_corner_image)

                    if(books[i].b_image.isNotEmpty()) {
                        Glide.with(this).asBitmap().load(Utils.books[i].b_image).into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val resized = Bitmap.createScaledBitmap(
                                        resource, dpToPx(72), dpToPx(72), false)
                                imageview.setImageBitmap(resized)
                            }
                        })
                    }

                    else
                    {
                        Glide.with(this).asBitmap().load(R.drawable.user_book_bg).into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val resized = Bitmap.createScaledBitmap(
                                        resource, dpToPx(72), dpToPx(72), false)
                                imageview.setImageBitmap(resized)
                            }
                        })
                    }

                    view.user_profile_books.addView(imageview)

                    imageview.setOnClickListener {
                        val intent = Intent(activity,UserBooksActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

            else
            {
                for (i in 0..3)
                {
                    val imageview = RoundedImageView(activity)

                    imageview.scaleType = ImageView.ScaleType.CENTER_CROP
                    imageview.cornerRadius = 15F
                    imageview.borderWidth = 2F
                    imageview.borderColor = Color.WHITE
                    imageview.isOval = false

                    val params = LinearLayout.LayoutParams(dpToPx(72), dpToPx(72))

                    params.setMargins(20, 40, 10, 10)

                    imageview.setPadding(10, 20, 10, 20)

//                    imageview.setBackgroundResource(R.drawable.rounded_corner_image)


                    if(i==3)
                    {
                        val bookcount = books.size-3
                        view.user_profile_books_last.visibility = View.VISIBLE
                        view.user_profile_book_count.text = "+"+bookcount.toString()
                        view.user_profile_book_count.visibility = View.VISIBLE
//
//                        val image = BitmapFactory.decodeResource(resources, R.drawable.user_book_number_bg)
//                        imageview.setImageBitmap(image)
//
//                        val books = TextView(activity)
//                        books.setPadding(0, 20, 50, 20)
//                        books.text = "+"+bookcount.toString()
//                        books.textSize = 13F
//                        books.typeface = Typeface.DEFAULT_BOLD
//                        view.user_profile_books.addView(imageview)
//                        view.user_profile_books.addView(books)
                        break
                    }


                if(books[i].b_image.isNotEmpty()){

                    Glide.with(this).asBitmap().load(books[i].b_image).into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val resized = Bitmap.createScaledBitmap(
                                    resource, dpToPx(72), dpToPx(72), false)
                            imageview.setImageBitmap(resized)
                        }
                    })
                }

                else {
                    Glide.with(this).asBitmap().load(R.drawable.user_book_bg).into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val resized = Bitmap.createScaledBitmap(
                                    resource, 219, 219, false)
                            imageview.setImageBitmap(resized)
                        }
                    })
                }
                    view.user_profile_books.addView(imageview)

                    imageview.setOnClickListener {
                        val intent = Intent(activity,UserBooksActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }

        else
        {
            val imageview = RoundedImageView(activity)

            val params = LinearLayout.LayoutParams(dpToPx(72), dpToPx(72))

            params.setMargins(150, 40, 10, 10)

            imageview.setPadding(30, 20, 30, 20)

            imageview.setImageResource(R.drawable.add_book)

            imageview.setOnClickListener { view ->
                val intent = Intent(activity, AddBookActivity::class.java)
                startActivity(intent)
            }

        view.user_profile_books.addView(imageview)

        }
    }

    fun removeAll(view: View)
    {
        view.user_profile_interests.removeAllViews()
        view.user_profile_language.setImageResource(R.drawable.user_language)
        view.user_profile_author.setImageResource(R.drawable.user_author)
        view.user_profile_genre.setImageResource(R.drawable.user_genre)
    }

    fun dpToPx(dp: Int): Int {
        val density = context!!.resources
                .displayMetrics
                .density
        return Math.round(dp.toFloat() * density)
    }
}
