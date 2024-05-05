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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class UserProfileActivity : AppCompatActivity() , AddressUpdateListener {
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
    private var buttonsV : View? = null
    private var cancelBtn : Button? = null

    private var editMode : Boolean = false

    private var token : String? = ""
    private var authLevel : AuthLevels? = null
    private var queue : RequestQueue? = null
    private var url : String = ""
    private var userInfo : JSONObject? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.token = getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString("token", "")

        if(this.token == null){
            Toast.makeText(applicationContext, R.string.NullTokenErr, Toast.LENGTH_LONG).show()
            finish()
        }
        this.authLevel = Utils.getAuthLevel(this.token!!)

        if( this.authLevel == null || this.authLevel == AuthLevels.CHILD || this.authLevel == AuthLevels.PARTNER ){
            Toast.makeText(applicationContext, R.string.ForbiddenErr, Toast.LENGTH_LONG).show()
            finish()
        }

        this.getViewElements()

        this.url = API_URL_ROOT + (if (this.authLevel == AuthLevels.BENEFICIARY) "beneficiary" else "volunteer") + "/profile/me"

        this.queue = Volley.newRequestQueue(this)
        val req = AuthStringRequest(
            Method.GET,
            this.url,
            this.token!!,
            {
                this.userInfo = JSONObject(it) //if (this.authLevel != AuthLevels.BENEFICIARY) JSONObject(it).getJSONObject("user") else JSONObject(it)

                resetLayoutValues()

                // When we have set all the data we set the total layout to visible
                findViewById<LinearLayout>(R.id.main).visibility = View.VISIBLE

                // We set the edit mode button listener
                editBtn?.setOnClickListener {
                    this.switchEditMode()
                }

                // We set the cancel button listener
                cancelBtn?.setOnClickListener {
                    AlertDialog.Builder(this)
                        .setMessage(R.string.CancelAsk)
                        .setPositiveButton(R.string.Yes) { _, _ ->
                            resetLayoutValues()
                            switchEditMode(true)
                        }
                        .setNegativeButton(R.string.No) { dialog, _ -> dialog.dismiss() }
                        .show()
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
        )

        this.queue?.add(req)
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
        buttonsV = findViewById(R.id.profile_buttons_v)
        cancelBtn = findViewById(R.id.profile_cancel_btn)
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
        buildingNumberTv?.text = userInfo.getString("buildingNumber")
        zipcodeTv?.text = userInfo.getInt("zipcode").toString()
        cityTv?.text = userInfo.getString("city")
        siteEd?.setText(userInfo.getJSONObject("site").getString("name"))
    }

    private fun changeEditTextsStatus(status: Boolean){
        firstnameEd?.isEnabled = status
        nameEd?.isEnabled = status
        birthdateTv?.isEnabled = status
        //emailEd?.isEnabled = status
        //phoneEd?.isEnabled = status
        //siteEd?.isEnabled = status
    }

    private fun switchEditMode(isCancellation : Boolean = false){
        // Switch edit mode
        this.editMode = !this.editMode

        // Enable editTexts if edit mode enable, disable them otherwise
        this.changeEditTextsStatus(this.editMode)

        // And we change text according to editMode state
        if (this.editMode){
            editBtn?.text = getString(R.string.Save)

            switchCancelButtonState()
        }
        else {
            if(!isCancellation) this.sendSaveRequest()
            editBtn?.text = getString(R.string.Edit)

            switchCancelButtonState()
        }
    }

    private fun sendSaveRequest() {
        val body = makeSaveRequestBody()
        val saveReq = AuthJsonObjectRequest(
            Method.PATCH,
            this.url,
            body,
            this.token!!,
            {
                Toast.makeText(applicationContext, R.string.SaveSuccess, Toast.LENGTH_LONG).show()

                if (this.authLevel == AuthLevels.BENEFICIARY)
                    this.updateUserInfo(userInfo!!)
                else
                    this.updateUserInfo(userInfo!!.getJSONObject("user"))
            },
            {
                if(it.message != null)
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(applicationContext, R.string.SaveErr, Toast.LENGTH_LONG).show()

                println(String(it.networkResponse.data))

                // If there's an error we reset the values
                resetLayoutValues()
            }
        )
        queue?.add(saveReq)
    }

    private fun makeSaveRequestBody() : JSONObject{
        return JSONObject().apply {
            // Things we change
            put("name", nameEd?.text.toString())
            put("firstName", firstnameEd?.text.toString())
            put("birthdate", birthdateTv?.text.toString())
            put("email", emailEd?.text.toString())
            put("phone", phoneEd?.text.toString())
            put("street", streetTv?.text.toString())
            put("houseNumber", houseNumberTv?.text.toString())
            put("city", cityTv?.text.toString())
            put("zipcode", zipcodeTv?.text.toString().toInt())
            put("buildingNumber", buildingNumberTv?.text.toString())

            // Things we don't change

            if(authLevel == AuthLevels.VOLUNTEER || authLevel == AuthLevels.ADMIN){
                put("siteId", userInfo?.getJSONObject("user")?.getJSONObject("site")?.getLong("id"))
                put("license", userInfo?.getJSONObject("volunteer")?.getInt("license"))
                put("licenseTruck", userInfo?.getJSONObject("volunteer")?.getInt("licenseTruck"))
                put("disponibilityAmount", userInfo?.getJSONObject("volunteer")?.getInt("disponibilityAmount"))
                put("disponibilityType", userInfo?.getJSONObject("volunteer")?.getInt("disponibilityType"))

                val availabilitiesArray = userInfo?.getJSONArray("disponibilities")
                val availabilities = IntArray(availabilitiesArray!!.length())

                for(i in 0 until availabilitiesArray.length())
                    availabilities[i] = availabilitiesArray
                        .getJSONArray(i)
                        .getJSONObject(0)
                        .getInt("disponibility")
                put("disponibilities", JSONArray(availabilities))
            } else
                put("siteId", userInfo?.getJSONObject("site")?.getLong("id"))

        }
    }

    private fun updateUserInfo(info : JSONObject){
        info.apply {
            put("name", nameEd?.text.toString())
            put("firstName", firstnameEd?.text.toString())
            put("birthdate", birthdateTv?.text.toString())
            put("email", emailEd?.text.toString())
            put("phone", phoneEd?.text.toString())
            put("street", streetTv?.text.toString())
            put("houseNumber", houseNumberTv?.text.toString())
            put("city", cityTv?.text.toString())
            put("zipcode", zipcodeTv?.text.toString().toInt())
            put("buildingNumber", buildingNumberTv?.text.toString())
        }
    }

    private fun resetLayoutValues(){
        if (this.authLevel == AuthLevels.BENEFICIARY)
            this.setViewElementsValue(userInfo!!)
        else
            this.setViewElementsValue(userInfo!!.getJSONObject("user"))
    }

    private fun openCalendarModal(){
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, dayOfMonth)
                birthdateTv?.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun switchCancelButtonState(){
        val temp = this.buttonsV?.visibility
        this.buttonsV?.visibility = this.cancelBtn?.visibility!!
        this.cancelBtn?.visibility = temp!!
    }

    override fun onAddressUpdate(address: Address) {
        houseNumberTv?.text = address.houseNumber
        streetTv?.text = address.street
        buildingNumberTv?.text = address.buildingNumber
        zipcodeTv?.text = address.zipcode.toString()
        cityTv?.text = address.city
    }

    override fun defineBaseAddress(): Address {
        return Address(
            houseNumberTv?.text.toString(),
            streetTv?.text.toString(),
            buildingNumberTv?.text.toString(),
            zipcodeTv?.text.toString().toInt(),
            cityTv?.text.toString()
        )
    }
}