package com.example.autempsdonne

import java.io.Serializable

class Volunteer(
    val id: Long,
    val userId: Long,
    val name: String?,
    val firstName: String?,
    val username: String?,
    val email: String?,
    val phone: String?,
    val license: Int,
    val authLevel: Int,
    val siteId: Long,
    val url: String?
) : Serializable {

    public fun isOkay () : Boolean {
        return (
                id > 0 && userId > 0 && !name.isNullOrEmpty()
                && !email.isNullOrEmpty() && !firstName.isNullOrEmpty()
                && !username.isNullOrEmpty() && siteId > 0 && !url.isNullOrEmpty()
                )
    }
}