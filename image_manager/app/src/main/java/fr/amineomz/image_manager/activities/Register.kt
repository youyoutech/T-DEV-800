package fr.amineomz.image_manager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import com.google.gson.JsonObject
import fr.amineomz.image_manager.R
import fr.amineomz.image_manager.models.UserResponse
import fr.amineomz.image_manager.services.UserServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Déclaration des variables graphiques
        var username_edit = findViewById<EditText>(R.id.username)
        var email_address = findViewById<EditText>(R.id.email_address)
        var password_edit = findViewById<EditText>(R.id.password)
        var phone_number_edit = findViewById<EditText>(R.id.phone_number)
        var register = findViewById<Button>(R.id.register)
        var redirect_login = findViewById<TextView>(R.id.redirect_login)

        // Creation du client retrofit pour toutes les requêtes http concernant les users
        val clientUser = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creation du service pour les users
        val serviceUser = clientUser.create(UserServices::class.java)

        // Le click sur le bouton register
        register.setOnClickListener {

            // Récupération des valeurs des champs du formulaire
            val email = email_address.text.toString().trim()
            val password = password_edit.text.toString().trim()
            val username = username_edit.text.toString().trim()
            val phone_number = phone_number_edit.text.toString().trim()

            // Règles de validation
            if (username.isEmpty()) {
                username_edit.setError("Pseudonyme obligatoire.")
                username_edit.requestFocus()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(email)) {
                email_address.setError("E-mail obligatoire.")
                email_address.requestFocus()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                password_edit.setError("Mot de passe obligatoire.")
                password_edit.requestFocus()
                return@setOnClickListener
            }

            if (phone_number.isEmpty()) {
                phone_number_edit.setError("Numéro de téléphone obligatoire.")
                phone_number_edit.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                password_edit.setError("Le mot de passe doit contenir au moins 6 caractères")
                password_edit.requestFocus()
                return@setOnClickListener
            }

            // Mettre les données du formulaire dans un object Json
            val jsonObject = JsonObject()
            jsonObject.addProperty("email", email)
            jsonObject.addProperty("username", username)
            jsonObject.addProperty("password", password)
            jsonObject.addProperty("phone_number", phone_number)

            // Creation d'un call pour le create user
            val callCreateUser = serviceUser.createUser(jsonObject)

            var result: UserResponse? = UserResponse(true, "")

            // Mettre le call dans une queue pour qu'il soit executer
            callCreateUser.enqueue(object : Callback<UserResponse> {

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("ERROR", "NO RESPONSE!")
                }

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    result = response.body()

                    if (result!!.error) {
                        Toast.makeText(applicationContext, result?.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    if (!result!!.error) {
                        redirectToLoginActivity()
                    }
                }

            })
        }

        redirect_login.setOnClickListener {
            startActivity(Intent(applicationContext, Login::class.java))
            finish()
        }
    }

    fun redirectToLoginActivity() {
        val intent = Intent(applicationContext, Login::class.java)
        startActivity(intent)
        finish()
    }
}
