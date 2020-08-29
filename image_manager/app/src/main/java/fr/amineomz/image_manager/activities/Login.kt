package fr.amineomz.image_manager.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import com.google.gson.JsonObject
import fr.amineomz.image_manager.R
import fr.amineomz.image_manager.models.User
import fr.amineomz.image_manager.models.UserResponse
import fr.amineomz.image_manager.services.UserServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


class Login : AppCompatActivity() {

    lateinit var user_email: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Déclaration des variables graphiques
        var email_address = findViewById<EditText>(R.id.email_address)
        var password_edit = findViewById<EditText>(R.id.password)
        var login = findViewById<Button>(R.id.login)
        var redirect_register = findViewById<TextView>(R.id.redirect_register)
        var forgot_password = findViewById<TextView>(R.id.forgot_password)


        // Creation du client retrofit pour toutes les requêtes http concernant les users
        val clientUser = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creation du service pour les users
        val serviceUser = clientUser.create(UserServices::class.java)

        // Le click sur le bouton login
        login.setOnClickListener {
            // Récupération des valeurs des champs du formulaire
            val email: String = email_address.text.toString().trim()
            val password: String = password_edit.text.toString().trim()

            // Règles de validation
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

            if (password.length < 6) {
                password_edit.setError("Le mot de passe doit contenir au moins 6 caractères")
                password_edit.requestFocus()
                return@setOnClickListener
            }

            // Mettre les données du formulaire dans un object Json
            val jsonObject = JsonObject()
            jsonObject.addProperty("email", email)
            jsonObject.addProperty("password", password)

            // Creation d'un call pour le create user
            val callLoginUser = serviceUser.loginUser(jsonObject)

            // Mettre le call dans une queue pour qu'il soit executer
            callLoginUser.enqueue(object : Callback<UserResponse> {
                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("ERROR", "NO RESPONSE!")
                }

                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    val result = response.body()
                    if (result!!.error) {
                        Toast.makeText(applicationContext, result.message, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        user_email = email
                        redirectToMainActivity(result.message)
                    }
                }
            })

            /*forgot_password.setOnClickListener {
            val resetEmail: EditText = EditText(it.context)
            val passwordResetDialog: AlertDialog.Builder = AlertDialog.Builder(it.context)
            passwordResetDialog.setTitle("Réinitialiser le mot de passe ?")
            passwordResetDialog.setMessage("Entrez votre e-mail pour recevoir le lien de réinitialisation")
            passwordResetDialog.setView(resetEmail)

            passwordResetDialog.setPositiveButton("Oui", DialogInterface.OnClickListener { dialog, which ->
                val mail_address: String = resetEmail.text.toString()
                firebaseAuth.sendPasswordResetEmail(mail_address).addOnSuccessListener {
                    Toast.makeText(this, "Email de réinitialisation envoyé", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Error: " + it.message, Toast.LENGTH_LONG).show()
                }
            })

            passwordResetDialog.setNegativeButton("Non", DialogInterface.OnClickListener { dialog, which ->

            })

            passwordResetDialog.create().show()
        }*/


        }

        redirect_register.setOnClickListener {
            startActivity(Intent(applicationContext, Register::class.java))
            finish()
        }
    }

    fun redirectToMainActivity(token: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(IntentExtra.EXTRA_TOKEN, token)
        intent.putExtra(IntentExtra.EXTRA_EMAIL, user_email)
        startActivity(intent)
        finish()
    }
}
