package com.sprint.krizma

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hootsuite.nachos.terminator.ChipTerminatorHandler
import com.sprint.krizma.Adapter.AuthorBookListAdapter
import com.sprint.krizma.Adapter.DataModel.Book
import com.sprint.krizma.Adapter.DataModel.Data
import com.sprint.krizma.Adapter.DataModel.ProfileBooks
import com.sprint.krizma.Main.SearchFragment
import kotlinx.android.synthetic.main.activity_add_book.*
import kotlinx.android.synthetic.main.activity_user_books.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException

class AddBookActivity : AppCompatActivity() {

    var books = 1

    var booksType = "Both"

    val REQUEST_CAMERA = 1

    val REQUEST_GALLERY = 2

    val CAMERA_PERMISSION = 10

    val STORAGE_PERMISSION = 11

    val utils = Utils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        window.statusBarColor = ContextCompat.getColor(this, R.color.statusbar)

        add_book_back_btn.setOnClickListener {
            val intent = Intent(this,UserBooksActivity::class.java)
            startActivity(intent)
        }

        val languages = Data.languages

        val languageAdapter = ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, languages)

        add_book_language_txt.setAdapter(languageAdapter)

        add_book_author_txt.addChipTerminator('\n', ChipTerminatorHandler.BEHAVIOR_CHIPIFY_ALL)


        if(intent.hasExtra("id"))
        {
            val id = intent.getIntExtra("id",0)
            val image = intent.getStringExtra("image")
            val name = intent.getStringExtra("name")
            val author = intent.getStringExtra("author")
            val language = intent.getStringExtra("language")
            val type = intent.getStringExtra("type")


            Glide.with(this).load(image).apply(RequestOptions().placeholder(R.drawable.profile_add_pic_btn)).into(add_book_btn)

            add_book_name_txt.setText(name)
            add_book_author_txt.setText(author)
            add_book_language_txt.setText(language)

            when (type) {
                "Both" -> {
                    removebutton()
                    add_book_type_both.setImageResource(R.drawable.profile_both_active)
                    booksType = "Both"
                }
                "New" -> {
                    removebutton()
                    add_book_type_new.setImageResource(R.drawable.profile_new_active)
                    booksType = "New"
                }
                "Used" -> {
                    removebutton()
                    add_book_type_used.setImageResource(R.drawable.profile_used_active)
                    booksType = "Used"
                }
            }
        }

        add_book_btn.setOnClickListener { view -> imageDialog() }

        add_book_type_new.setOnClickListener { view1 ->
            add_book_type_new.setImageResource(R.drawable.profile_new_active)
            add_book_type_used.setImageResource(R.drawable.profile_used_deactive)
            add_book_type_both.setImageResource(R.drawable.profile_both_deactive)
            books = 2
            booksType = "New"
        }

        add_book_type_used.setOnClickListener { view1 ->
            add_book_type_new.setImageResource(R.drawable.profile_new_deactive)
            add_book_type_used.setImageResource(R.drawable.profile_used_active)
            add_book_type_both.setImageResource(R.drawable.profile_both_deactive)
            books = 2
            booksType= "Used"
        }

        add_book_type_both.setOnClickListener { view1 ->
            add_book_type_new.setImageResource(R.drawable.profile_new_deactive)
            add_book_type_used.setImageResource(R.drawable.profile_used_deactive)
            add_book_type_both.setImageResource(R.drawable.profile_both_active)
            books = 2
            booksType = "Both"
        }

        add_add_books.setOnClickListener { view ->
            if(intent.hasExtra("id"))
            {
                val id = intent.getIntExtra("id",0)
                val index = intent.getIntExtra("index",0)
                updateBook(id,index)
            }
            else
            {
                addBook()
            }

        }
    }

    fun imageDialog()
    {

        val builder = AlertDialog.Builder(this,R.style.AlertDialogCustom)
        builder.setMessage("Select Camera or Gallery")
                .setPositiveButton("Camera") { dialog, id ->
                    cameraPermission()
                }
                .setNeutralButton("Photo Library") { dialog, id ->
                    storagePermission()
                }

        builder.create()

        builder.show()
    }

    fun cameraPermission()
    {

        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            camera()
        }

    }

    fun storagePermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            gallery()
        }
    }

    fun camera()
    {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (camera.resolveActivity(packageManager) != null) {
            startActivityForResult(camera, REQUEST_CAMERA)
        }
    }

    fun gallery()
    {
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.type = "image/*"
        startActivityForResult(gallery, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                val photo = data!!.extras.get("data") as Bitmap

                val resized = Bitmap.createScaledBitmap(
                        photo, 222, 222, false)

                add_book_btn.setImageBitmap(resized)

//                    Utils.bookPic = getStringImage(resized)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = data!!.data
                val imageStream = contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val resized = Bitmap.createScaledBitmap(
                        selectedImage, 222, 222, false)

                add_book_btn.setImageBitmap(resized)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun addBook()
    {

        if (add_book_name_txt.text.isNotEmpty()) {
            if (add_book_author_txt.text.isNotEmpty()) {
                if (add_book_language_txt.text.isNotEmpty()) {

                    val drawable = add_book_btn.drawable as BitmapDrawable
                    val book = drawable.bitmap

                    val bookImage = getStringImage(book)


                    val books = ProfileBooks(bookImage, add_book_name_txt.text.toString(),
                            add_book_author_txt.text.toString(),
                            android.text.TextUtils.join(",",add_book_language_txt.allChips),
                            booksType)

                    utils.profile_books = books


                    add_book_progress.visibility = View.VISIBLE

                    val sharedPref = getSharedPreferences("user", FragmentActivity.MODE_PRIVATE)
                    val u_id = sharedPref.getString("user_id", "")

                    val request = JSONObject()

                    request.put("u_id", u_id)
                    request.put("name", utils.profile_books.book_name)
                    request.put("author", utils.profile_books.book_author)
                    request.put("language", utils.profile_books.book_language)
                    request.put("type", utils.profile_books.book_type)
                    request.put("image", utils.profile_books.book_image)


                    val queue = Volley.newRequestQueue(this)
                    val url = "http://sprintsols.com/krizma/public/addBook"
                    val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                            Response.Listener<JSONObject> { response ->

                                add_book_progress.visibility = View.GONE
                                //                    Toast.makeText(this,response.toString(), Toast.LENGTH_LONG).show()

                                val code = response.getInt("code")

                                if (code == 101) {
                                    Toast.makeText(this, "Book added successfully", Toast.LENGTH_LONG).show()

                                    val b_id = response.getInt("b_id")

                                    add_book_name_txt.setText("")
                                    add_book_author_txt.setText("")
                                    add_book_language_txt.setText("")
                                    add_book_btn.setImageResource(R.drawable.profile_add_pic_btn)

                                    Utils.books.add(Book(b_id,u_id.toInt(),utils.profile_books.book_name,
                                            utils.profile_books.book_author,utils.profile_books.book_author,
                                            utils.profile_books.book_type,utils.profile_books.book_image))



                                } else {
                                    Toast.makeText(this, "something went wrong", Toast.LENGTH_LONG).show()
                                }

                            },
                            Response.ErrorListener {
                                add_book_progress.visibility = View.GONE
                                Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                            }

                    ) {
                    }
                    queue.add(postRequest)

                }
                else
                {
                    Toast.makeText(this, "Language should not be empty", Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                Toast.makeText(this, "Author should not be empty", Toast.LENGTH_LONG).show()
            }
        }
        else
        {
            Toast.makeText(this, "Name should not be empty", Toast.LENGTH_LONG).show()
        }
    }

    fun getStringImage(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)

    }

    fun removebutton()
    {
        add_book_type_new.setImageResource(R.drawable.profile_new_deactive)
        add_book_type_used.setImageResource(R.drawable.profile_used_deactive)
        add_book_type_both.setImageResource(R.drawable.profile_both_deactive)
    }

    fun updateBook(id:Int,index:Int)
    {
        if (add_book_name_txt.text.isNotEmpty()) {
            if (add_book_author_txt.text.isNotEmpty()) {
                if (add_book_language_txt.text.isNotEmpty()) {

                    val drawable = add_book_btn.drawable as BitmapDrawable
                    val book = drawable.bitmap

                    val bookImage = getStringImage(book)


                    val books = ProfileBooks(bookImage, add_book_name_txt.text.toString(),
                            add_book_author_txt.text.toString(),
                            android.text.TextUtils.join(",",add_book_language_txt.allChips),
                            booksType)

                    utils.profile_books = books


                    add_book_progress.visibility = View.VISIBLE

                    val sharedPref = getSharedPreferences("user", FragmentActivity.MODE_PRIVATE)
                    val u_id = sharedPref.getString("user_id", "")

                    val request = JSONObject()

                    request.put("u_id", u_id)
                    request.put("b_id", id)
                    request.put("name", utils.profile_books.book_name)
                    request.put("author", utils.profile_books.book_author)
                    request.put("language", utils.profile_books.book_language)
                    request.put("type", utils.profile_books.book_type)
                    request.put("image", utils.profile_books.book_image)


                    val queue = Volley.newRequestQueue(this)
                    val url = "http://sprintsols.com/krizma/public/updateBook"
                    val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                            Response.Listener<JSONObject> { response ->

                                add_book_progress.visibility = View.GONE
                                //                    Toast.makeText(this,response.toString(), Toast.LENGTH_LONG).show()

                                val code = response.getInt("code")

                                if (code == 101) {
                                    Toast.makeText(this, "Book added successfully", Toast.LENGTH_LONG).show()


                                    add_book_name_txt.setText("")
                                    add_book_author_txt.setText("")
                                    add_book_language_txt.setText("")
                                    add_book_btn.setImageResource(R.drawable.profile_add_pic_btn)


                                    Utils.books[index].b_name = utils.profile_books.book_name
                                    Utils.books[index].b_author = utils.profile_books.book_author
                                    Utils.books[index].b_language = utils.profile_books.book_language
                                    Utils.books[index].b_type = utils.profile_books.book_type
                                    Utils.books[index].b_image = utils.profile_books.book_image


                                } else {
                                    Toast.makeText(this, "something went wrong", Toast.LENGTH_LONG).show()
                                }

                            },
                            Response.ErrorListener {
                                add_book_progress.visibility = View.GONE
                                Toast.makeText(this,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                            }

                    ) {
                    }
                    queue.add(postRequest)

                }
                else
                {
                    Toast.makeText(this, "Language should not be empty", Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                Toast.makeText(this, "Author should not be empty", Toast.LENGTH_LONG).show()
            }
        }
        else
        {
            Toast.makeText(this, "Name should not be empty", Toast.LENGTH_LONG).show()
        }
    }
}
