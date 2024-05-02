package com.example.autempsdonne

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class VolunteersListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_volunteers_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()

        val queue = Volley.newRequestQueue(this)
        val url = API_URL_ROOT + "admin/volunteers"

        val req = StringRequest(
            Request.Method.GET, url,
            {content ->
                val gotVolunteers = JSONObject(content).getJSONArray("data")
                val volunteers = mutableListOf<Volunteer>()

                for(i in 0 until gotVolunteers.length()){
                    val current = gotVolunteers.getJSONObject(i)
                    volunteers.add(
                        Volunteer(
                            current.getLong("id"),
                            current.getLong("userId"),
                            current.getString("name"),
                            current.getString("firstName"),
                            current.getString("username"),
                            current.getString("email"),
                            current.getString("phone"),
                            current.getInt("license"),
                            current.getInt("authLevel"),
                            current.getLong("siteId"),
                            current.getString("url")
                        )
                    )
                }

                val volunteersLv = findViewById<ListView>(R.id.lv_volunteers)
                val myAdapter = VolunteerAdapter(this, volunteers)
                volunteersLv.adapter = myAdapter

                volunteersLv.setOnItemClickListener{ adapter, _, pos, _ ->
                    val va = adapter.adapter as VolunteerAdapter
                    val item = va.getItem(pos) as Volunteer

                    val i = Intent(applicationContext, AssignNfcActivity::class.java)

                    i.putExtra("content", item.url)

                    startActivity(i)
                }
            },
            {error ->
                if(error.message != null)
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(applicationContext, R.string.UsersLoadingErr, Toast.LENGTH_LONG).show()

                finish()
            }
        )
        queue.add(req)
    }
}