package com.example.autempsdonne

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class UserProfileActivity : AppCompatActivity() {
    private var usernameTv : TextView? = null
    private var firstnameEd : EditText? = null
    private var nameEd : EditText? = null
    private var emailEd : EditText? = null
    private var phoneEd : EditText? = null
    private var birthdateTv: TextView? = null
    private var addressLl : LinearLayout? = null
    private var houseNumberTv : TextView? = null
    private var streetTv : TextView? = null
    private var buildingNumberTv : TextView? = null
    private var zipcodeTv : TextView? = null
    private var cityTv : TextView? = null
    private var siteEd : EditText? = null
    private var editBtn : Button? = null

    private var editMode : Boolean = false
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

        this.getViewElements()

        val uri = API_URL_ROOT + (if (decoded == AuthLevels.BENEFICIARY) "beneficiary" else "volunteer") + "/profile/me"

        val queue = Volley.newRequestQueue(this)
        val req = object : StringRequest(
            Method.GET,
            uri,
            {
                val userInfo = if (decoded != AuthLevels.BENEFICIARY) JSONObject(it).getJSONObject("user") else JSONObject(it)

                this.setViewElementsValue(userInfo)

                // When we have set all the data we set the total layout to visible
                findViewById<LinearLayout>(R.id.main).visibility = View.VISIBLE

                // We set the edit mode button listener
                editBtn?.setOnClickListener {
                    this.switchEditMode()
                }

                // We set the birthdate editor listener
                birthdateTv?.setOnClickListener {
                    // If editMode is not on we leave
                    if(!this.editMode) return@setOnClickListener

                    this.openCalendarModal()
                }

                // We set the address editor listener
                addressLl?.setOnClickListener {
                    if(!this.editMode) return@setOnClickListener

                    EditAddressDialogFragment().show(supportFragmentManager, "EditAddressDialog")
                }
            },
            {
                if(it.message != null)
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(applicationContext, R.string.LoginErr, Toast.LENGTH_LONG).show()

                finish()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(req)

    }

    private fun getViewElements(){
        usernameTv = findViewById(R.id.profile_username_tv)
        firstnameEd = findViewById(R.id.profile_firstname_ed)
        nameEd = findViewById(R.id.profile_name_ed)
        emailEd = findViewById(R.id.profile_email_ed)
        phoneEd = findViewById(R.id.profile_phone_ed)
        birthdateTv = findViewById(R.id.profile_birthdate_tv)
        addressLl = findViewById(R.id.profile_address_ll)
        houseNumberTv = findViewById(R.id.profile_houseNb_tv)
        streetTv = findViewById(R.id.profile_street_tv)
        buildingNumberTv = findViewById(R.id.profile_buildingNb_tv)
        zipcodeTv = findViewById(R.id.profile_zipcode_tv)
        cityTv = findViewById(R.id.profile_city_tv)
        siteEd = findViewById(R.id.profile_site_ed)
        editBtn = findViewById(R.id.profile_edit_btn)
    }

    private fun setViewElementsValue(userInfo: JSONObject){
        usernameTv?.text = userInfo.getString("username")
        firstnameEd?.setText(userInfo.getString("firstName"))
        nameEd?.setText(userInfo.getString("name"))
        emailEd?.setText(userInfo.getString("email"))
        phoneEd?.setText(userInfo.getString("phone"))
        birthdateTv?.text = userInfo.getString("birthdate")
        houseNumberTv?.text = userInfo.getString("houseNumber")
        streetTv?.text = userInfo.getString("street")
        val building = getString(R.string.Building) + " " + userInfo.getString("buildingNumber")
        buildingNumberTv?.text = building
        zipcodeTv?.text = userInfo.getInt("zipcode").toString()
        cityTv?.text = userInfo.getString("city")
        siteEd?.setText(userInfo.getJSONObject("site").getString("name"))
    }

    private fun changeEditTextsStatus(status: Boolean){
        firstnameEd?.isEnabled = status
        nameEd?.isEnabled = status
        emailEd?.isEnabled = status
        phoneEd?.isEnabled = status
        birthdateTv?.isEnabled = status
        siteEd?.isEnabled = status
    }

    private fun switchEditMode(){
        // Switch edit mode
        this.editMode = !this.editMode

        // Enable editTexts if edit mode enable, disable them otherwise
        this.changeEditTextsStatus(this.editMode)

        // And we change text according to editMode state
        editBtn?.text = if(this.editMode) getString(R.string.Save) else getString(R.string.Edit)
    }

    private fun openCalendarModal(){
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                birthdateTv?.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}