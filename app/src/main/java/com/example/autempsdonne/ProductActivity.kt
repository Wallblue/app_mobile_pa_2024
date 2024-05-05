package com.example.autempsdonne

import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request.Method
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ProductActivity : AppCompatActivity() {

    private lateinit var queue : RequestQueue
    private var token : String? = null
    private var authLevel : AuthLevels? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product)
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

        if( this.authLevel != AuthLevels.VOLUNTEER && this.authLevel != AuthLevels.ADMIN ){
            Toast.makeText(applicationContext, R.string.ForbiddenErr, Toast.LENGTH_LONG).show()
            finish()
        }

        // val tv = findViewById<TextView>(R.id.product_id_tv)
        // tv.text =

        this.queue = Volley.newRequestQueue(this)
        val req = AuthStringRequest(
            Method.GET,
            intent.getStringExtra(QRScanActivity.EXTRA_QR)!!,
            this.token!!,
            {
                val response = JSONObject(it)

                findViewById<TextView>(R.id.product_id_tv).text = response.getLong("id").toString()
                findViewById<TextView>(R.id.product_name_tv).text = response.getString("name")
                findViewById<TextView>(R.id.product_type_tv).text = response.getString("type")
                findViewById<TextView>(R.id.product_quantity_tv).text = response.getInt("quantity").toString()
                findViewById<TextView>(R.id.product_expiry_tv).text = response.getString("expiry")
                findViewById<TextView>(R.id.product_gathering_tv).text = response.getLong("gatheringId").toString()
                findViewById<TextView>(R.id.product_size_tv).text = response.getInt("unitSize").toString()

                findViewById<FrameLayout>(R.id.main).visibility = View.VISIBLE
            },
            {
                if(it.message != null)
                    Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(applicationContext, R.string.FailedReqErr, Toast.LENGTH_LONG).show()

                finish()
            }
        )
        this.queue.add(req)
    }
}