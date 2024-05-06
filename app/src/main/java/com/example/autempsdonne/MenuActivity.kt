package com.example.autempsdonne

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

abstract class MenuActivity : AppCompatActivity() {
    protected var token : String? = null
    protected var authLevel : AuthLevels? = null
    abstract val layoutRes : Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layoutRes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.token = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString("token", "")

        if(this.token == null){
            Toast.makeText(applicationContext, R.string.NullTokenErr, Toast.LENGTH_LONG).show()
            finish()
        }

        this.authLevel = Utils.getAuthLevel(this.token!!)

        getButtons()

        setButtonsListeners()
        setLogo()
    }

    protected fun setLogo() {
        val imageView = findViewById<ImageView>(R.id.home_logo)

        val resource =
            when ( this.authLevel ) {
                AuthLevels.VOLUNTEER -> R.drawable.logo_volunteer
                AuthLevels.BENEFICIARY -> R.drawable.logo_beneficiary
                AuthLevels.ADMIN -> R.drawable.logo_admin
                else -> { null }
            }

        if ( resource != null)
            imageView.setImageResource(resource)
    }

    protected abstract fun getButtons()
    protected abstract fun setButtonsListeners()
}