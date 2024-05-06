package com.example.autempsdonne

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class EditProductQuantityActivity : AppCompatActivity() {

    private lateinit var idTv : TextView
    private lateinit var nameTv : TextView
    private lateinit var typeTv : TextView
    private lateinit var quantityTv : TextView
    private lateinit var quantityNp : NumberPicker

    private lateinit var queue : RequestQueue
    private var token : String? = null
    private var authLevel : AuthLevels? = null

    private lateinit var gotProduct : Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_product_quantity)
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

        val url = intent.getStringExtra(QRScanActivity.EXTRA_QR)!!

        this.queue = Volley.newRequestQueue(this)
        val req = AuthStringRequest(
            Request.Method.GET,
            url,
            this.token!!,
            {
                val response = JSONObject(it)

                this.gotProduct = Product (
                    response.getLong("id"),
                    response.getString("name"),
                    response.getString("expiry"),
                    response.getInt("quantity"),
                    response.getLong("gatheringId"),
                    response.getString("type"),
                    response.getInt("unitSize"),
                    response.getString("image"),
                    response.getString("qrCodePath"),
                    response.getJSONArray("storages"),
                    url
                )

                val imageView = findViewById<ImageView>(R.id.product_img)
                Glide.with(this)
                    .load(gotProduct.image)
                    .into(imageView)

                this.idTv = findViewById(R.id.product_id_tv)
                this.nameTv = findViewById(R.id.product_name_tv)
                this.typeTv = findViewById(R.id.product_type_tv)
                this.quantityTv = findViewById(R.id.product_quantity_tv)

                this.idTv.text = gotProduct.id.toString()
                this.nameTv.text = gotProduct.name
                this.typeTv.text = gotProduct.type
                this.quantityTv.text = gotProduct.quantity.toString()

                this.quantityNp = findViewById(R.id.product_impactedQuantity_np)
                this.quantityNp.minValue = 0
                this.quantityNp.maxValue = gotProduct.quantity
                this.quantityNp.wrapSelectorWheel = false

                findViewById<Button>(R.id.product_removeQuantity_btn).setOnClickListener {
                    onRemove()
                }

                findViewById<LinearLayout>(R.id.main).visibility = View.VISIBLE
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

    private fun onRemove(){
        if(this.gotProduct.quantity - quantityNp.value < 0){
            Toast.makeText(applicationContext, R.string.NegQuantityErr, Toast.LENGTH_LONG).show()
            return
        }
        gotProduct.quantity -= quantityNp.value

        /*
        * Body needs :
        * name
        * expiry
        * quantity
        * unitSize
        * type
        * image
        * */

        val req = AuthJsonObjectRequest(
            Request.Method.PATCH,
            this.gotProduct.url,
            TODO("Make request body"),
            this.token!!,
            {

            },
            {

            }
        )
        this.queue.add(req)
    }
}