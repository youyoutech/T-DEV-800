package fr.amineomz.image_manager.activities

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import fr.amineomz.image_manager.R
import fr.amineomz.image_manager.graphics.downloadImage
import fr.amineomz.image_manager.models.GalleryPhoto
import fr.amineomz.image_manager.models.UserResponse
import fr.amineomz.image_manager.services.PhotoServices
import kotlinx.android.synthetic.main.activity_image_details.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ImageDetailsActivity : AppCompatActivity() {

    lateinit var photo: GalleryPhoto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_details)

        setSupportActionBar(detail_toolbar)

        photo = intent.getParcelableExtra<GalleryPhoto>(IntentExtra.EXTRA_GALLERY_PHOTO)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.photo_details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                val nameEditText = EditText(this)
                val dialog = android.app.AlertDialog.Builder(this)
                    .setMessage("Modifier le nom de la photo:")
                    .setView(nameEditText)
                    .setPositiveButton("Confirmer") { _, _ ->
                        val name = nameEditText.text.toString()
                        Log.e("Dialogue", name)
                        if (name.length > 1) {
                            update(photo.user_email, photo.name, name)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Choisissez un nom d'image valide",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .setNegativeButton("Annuler", null)
                    .create()
                dialog.show()
                true
            }
            R.id.action_delete -> {
                val dialog = android.app.AlertDialog.Builder(this)
                    .setMessage("Voulez-vous vraiment supprimer " + photo.name + "?")
                    .setPositiveButton("Confirmer") { _, _ ->
                        delete(photo.user_email, photo.name)
                    }
                    .setNegativeButton("Annuler", null)
                    .create()
                dialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        downloadImage(iv_detail, photo)
    }

    private fun update(user_email: String, name: String, new_name: String) {
        val userEmailPart = RequestBody.create(MultipartBody.FORM, user_email)
        val namePart = RequestBody.create(MultipartBody.FORM, name)
        val newNamePart = RequestBody.create(MultipartBody.FORM, new_name)

        // Creation du client retrofit pour toutes les requêtes http concernant les users
        val clientUser = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/images/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creation du service pour les users
        val servicePhoto = clientUser.create(PhotoServices::class.java)

        val callPutPhoto = servicePhoto.putPhoto(userEmailPart, namePart, newNamePart)

        callPutPhoto.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("ERROR", "NO RESPONSE!")
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {

                val result = response.body()
                result?.let {
                    if (it.error) {
                        Toast.makeText(applicationContext, "Erreur", Toast.LENGTH_SHORT).show()
                    }
                    if (!it.error) {
                        val intent = Intent(this@ImageDetailsActivity, GalleryActivity::class.java)
                        intent.putExtra(IntentExtra.EXTRA_EMAIL, photo.user_email)
                        startActivity(intent)
                        Toast.makeText(
                            applicationContext,
                            it.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        })
    }

    private fun delete(user_email: String, name: String) {
        // Creation du client retrofit pour toutes les requêtes http concernant les users
        val clientUser = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/images/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creation du service pour les users
        val servicePhoto = clientUser.create(PhotoServices::class.java)

        val calldeletePhoto = servicePhoto.deletePhoto(user_email, name)

        calldeletePhoto.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("ERROR", "NO RESPONSE!")
            }

            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {

                val result = response.body()
                result?.let {
                    if (it.error) {
                        Toast.makeText(applicationContext, "Erreur", Toast.LENGTH_SHORT).show()
                    }
                    if (!it.error) {
                        val intent = Intent(this@ImageDetailsActivity, GalleryActivity::class.java)
                        intent.putExtra(IntentExtra.EXTRA_EMAIL, photo.user_email)
                        startActivity(intent)
                    }
                }
            }

        })
    }
}
