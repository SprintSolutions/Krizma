package com.sprint.krizma

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.applozic.mobicomkit.api.conversation.MessageListTask
import com.applozic.mobicomkit.uiwidgets.conversation.ConversationUIService
import com.applozic.mobicomkit.uiwidgets.conversation.activity.ConversationActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.makeramen.roundedimageview.RoundedImageView
import com.sprint.krizma.CustomComponents.CustomImageView
import kotlinx.android.synthetic.main.activity_user.*
import java.util.*


class AuthorActivity : AppCompatActivity() {

    var random = 0

    val colors = arrayOf(R.drawable.rounded_corner_text,R.drawable.rounded_corner_text_2,R.drawable.rounded_corner_text_3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        window.statusBarColor = ContextCompat.getColor(this, R.color.black)


        val id = intent.getIntExtra("ID",0)
        val name = intent.getStringExtra("Name")
        val age = intent.getStringExtra("Age")
        val country = intent.getStringExtra("Country")
        val gender = intent.getStringExtra("Gender")
        val image = intent.getStringExtra("Image")
        val language = intent.getStringExtra("Language")
        val author = intent.getStringExtra("Author")
        val genre = intent.getStringExtra("Genre")
        val index = intent.getIntExtra("Index",0)

        user_back.setOnClickListener { super.onBackPressed() }

        userGenres(genre)

        userBooks(index)

        user_message_btn.setOnClickListener {
            val intent = Intent(this,ConversationActivity::class.java)
            intent.putExtra(ConversationUIService.USER_ID, id.toString())
            intent.putExtra(ConversationUIService.DISPLAY_NAME, name.toString())
            intent.putExtra(ConversationUIService.TAKE_ORDER,true)
            startActivity(intent)
//            val intent = Intent(this,VideoActivity::class.java)
//            startActivity(intent)
        }

        user_books_label.setOnClickListener { view ->
            val intent = Intent(this,AuthorBooksActivity::class.java)
            intent.putExtra("index",index)
            startActivity(intent)
        }

        user_books_last.setOnClickListener {
            val intent = Intent(this,AuthorBooksActivity::class.java)
            intent.putExtra("index",index)
            startActivity(intent)
        }

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(object:OnSuccessListener<InstanceIdResult> {
            override fun onSuccess(instanceIdResult:InstanceIdResult) {

                val deviceToken = instanceIdResult.token

                Log.d("token",deviceToken)
                // Do whatever you want with your token now
                // i.e. store it on SharedPreferences or DB
                // or directly send it to server
            }
        })


//        val token = FirebaseInstanceId.getInstance().token
//        Log.d("token",token)


        if(name.isNotEmpty())
        {
            user_name_txt.visibility = View.VISIBLE
            user_name_txt.text = name
        }

        if(age.isNotEmpty())
        {
            user_age_txt.visibility = View.VISIBLE
            user_age_txt.text = age
        }

        if(gender.isNotEmpty())
        {
            user_gender_txt.visibility = View.VISIBLE
            user_gender_txt.text = gender
        }

        if(country.isNotEmpty())
        {
            user_country_txt.visibility = View.VISIBLE
            user_country_txt.text = country
        }

        Glide.with(this).load(image).into(user_pic_btn)

        user_language.setOnClickListener { view -> userLanguages(language) }

        user_author.setOnClickListener { view -> userAuthors(author) }

        user_genre.setOnClickListener{ view -> userGenres(genre) }

    }

