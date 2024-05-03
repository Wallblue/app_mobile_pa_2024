package com.example.autempsdonne

import org.json.JSONObject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class Utils {
    companion object {
        @OptIn(ExperimentalEncodingApi::class)
        fun decodeTokenInfo(token: String): String {
            val encodedInfo = token.split(".")[1]
            val decodedByte = Base64.decode(encodedInfo)
            return String(decodedByte)
        }

        fun getAuthLevel(token : String): AuthLevels? {
            val value = JSONObject(decodeTokenInfo(token)).getInt("authLevel")
            return AuthLevels.findFromValue(value)
        }
    }
}