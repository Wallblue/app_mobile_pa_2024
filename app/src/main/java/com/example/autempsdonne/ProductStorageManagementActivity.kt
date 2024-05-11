package com.example.autempsdonne

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject

class ProductStorageManagementActivity : AppCompatActivity() {
    private lateinit var idTv : TextView
    private lateinit var nameTv : TextView
    private lateinit var typeTv : TextView
    private lateinit var quantityTv : TextView
    private lateinit var storageNp : NumberPicker
    private lateinit var quantityNp : NumberPicker
    private lateinit var throwOutStc : SwitchCompat
    private lateinit var mainBtn : Button

    private lateinit var queue : RequestQueue
    private var token : String? = null
    private var authLevel : AuthLevels? = null

    private lateinit var gotProduct : Product
    private val storagesHm = hashMapOf<String, Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_storage_management)
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
                    response.getInt("storedQuantity"),
                    response.getInt("notStoredQuantity"),
                    url
                )

                if(gotProduct.quantity == 0){
                    Toast.makeText(applicationContext, R.string.ProductNotStored, Toast.LENGTH_LONG).show()
                    finish()
                    return@AuthStringRequest
                }

                val imageView = findViewById<ImageView>(R.id.product_img)
                Glide.with(this)
                    .load(gotProduct.image)
                    .into(imageView)

                this.idTv = findViewById(R.id.product_id_tv)
                this.nameTv = findViewById(R.id.product_name_tv)
                this.typeTv = findViewById(R.id.product_type_tv)
                this.quantityTv = findViewById(R.id.product_quantity_tv)
                this.throwOutStc = findViewById(R.id.throwOutStc)
                this.mainBtn = findViewById(R.id.storage_main_btn)

                this.idTv.text = gotProduct.id.toString()
                this.nameTv.text = gotProduct.name
                this.typeTv.text = gotProduct.type
                this.quantityTv.text = gotProduct.quantity.toString()

                // Setting the storage picker
                this.storageNp = findViewById(R.id.product_impactedStorage_np)

                for (i in 0 until (gotProduct.storages?.length() ?: 0)) {
                    val current = gotProduct.storages?.getJSONObject(i)
                    this.storagesHm[current!!.getString("reference")] = current.getInt("quantity")
                }

                val refList = this.storagesHm.keys.toMutableList()
                if(gotProduct.notStoredQuantity > 0){
                    refList.add(0, getString(R.string.NotStoredItems))
                    storagesHm[getString(R.string.NotStoredItems)] = gotProduct.notStoredQuantity
                    mainBtn.text = getString(R.string.RemoveQuantity)
                    throwOutStc.visibility = View.GONE
                }

                this.storageNp.minValue = 0
                this.storageNp.maxValue = refList.size - 1
                this.storageNp.displayedValues = refList.toTypedArray()
                this.storageNp.wrapSelectorWheel = false

                this.quantityNp = findViewById(R.id.product_quantity_np)
                this.quantityNp.minValue = 1
                this.quantityNp.maxValue = this.storagesHm[refList[0]]!!
                this.quantityNp.wrapSelectorWheel = false

                this.storageNp.setOnValueChangedListener { _, _, newVal ->
                    updateQuantityPicker()

                    if(this.storageNp.displayedValues[newVal] == getString(R.string.NotStoredItems)){
                        throwOutStc.visibility = View.GONE
                        mainBtn.text = getString(R.string.RemoveQuantity)
                    }
                    else{
                        throwOutStc.visibility = View.VISIBLE
                        mainBtn.text = getString(R.string.Destock)
                    }
                }


                // Setting the buttons

                mainBtn.setOnClickListener {
                    if(this.storageNp.displayedValues[storageNp.value] == getString(R.string.NotStoredItems))
                        onRemove()
                    else
                        onDestock()
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

    private fun onDestock(){

        val body = JSONObject().apply {
            put("productId", gotProduct.id)
            put("quantity", quantityNp.value)
            put("throwOut", throwOutStc.isChecked)
        }

        val refList = storageNp.displayedValues
        val ref = Utils.parseStorageReference(refList[storageNp.value])
        val url = API_URL_ROOT + "addresses/warehouses/" + ref[0] + "/sections/" + ref[1] + "/shelves/" + ref[2] + "/storages/" + ref[3] + "/destock"

        val req = AuthJsonObjectRequest(
            Request.Method.PATCH,
            url,
            body,
            this.token!!,
            {
                Toast.makeText(applicationContext, R.string.DestockSuccess, Toast.LENGTH_SHORT).show()

                if(throwOutStc.isChecked){
                    this.gotProduct.quantity -= quantityNp.value
                    this.quantityTv.text = this.gotProduct.quantity.toString()
                }
                else updateNotStoredProducts(this.quantityNp.value)

                updateNumberPickers()
            },
            {
                val res = JSONObject(String(it.networkResponse.data, Charsets.UTF_8))
                Toast.makeText(applicationContext, res.getString("message"), Toast.LENGTH_SHORT).show()
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

        val body = gotProduct.parseJson()
        body.remove("storages")

        val req = AuthJsonObjectRequest(
            Request.Method.PATCH,
            this.gotProduct.url,
            body,
            this.token!!,
            {
                Toast.makeText(applicationContext, R.string.RemoveQuantitySuccess, Toast.LENGTH_SHORT).show()

                this.quantityTv.text = this.gotProduct.quantity.toString()

                updateNumberPickers()
            },
            {
                val res = JSONObject(String(it.networkResponse.data, Charsets.UTF_8))
                Toast.makeText(applicationContext, res.getString("message"), Toast.LENGTH_SHORT).show()
                gotProduct.quantity += quantityNp.value
                println(it)
            }
        )
        this.queue.add(req)
    }

    private fun updateNumberPickers(){
        val newQuantity = this.quantityNp.maxValue - this.quantityNp.value

        val key = storageNp.displayedValues[storageNp.value]
        this.storagesHm[key] = newQuantity

        if(newQuantity == 0){
            if(storageNp.maxValue == 0){
                Toast.makeText(applicationContext, R.string.ProductNoMoreStored, Toast.LENGTH_LONG).show()
                finish()
                return
            }
            removeCurrentStorageRef()
            updateQuantityPicker()
        } else
            quantityNp.maxValue = newQuantity
    }

    private fun removeCurrentStorageRef(){
        val refList = storageNp.displayedValues.toMutableList()
        val i = storageNp.value
        refList.removeAt(i)
        storageNp.maxValue--
        storageNp.displayedValues = refList.toTypedArray()
    }
    private fun updateQuantityPicker(){
        this.quantityNp.maxValue = storagesHm[this.storageNp.displayedValues[storageNp.value]]!!
    }

    private fun updateNotStoredProducts(destockQuantity : Int = 0){
        if(this.storageNp.displayedValues.contains(getString(R.string.NotStoredItems))){
            storagesHm[getString(R.string.NotStoredItems)] = storagesHm[getString(R.string.NotStoredItems)]!! + destockQuantity
        }
        else{
            val refList = this.storagesHm.keys.toMutableList()
            if(destockQuantity > 0){
                refList.add(0, getString(R.string.NotStoredItems))
                this.storagesHm[getString(R.string.NotStoredItems)] = destockQuantity
                this.storageNp.displayedValues = refList.toTypedArray()
                this.storageNp.maxValue++
                this.storageNp.value++ // We adapt the current value bc we've inserted a new line in the picker
            }
        }

    }
}