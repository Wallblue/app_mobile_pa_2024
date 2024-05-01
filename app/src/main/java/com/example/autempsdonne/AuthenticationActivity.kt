package com.example.autempsdonne

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_authentication)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Set on Submit listener
        findViewById<Button>(R.id.auth_submit_btn).setOnClickListener{
            val identifier = findViewById<EditText>(R.id.auth_username_et).text.toString()
            val password = findViewById<EditText>(R.id.auth_password_et).text.toString()

            val identifierType = if (identifier.contains('@')) "email" else "username"

            val url = API_URL_ROOT + "auth/login"
            val reqBody = JSONObject().apply {
                put(identifierType, identifier)
                put("password", password)
            }

            val queue = Volley.newRequestQueue(this)
            val req = JsonObjectRequest(
                Request.Method.POST, url, reqBody,
                {content ->
                    println(content)
                    if(!content.getBoolean("success"))
                        Toast.makeText(applicationContext, R.string.BadCredentials, Toast.LENGTH_LONG).show()
                    else{
                        getSharedPreferences("autempsdonne", MODE_PRIVATE).edit().putString("token", content.getString("jwt")).apply()
                        val i = Intent(this, HomeActivity::class.java)
                        startActivity(i)
                    }
                },
                {error ->
                    if(error.message != null)
                        Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                    else
                        Toast.makeText(applicationContext, R.string.LoginErr, Toast.LENGTH_LONG).show()
                }
            )
            queue.add(req)
        }
    }
}