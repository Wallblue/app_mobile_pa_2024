package com.example.autempsdonne

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class VolunteersInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteers_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val queue = Volley.newRequestQueue(this)

        val req = StringRequest(
            Request.Method.GET, intent.getStringExtra("uri"),
            {content ->
                val gotVolunteer = JSONObject(content)
                val gotSite = gotVolunteer.getJSONObject("site")

                findViewById<TextView>(R.id.volName_tv).text = gotVolunteer.getString("name")
                findViewById<TextView>(R.id.volFirstname_tv).text = gotVolunteer.getString("firstName")
                findViewById<TextView>(R.id.volUsername_tv).text = gotVolunteer.getString("username")
                findViewById<TextView>(R.id.volEmail_tv).text = gotVolunteer.getString("email")
                findViewById<TextView>(R.id.volSite_tv).text = gotSite.getString("name")
            },
            {error ->
                if(error.message != null)
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(applicationContext, R.string.VolunteerLoadingErr, Toast.LENGTH_LONG).show()

                finish()
            }
        )
        queue.add(req)
    }
}