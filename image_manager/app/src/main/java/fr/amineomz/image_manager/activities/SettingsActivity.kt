package fr.amineomz.image_manager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import fr.amineomz.image_manager.R
import fr.amineomz.image_manager.models.User
import fr.amineomz.image_manager.models.UserResponse
import fr.amineomz.image_manager.services.UserServices
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.nav_header_main.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SettingsActivity : AppCompatActivity() {

    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(settings_toolbar)

        user = intent.getParcelableExtra(IntentExtra.EXTRA_USER)

        edit_username.setText(user.username)
        edit_phone_number.setText(user.phone_number)
        save.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val emailPart = RequestBody.create(MultipartBody.FORM, user.email)
        val newUserNamePart = RequestBody.create(MultipartBody.FORM, edit_username.text.toString())
        val newPhoneNumberPart = RequestBody.create(MultipartBody.FORM, edit_phone_number.text.toString())

        // Creation du client retrofit pour toutes les requêtes http concernant les users
        val clientUser = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creation du service pour les users
        val serviceUser = clientUser.create(UserServices::class.java)


        // Creation d'un call pour le create user
        val callGetProfile = serviceUser.putProfile(emailPart, newUserNamePart, newPhoneNumberPart)

        // Mettre le call dans une queue pour qu'il soit executer
        callGetProfile.enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("ERROR", "NO RESPONSE!")
            }

            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                val result = response.body()
                result?.let {
                    if (it.error) {
                        Toast.makeText(applicationContext, "Erreur", Toast.LENGTH_SHORT).show()
                    }
                    if (!it.error) {
                        val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                        intent.putExtra(IntentExtra.EXTRA_EMAIL, user.email)
                        startActivity(intent)
                    }
                }
            }
        })

    }
}
