package com.example.autempsdonne

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EditAddressDialogFragment : DialogFragment() {
    private var addressUpdateListener: AddressUpdateListener? = null

    private var houseNbEt : EditText? = null
    private var streetEt: EditText? = null
    private var buildingNbEt: EditText? = null
    private var zipCodeEt: EditText? = null
    private var cityEt: EditText? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context !is AddressUpdateListener)
            throw RuntimeException("$context must implement AddressUpdateListener")

        this.addressUpdateListener = context
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{
        val view : View = requireActivity().layoutInflater.inflate(R.layout.edit_address_dialog, null)
        this.getDialogElements(view)

        this.setAddressInDialog()

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(R.string.EditAddress)
            .setPositiveButton(getString(R.string.OK)) { _,_ ->
                this.addressUpdateListener?.onAddressUpdate(getAddressFromDialog())
            }
            .setNegativeButton(R.string.Cancel, null)
            .create()
    }

    private fun getDialogElements(view : View) {
        this.houseNbEt = view.findViewById(R.id.dialog_houseNb_ed)
        this.streetEt = view.findViewById(R.id.dialog_street_ed)
        this.buildingNbEt = view.findViewById(R.id.dialog_buildingNb_ed)
        this.zipCodeEt = view.findViewById(R.id.dialog_zipcode_ed)
        this.cityEt = view.findViewById(R.id.dialog_city_ed)
    }

    private fun setAddressInDialog() {
        val defaultAddress = this.addressUpdateListener?.defineBaseAddress()
        this.houseNbEt?.setText(defaultAddress?.houseNumber)
        this.streetEt?.setText(defaultAddress?.street)
        this.buildingNbEt?.setText(defaultAddress?.buildingNumber)
        this.zipCodeEt?.setText(defaultAddress?.zipcode.toString())
        this.cityEt?.setText(defaultAddress?.city)
    }

    private fun getAddressFromDialog(): Address {
        return Address(
            houseNbEt?.text.toString(),
            streetEt?.text.toString(),
            buildingNbEt?.text.toString(),
            zipCodeEt?.text.toString().toInt(),
            cityEt?.text.toString()
        )
    }

    companion object {
        const val TAG = "EditAddressDialog"
    }
}