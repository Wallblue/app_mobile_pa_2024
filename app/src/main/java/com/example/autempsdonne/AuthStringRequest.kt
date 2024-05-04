package com.example.autempsdonne

import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class AuthStringRequest (
        method: Int,
        url: String,
        private val token: String,
        listener: Response.Listener<String>,
        errorListener: Response.ErrorListener?
    ) : StringRequest(method, url, listener, errorListener) {
    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        headers["Authorization"] = token
        return headers
    }
}