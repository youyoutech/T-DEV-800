package fr.amineomz.image_manager.activities

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.amineomz.image_manager.R
import fr.amineomz.image_manager.graphics.ImageGalleryAdapter
import fr.amineomz.image_manager.graphics.convert
import fr.amineomz.image_manager.graphics.getGallery
import fr.amineomz.image_manager.models.GalleryPhoto
import fr.amineomz.image_manager.models.UserResponse
import fr.amineomz.image_manager.services.PhotoServices
import fr.amineomz.image_manager.services.UserServices
import kotlinx.android.synthetic.main.activity_gallery.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class GalleryActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSION_CODE_CAMERA = 1000
    private val PERMISSION_CODE_GALLERY = 1001
    private val IMAGE_CAPTURE_CODE = 1
    private val IMAGE_PICK_CODE = 2
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageGalleryAdapter: ImageGalleryAdapter
    private lateinit var galleryPhotos: Array<GalleryPhoto>
    private lateinit var user_email: String
    var image_uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        setSupportActionBar(gallery_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        intent.getStringExtra(IntentExtra.EXTRA_EMAIL)?.let { user_email = it }

        // Creation du client retrofit pour toutes les requêtes http concernant les users
        val clientUser = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creation du service pour les users
        val serviceUser = clientUser.create(UserServices::class.java)

        // Creation d'un call pour le create user
        val callGetGallery = serviceUser.getGallery(user_email)

        callGetGallery.enqueue(object: Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("Error", t.message)
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val result = response.body()
                val mutableList = mutableListOf<GalleryPhoto>()
                val galleryStr = result!!.message
                val galleryList = galleryStr.split("\n")
                    .forEach {
                        mutableList.add(GalleryPhoto(it, user_email))
                    }
                mutableList.removeAt(mutableList.size-1)
                galleryPhotos = mutableList.toTypedArray()
                imageGalleryAdapter = ImageGalleryAdapter(galleryPhotos, this@GalleryActivity) // Initialisation de l'adapteur

                val layoutManager = GridLayoutManager(this@GalleryActivity, 2) // Création du gestionnaire de diposition
                recyclerView =findViewById(R.id.rv_images)  // Création de la RecyclerView
                recyclerView.setHasFixedSize(true) // Fixer la taille de la RecyclerView même en changeant le contenu
                recyclerView.layoutManager = layoutManager // Utiliser le gestionnaire de disposition
                recyclerView.adapter = imageGalleryAdapter // Lier notre adapteur à la RecyclerView
            }
        })

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_camera -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_DENIED){
                        //permission was not enabled
                        val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE_CAMERA)
                    }
                    else{
                        //permission already granted
                        dispatchCameraIntent()
                    }
                }
                else{
                    //system os is < marshmallow
                    dispatchCameraIntent()
                }
                true
            }
            R.id.action_gallery -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_DENIED){
                        //permission denied
                        val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                        //show popup to request runtime permission
                        requestPermissions(permissions, PERMISSION_CODE_GALLERY);
                    }
                    else{
                        //permission already granted
                        dispatchGalleryIntent();
                    }
                }
                else{
                    //system OS is < Marshmallow
                    dispatchGalleryIntent();
                }
                true
            }
            android.R.id.home -> {
                startActivity(Intent(this, MainActivity::class.java)
                    .putExtra(IntentExtra.EXTRA_EMAIL, user_email))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE_GALLERY -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    dispatchGalleryIntent()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_CAPTURE_CODE){
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, image_uri)
            val base64string = convert(bitmap)
            val file = File(applicationContext.filesDir, "test")
            base64string?.let { file.writeText(it) }
            uploadFile()
        }
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
            val base64string = convert(bitmap)
            val file = File(applicationContext.filesDir, "test")
            base64string?.let {
                file.writeText(it)
            }
            uploadFile()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cv_images -> {
                val photo = galleryPhotos[view.tag as Int]

                val intent = Intent(applicationContext, ImageDetailsActivity::class.java)
                intent.putExtra(IntentExtra.EXTRA_GALLERY_PHOTO, photo)
                startActivity(intent)
            }
        }
    }


    private fun dispatchGalleryIntent() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun dispatchCameraIntent() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    private fun uploadFile() {
        val user_emailPart = RequestBody.create(MultipartBody.FORM, user_email)
        val namePart = RequestBody.create(MultipartBody.FORM, "image_" + (galleryPhotos.size + 1).toString())

        val originalFile = File(applicationContext.filesDir, "test")
        val filePart = RequestBody.create(
            MultipartBody.FORM,
            originalFile
        )
        val file = MultipartBody.Part.createFormData("file", originalFile?.name, filePart)

        val client = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/images/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = client.create(PhotoServices::class.java)

        val callGetFile = service.uploadPhoto(user_emailPart, namePart, file)



        callGetFile.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("ERROR", "No call back")
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                val result = response.body()

                Log.e("la", result?.message)

                if (result!!.error) {
                    Toast.makeText(applicationContext, result?.message, Toast.LENGTH_SHORT)
                        .show()
                }
                if (!result!!.error) {
                    val intent = Intent(this@GalleryActivity, GalleryActivity::class.java)
                    intent.putExtra(IntentExtra.EXTRA_EMAIL, user_email)
                    startActivity(intent)
                    finish()
                }
                //Log.i("RESPONSE", result?.error.toString() + ", " + result?.message)
            }

        })
    }

}
