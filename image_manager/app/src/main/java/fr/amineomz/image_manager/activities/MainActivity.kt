package fr.amineomz.image_manager.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import fr.amineomz.image_manager.R
import fr.amineomz.image_manager.graphics.getGallery
import fr.amineomz.image_manager.models.User
import fr.amineomz.image_manager.services.UserServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    // ABOUT IMAGE CAPTURE
    private lateinit var currentPhotoPath: String

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var user_email: String
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user_email = intent.getStringExtra(IntentExtra.EXTRA_EMAIL)

        drawer = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(this, drawer,
            R.string.open,
            R.string.close
        )
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        // Creation du client retrofit pour toutes les requêtes http concernant les users
        val clientUser = Retrofit.Builder()
            .baseUrl("http://127.0.0.1:3000/users/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creation du service pour les users
        val serviceUser = clientUser.create(UserServices::class.java)

        // Creation d'un call pour le create user
        val callGetProfile = serviceUser.getProfile(user_email)

        // Mettre le call dans une queue pour qu'il soit executer
        callGetProfile.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("ERROR", "NO RESPONSE!")
            }

            override fun onResponse(
                call: Call<User>,
                response: Response<User>
            ) {
                val result = response.body()
                result?.let { user = result }
                nav_header_textView.text = user.username

            }
        })



    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.gallery -> {
                dispatchGalleryIntent()
                return true
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.putExtra(IntentExtra.EXTRA_USER, user)
                startActivity(intent)
                return true
            }
            R.id.deconnection -> {
                logout()
                return true
            }
        }
        return true
    }



    private fun dispatchGalleryIntent() {
        val intent = Intent(applicationContext, GalleryActivity::class.java)
        intent.putExtra(IntentExtra.EXTRA_EMAIL, user_email)
        startActivity(intent)
        // intent.type = "image/*"
        /*intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(Intent.createChooser(intent, "Select image"), REQUEST_SELECT_IMAGE)*/
    }


    private fun logout() {
        val intent = Intent(applicationContext, Login::class.java)
        intent.flags =  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
