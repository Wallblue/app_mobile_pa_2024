package com.example.autempsdonne

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

        findViewById<Button>(R.id.home_btn2).setOnClickListener{
            Toast.makeText(applicationContext, "b2", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.home_btn3).setOnClickListener{
            Toast.makeText(applicationContext, "b3", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.home_assignNFC_btn).setOnClickListener{
            /*
            * TODO
            *  Lister les utilisateurs
            *  Choisir utilisateur
            *  Scanner NFC
            *  Valider et renvoyer au menu
            * */
        }
    }
}