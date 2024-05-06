package com.example.autempsdonne

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class HomeActivity : AppCompatActivity() {

    private lateinit var scanQrBtn : Button
    private lateinit var profileBtn : Button
    private lateinit var readNfcBtn : Button
    private lateinit var assignNfcBtn : Button

    private var token : String? = null
    private var authLevel : AuthLevels? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
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

    private fun setLogo() {
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

    private fun setButtonsListeners() {
        setProfileListener()
        setReadNfcListener()
        setScanQrListener()
        setAssignNfcListener()
    }

    private fun setProfileListener(){
        profileBtn.setOnClickListener{
            val i = Intent(this, UserProfileActivity::class.java)
            startActivity(i)
        }
    }

    private fun setReadNfcListener(){
        readNfcBtn.setOnClickListener{
            val i = Intent(this, ReadNfcActivity::class.java)
            startActivity(i)
        }
    }

    private fun setScanQrListener(){
        if (this.authLevel == AuthLevels.VOLUNTEER || this.authLevel == AuthLevels.ADMIN) {
            scanQrBtn.visibility = View.VISIBLE
            scanQrBtn.setOnClickListener {
                val i = Intent(this, QRScanActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun setAssignNfcListener(){
        if (this.authLevel == AuthLevels.ADMIN) {
            assignNfcBtn.visibility = View.VISIBLE
            assignNfcBtn.setOnClickListener {
                val i = Intent(this, VolunteersListActivity::class.java)
                startActivity(i)
            }
        }
    }

    private fun getButtons() {
        this.profileBtn = findViewById(R.id.home_profile_btn)
        this.readNfcBtn = findViewById(R.id.home_read_nfc_btn)
        this.scanQrBtn = findViewById(R.id.home_scanQR_btn)
        this.assignNfcBtn = findViewById(R.id.home_assignNFC_btn)
    }
}