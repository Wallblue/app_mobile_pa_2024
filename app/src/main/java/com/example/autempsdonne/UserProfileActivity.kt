package com.example.autempsdonne

import android.os.Bundle
import android.widget.EditText
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

class UserProfileActivity : AppCompatActivity() {
    /*private var usernameTv : TextView? = null
    private var firstnameEd : EditText? = null
    private var nameEd : EditText? = null
    private var emailEd : EditText? = null
    private var phoneEd : EditText? = null
    private var birthdateEd : EditText? = null
    private var addressLl : LinearLayout? = null
    private var streetNumberTv : TextView? = null
    private var streetTv : TextView? = null
    private var buildingNumberTv : TextView? = null
    private var zipcodeTv : TextView? = null
    private var cityTv : TextView? = null*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val token = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString("token", "")
        if(token == null){
            Toast.makeText(applicationContext, R.string.NullTokenErr, Toast.LENGTH_LONG).show()
            finish()
        }
        val decoded = Utils.getAuthLevel(token!!)

        if( decoded == null || decoded == AuthLevels.CHILD || decoded == AuthLevels.PARTNER ){
            Toast.makeText(applicationContext, R.string.ForbiddenErr, Toast.LENGTH_LONG).show()
            finish()
        }

        val uri = API_URL_ROOT + (if (decoded == AuthLevels.BENEFICIARY) "beneficiary" else "volunteer") + "/profile/me"

        val queue = Volley.newRequestQueue(this)
        val req = object : StringRequest(
            Request.Method.GET,
            uri,
            {
                val userInfo = if (decoded != AuthLevels.BENEFICIARY) JSONObject(it).getJSONObject("user") else JSONObject(it)

                findViewById<TextView>(R.id.profile_username_tv).text = userInfo.getString("username")
                findViewById<EditText>(R.id.profile_firstname_ed).setText(userInfo.getString("firstName"))
                findViewById<EditText>(R.id.profile_name_ed).setText(userInfo.getString("name"))
                findViewById<EditText>(R.id.profile_email_ed).setText(userInfo.getString("email"))
                findViewById<EditText>(R.id.profile_phone_ed).setText(userInfo.getString("phone"))
                findViewById<EditText>(R.id.profile_birthdate_ed).setText(userInfo.getString("birthdate"))
                findViewById<TextView>(R.id.profile_houseNb_tv).text = userInfo.getString("houseNumber")
                findViewById<TextView>(R.id.profile_street_tv).text = userInfo.getString("street")
                val building = getString(R.string.Building) + " " + userInfo.getString("buildingNumber")
                findViewById<TextView>(R.id.profile_buildingNb_tv).text = building
                findViewById<TextView>(R.id.profile_zipcode_tv).text = userInfo.getInt("zipcode").toString()
                findViewById<TextView>(R.id.profile_city_tv).text = userInfo.getString("city")
                findViewById<EditText>(R.id.profile_site_ed).setText(userInfo.getJSONObject("site").getString("name"))
            },
            {
                if(it.message != null)
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(applicationContext, R.string.LoginErr, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(req)


        //val addressLl = findViewById<LinearLayout>(R.id.profile_address_ll)
    }
}