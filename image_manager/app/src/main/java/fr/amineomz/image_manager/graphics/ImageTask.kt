package fr.amineomz.image_manager.graphics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import com.google.gson.JsonObject
import fr.amineomz.image_manager.models.GalleryPhoto
import fr.amineomz.image_manager.models.UserResponse
import fr.amineomz.image_manager.services.PhotoServices
import fr.amineomz.image_manager.services.UserServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.ByteArrayOutputStream

fun getGallery(user_email: String): Array<GalleryPhoto> {

    var galleryPhotos = arrayOf<GalleryPhoto>()

    // Creation du client retrofit pour toutes les requêtes http concernant les users
    val clientUser = Retrofit.Builder()
        .baseUrl("http://127.0.0.1:3000/users/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // Creation du service pour les users
    val serviceUser = clientUser.create(UserServices::class.java)

    // Creation d'un call pour le create user
    val callGetGallery = serviceUser.getGallery(user_email)

    callGetGallery.enqueue(object : Callback<UserResponse> {
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
            Log.e("Par la", "Par la")
        }
    })
    galleryPhotos.forEach {
        Log.e(it.name, it.user_email)
    }
    return galleryPhotos
}

fun downloadImage(iv: ImageView, photo: GalleryPhoto) {
    val user_email = photo.user_email
    val name = photo.name

    val jsonObject = JsonObject()
    jsonObject.addProperty("user_email", user_email)
    jsonObject.addProperty("name", name)


    val client = Retrofit.Builder()
        .baseUrl("http://127.0.0.1:3000/images/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val service = client.create(PhotoServices::class.java)


    val callGetFile = service.getImageFile(user_email, name)


    callGetFile.enqueue(object : Callback<String> {
        override fun onFailure(call: Call<String>, t: Throwable) {
            Log.e("ERROR", t.message)
        }

        override fun onResponse(call: Call<String>, response: Response<String>) {
            val result = response.body()

            val decodedString = Base64.decode(result, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

            iv.setImageBitmap(decodedByte)
        }

    })
}

fun convert(bitmap: Bitmap): String? {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
    return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
}
