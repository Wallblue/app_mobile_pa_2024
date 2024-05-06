package com.example.autempsdonne

import android.content.Intent
import android.widget.Button
import android.widget.Toast

class QrMenuActivity() : MenuActivity() {

    private lateinit var showBtn : Button
    private lateinit var editQuantityBtn : Button

    override val layoutRes: Int = R.layout.activity_qr_menu

    override fun getButtons() {
        this.showBtn = findViewById(R.id.qr_show_btn)
        this.editQuantityBtn = findViewById(R.id.qr_editQuantity_btn)
    }

    override fun setButtonsListeners() {
        setShowListener()
        setEditListener()
    }

    private fun setShowListener(){
        showBtn.setOnClickListener {
            val i = Intent(this, QRScanActivity::class.java)
            i.putExtra(QRScanActivity.EXTRA_MODE, QrScanModes.SHOW_MODE)
            startActivity(i)
        }
    }

    private fun setEditListener(){
        editQuantityBtn.setOnClickListener {
            val i = Intent(this, QRScanActivity::class.java)
            i.putExtra(QRScanActivity.EXTRA_MODE, QrScanModes.EDIT_MODE)
            startActivity(i)
        }
    }
}