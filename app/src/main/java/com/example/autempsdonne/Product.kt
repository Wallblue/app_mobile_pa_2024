package com.example.autempsdonne

import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class Product (
    var id : Long,
    var name : String,
    var expiry : String,
    var quantity : Int,
    var gatheringId : Long,
    var type : String,
    var unitSize : Int,
    var image : String,
    var qrCodePath : String,
    var storages : JSONArray?,
    var storedQuantity : Int,
    var notStoredQuantity : Int,
    val url : String
) : Serializable {
    fun parseJson() : JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("name", name)
            put("expiry", expiry)
            put("quantity", quantity)
            put("gatheringId", gatheringId)
            put("type", type)
            put("unitSize", unitSize)
            put("storages", storages)
        }
    }
}