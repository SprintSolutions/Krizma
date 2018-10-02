package com.sprint.krizma.Profile


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.util.Util
import com.sprint.krizma.Adapter.DataModel.ProfileBooks
import com.sprint.krizma.MainActivity
import com.sprint.krizma.R
import com.sprint.krizma.Utils
import kotlinx.android.synthetic.main.profile_fragment_books.*
import kotlinx.android.synthetic.main.profile_fragment_books.view.*
import kotlinx.android.synthetic.main.profile_fragment_interest.view.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException


class BooksFragment : Fragment() {

    var books = 1

    var booksType = "Both"

    val REQUEST_CAMERA = 1

    val REQUEST_GALLERY = 2

    val CAMERA_PERMISSION = 10

    val STORAGE_PERMISSION = 11

    val utils = Utils()


    companion object {
        fun newInstance(): BooksFragment {
            return BooksFragment()
    }
}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.profile_fragment_books, container, false)

        fonts(view)

        view.profile_book_type_new.setOnClickListener { view1 ->
            view.profile_book_type_new.setImageResource(R.drawable.profile_new_active)
            view.profile_book_type_used.setImageResource(R.drawable.profile_used_deactive)
            view.profile_book_type_both.setImageResource(R.drawable.profile_both_deactive)
            books = 2
            booksType = "New"
        }

        view.profile_book_type_used.setOnClickListener { view1 ->
            view.profile_book_type_new.setImageResource(R.drawable.profile_new_deactive)
            view.profile_book_type_used.setImageResource(R.drawable.profile_used_active)
            view.profile_book_type_both.setImageResource(R.drawable.profile_both_deactive)
            books = 2
            booksType= "Used"
        }

        view.profile_book_type_both.setOnClickListener { view1 ->
            view.profile_book_type_new.setImageResource(R.drawable.profile_new_deactive)
            view.profile_book_type_used.setImageResource(R.drawable.profile_used_deactive)
            view.profile_book_type_both.setImageResource(R.drawable.profile_both_active)
            books = 2
            booksType = "Both"
        }


        view.profile_add_books.setOnClickListener{view1 ->
            addBooks(view)
        }

        view.profile_book_btn.setOnClickListener { view ->
            imageDialog()
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

        view.profile_books_label.typeface = montserrat_bold

        view.profile_book_name_txt.typeface = montserrat_regular

        view.profile_book_author_txt.typeface = montserrat_regular

        view.profile_book_language_txt.typeface = montserrat_regular

    }

    fun imageDialog()
    {

        val builder = AlertDialog.Builder(activity,R.style.AlertDialogCustom)
        builder.setMessage("Select Camera or Gallery")
                .setPositiveButton("Camera", { dialog, id ->
                    cameraPermission()
                })
                .setNeutralButton("Photo Library", { dialog, id ->
                    storagePermission()
                })

        // Create the AlertDialog object and return it
        builder.create()

        builder.show()
    }

    fun cameraPermission()
    {

        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.CAMERA)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
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
        if (ContextCompat.checkSelfPermission(activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
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
        if (camera.resolveActivity(activity!!.packageManager) != null) {
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
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            try {
                val photo = data!!.extras.get("data") as Bitmap

                val resized = Bitmap.createScaledBitmap(
                        photo, 222, 222, false)

                    view!!.profile_book_btn.setImageBitmap(resized)

//                    Utils.bookPic = getStringImage(resized)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK) {
            try {
                val imageUri = data!!.data
                val imageStream = activity!!.contentResolver.openInputStream(imageUri)
                val selectedImage = BitmapFactory.decodeStream(imageStream)
                val resized = Bitmap.createScaledBitmap(
                        selectedImage, 222, 222, false)

                    view!!.profile_book_btn.setImageBitmap(resized)
//                    bookImageChanged = true

//                    Utils.bookPic = getStringImage(resized)

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

    fun addBooks(view: View)
    {
            //            if (bookImageChanged) {
            if (view.profile_book_name_txt.text.isNotEmpty()) {
                if (view.profile_book_author_txt.text.isNotEmpty()) {
                    if (view.profile_book_language_txt.text.isNotEmpty()) {

                        val drawable = profile_book_btn.drawable as BitmapDrawable
                        val book = drawable.bitmap

                        val bookImage = getStringImage(book)


                        val books = ProfileBooks(bookImage, view.profile_book_name_txt.text.toString(),
                                view.profile_book_author_txt.text.toString(),
                                view.profile_book_language_txt.text.toString(),
                                booksType)

                        utils.profile_books = books


                        view.profile_book_progress.visibility = View.VISIBLE

                        val sharedPref = activity!!.getSharedPreferences("user", FragmentActivity.MODE_PRIVATE)
                        val u_id = sharedPref.getString("user_id", "")

                        val request = JSONObject()

                        request.put("u_id", u_id)
                        request.put("name", utils.profile_books.book_name)
                        request.put("author", utils.profile_books.book_author)
                        request.put("language", utils.profile_books.book_language)
                        request.put("type", utils.profile_books.book_type)
                        request.put("image", utils.profile_books.book_image)


                        val queue = Volley.newRequestQueue(activity)
                        val url = "http://sprintsols.com/krizma/public/addBook"
                        val postRequest = object : JsonObjectRequest(Request.Method.POST, url, request,
                                Response.Listener<JSONObject> { response ->

                                    view.profile_book_progress.visibility = View.GONE
                                    //                    Toast.makeText(this,response.toString(), Toast.LENGTH_LONG).show()

                                    val code = response.getInt("code")

                                    if (code == 101) {
                                        Toast.makeText(activity, "Book added successfully", Toast.LENGTH_LONG).show()


                                        view.profile_book_name_txt.setText("")
                                        view.profile_book_author_txt.setText("")
                                        view.profile_book_language_txt.setText("")
                                        view.profile_book_btn.setImageResource(R.drawable.profile_add_pic_btn)

                                    } else {
                                        Toast.makeText(activity, "something went wrong", Toast.LENGTH_LONG).show()
                                    }

                                },
                                Response.ErrorListener {
                                    view.profile_book_progress.visibility = View.GONE
                                    Toast.makeText(activity,"The network connection was lost. Please try again.", Toast.LENGTH_LONG).show()
                                }


                        ) {
                        }
                        queue.add(postRequest)


                    }
                    else
                    {
                        Toast.makeText(activity, "Language should not be empty", Toast.LENGTH_LONG).show()
                    }
                }
                else
                {
                    Toast.makeText(activity, "Author should not be empty", Toast.LENGTH_LONG).show()
                }
            }
            else
            {
                Toast.makeText(activity, "Name should not be empty", Toast.LENGTH_LONG).show()
            }
        }

    fun getStringImage(bmp: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)

    }
}
