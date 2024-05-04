package com.example.autempsdonne

import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject

class AuthJsonObjectRequest (
    method: Int,
    url: String,
    body: JSONObject?,
    private val token: String,
    listener: Response.Listener<JSONObject>,
    errorListener: Response.ErrorListener?
) : JsonObjectRequest(method, url, body, listener, errorListener) {
    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Authorization"] = token
        return headers
    }
}