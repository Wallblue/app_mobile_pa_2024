package com.example.autempsdonne

import android.content.Intent
import android.view.View
import android.widget.Button

class HomeActivity : MenuActivity() {

    private lateinit var scanQrBtn : Button
    private lateinit var profileBtn : Button
    private lateinit var readNfcBtn : Button
    private lateinit var assignNfcBtn : Button

    override var layoutRes: Int = R.layout.activity_home
    override fun getButtons() {
        this.profileBtn = findViewById(R.id.home_profile_btn)
        this.readNfcBtn = findViewById(R.id.home_read_nfc_btn)
        this.scanQrBtn = findViewById(R.id.home_scanQR_btn)
        this.assignNfcBtn = findViewById(R.id.home_assignNFC_btn)
    }

    override fun setButtonsListeners() {
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
                val i = Intent(this, QrMenuActivity::class.java)
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
}