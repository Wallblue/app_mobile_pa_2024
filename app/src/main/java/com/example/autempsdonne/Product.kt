package com.example.autempsdonne

import org.json.JSONArray
import java.io.Serializable

data class Product (
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
    val url : String
) : Serializable