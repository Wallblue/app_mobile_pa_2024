package com.example.autempsdonne

enum class AuthLevels (val value : Int) {
    VOLUNTEER(1),
    BENEFICIARY(2),
    PARTNER(3),
    ADMIN(4),
    CHILD(5);

    companion object {
        fun findFromValue(value: Int): AuthLevels? {
            return entries.find { it.value == value }
        }
    }
}