package com.example.autempsdonne

import android.content.Intent
import android.widget.Button
import android.widget.Toast

class QrMenuActivity() : MenuActivity() {

    private lateinit var showBtn : Button
    private lateinit var editBtn : Button

    override val layoutRes: Int = R.layout.activity_qr_menu

    override fun getButtons() {
        this.showBtn = findViewById(R.id.qr_show_btn)
        this.editBtn = findViewById(R.id.qr_edit_btn)
    }

    override fun setButtonsListeners() {
        setShowListener()
        setEditListener()
    }

    private fun setShowListener(){
        // TODO Change mode
        showBtn.setOnClickListener {
            val i = Intent(this, QRScanActivity::class.java)
            startActivity(i)
        }
    }

    private fun setEditListener(){
        // TODO change mode
        editBtn.setOnClickListener {
            Toast.makeText(applicationContext, "edit", Toast.LENGTH_SHORT).show()
            // TODO
        }
    }
}