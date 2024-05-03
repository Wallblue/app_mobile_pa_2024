package com.example.autempsdonne

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<Button>(R.id.home_btn1).setOnClickListener{
            Toast.makeText(applicationContext, "b1", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.home_profile_btn).setOnClickListener{
            val i = Intent(this, UserProfileActivity::class.java)
            startActivity(i)
        }

        findViewById<Button>(R.id.home_read_nfc_btn).setOnClickListener{
            val i = Intent(this, ReadNfcActivity::class.java)
            startActivity(i)
        }

        findViewById<Button>(R.id.home_assignNFC_btn).setOnClickListener{
            val i = Intent(this, VolunteersListActivity::class.java)
            startActivity(i)
        }
    }
}