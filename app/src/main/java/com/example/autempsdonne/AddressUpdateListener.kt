package com.example.autempsdonne

interface AddressUpdateListener {
    fun onAddressUpdate(address: Address)
    fun defineBaseAddress() : Address
}