    fun userLanguages(language:String)
    {
        removeAll()

        user_language.setImageResource(R.drawable.user_language_active)

        val languages:List<String> = language.split(",")

        if(languages.isNotEmpty()) {

            for (i in 0 until languages.size) {
                random = Random().nextInt(3)

                val textview = TextView(this)

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)

                params.setMargins(20, 40, 10, 10)

                textview.setPadding(30, 20, 30, 20)

                textview.setTextColor(Color.WHITE)

                textview.textSize = 14F

                textview.layoutParams = params

                textview.text = languages[i]

                textview.setBackgroundResource(colors[random])

                textview.typeface = ResourcesCompat.getFont(this, R.font.montserrat_regular)


//            prevTextViewId = curTextViewId
                user_interests.addView(textview)

            }
        }

    }

    fun userAuthors(author:String)
    {
        removeAll()

        user_author.setImageResource(R.drawable.user_author_active)

        if(author.isNotEmpty()) {

            val authors:List<String> = author.split(",")

            for (i in 0 until authors.size) {
                random = Random().nextInt(3)

                val textview = TextView(this)

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)

                params.setMargins(20, 40, 10, 10)

                textview.setPadding(30, 20, 30, 20)

                textview.setTextColor(Color.WHITE)

                textview.textSize = 14F

                textview.layoutParams = params

                textview.text = authors[i]

                textview.setBackgroundResource(colors[random])

                textview.typeface = ResourcesCompat.getFont(this, R.font.montserrat_regular)


//            prevTextViewId = curTextViewId
                user_interests.addView(textview)

            }
        }

    }

    fun userGenres(genre:String)
    {
        removeAll()

        user_genre.setImageResource(R.drawable.user_genre_active)

        if(genre.isNotEmpty()) {

        val genres:List<String> = genre.split(",")

            for (i in 0 until genres.size) {
                random = Random().nextInt(3)

                val textview = TextView(this)

                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT)

                params.setMargins(20, 40, 10, 10)

                textview.setPadding(30, 20, 30, 20)

                textview.setTextColor(Color.WHITE)

                textview.textSize = 14F

                textview.layoutParams = params

                textview.text = genres[i]

                textview.setBackgroundResource(colors[random])

                textview.typeface = ResourcesCompat.getFont(this, R.font.montserrat_regular)


//            prevTextViewId = curTextViewId
                user_interests.addView(textview)

            }
        }

    }

    fun userBooks(index:Int)
    {
        val books = Utils.users[index].u_books

        if(books.size<4)
        {
            for (i in 0 until books.size) {
                val imageview = RoundedImageView(this)

                imageview.scaleType = ImageView.ScaleType.CENTER_CROP
                imageview.cornerRadius = 15F
                imageview.borderWidth = 2F
                imageview.borderColor = Color.WHITE
                imageview.isOval = false

                val params = LinearLayout.LayoutParams(dpToPx(72), dpToPx(72))

                params.setMargins(20, 40, 10, 10)

                imageview.setPadding(20, 20, 10, 20)

                imageview.setBackgroundResource(R.drawable.rounded_corner_image)

                if (books[i].b_image.isNotEmpty()) {
                    Glide.with(this).asBitmap().load(books[i].b_image).into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val resized = Bitmap.createScaledBitmap(
                                    resource, dpToPx(72), dpToPx(72), false)
                            imageview.setImageBitmap(resized)
                        }
                    })
                } else {
                    Glide.with(this).asBitmap().load(R.drawable.user_book_bg).into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            imageview.setImageBitmap(resource)
                        }
                    })
                }

                user_books.addView(imageview)

                imageview.setOnClickListener {
                    val intent = Intent(this, AuthorBooksActivity::class.java)
                    intent.putExtra("index",index)
                    startActivity(intent)
                }
            }
        }

        else
        {
            for (i in 0..3)
            {
                val imageview = RoundedImageView(this)

                imageview.scaleType = ImageView.ScaleType.CENTER_CROP
                imageview.cornerRadius = 15F
                imageview.borderWidth = 2F
                imageview.borderColor = Color.WHITE
                imageview.isOval = false

                val params = LinearLayout.LayoutParams(dpToPx(72), dpToPx(72))

                params.setMargins(20, 40, 10, 10)

                imageview.setPadding(10, 20, 10, 20)

                imageview.setBackgroundResource(R.drawable.rounded_corner_image)


                if(i==3)
                {
                    val bookcount = books.size-3
                    user_books_last.visibility = View.VISIBLE
                    user_book_count.text = "+"+bookcount.toString()
                    user_book_count.visibility = View.VISIBLE
                    break
                }

                if(books[i].b_image.isNotEmpty()) {
                    Glide.with(this).asBitmap().load(books[i].b_image).into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            val resized = Bitmap.createScaledBitmap(
                                    resource, dpToPx(72), dpToPx(72), false)
                            imageview.setImageBitmap(resized)
                        }
                    })
                }

                else{
                    Glide.with(this).asBitmap().load(R.drawable.user_book_bg).into(object : SimpleTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            imageview.setImageBitmap(resource)
                        }
                    })
                }

                user_books.addView(imageview)

                imageview.setOnClickListener {
                    val intent = Intent(this, AuthorBooksActivity::class.java)
                    intent.putExtra("index",index)
                    startActivity(intent)
                }
            }
        }
    }

    fun removeAll()
    {
        user_interests.removeAllViews()
        user_language.setImageResource(R.drawable.user_language)
        user_author.setImageResource(R.drawable.user_author)
        user_genre.setImageResource(R.drawable.user_genre)
    }

    fun dpToPx(dp: Int): Int {
        val density = resources
                .displayMetrics
                .density
        return Math.round(dp.toFloat() * density)
    }

}
