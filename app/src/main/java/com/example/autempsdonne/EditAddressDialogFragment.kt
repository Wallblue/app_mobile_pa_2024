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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context !is AddressUpdateListener)
            throw RuntimeException("$context must implement AddressUpdateListener")

        this.addressUpdateListener = context
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{
        val view : View = requireActivity().layoutInflater.inflate(R.layout.edit_address_dialog, null)

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(R.string.EditAddress)
            .setPositiveButton(getString(R.string.OK)) { _,_ ->
                this.addressUpdateListener?.onAddressUpdate(getAddressFromDialog(view))
            }
            .setNegativeButton(R.string.Cancel, null)
            .create()
    }

    private fun getAddressFromDialog(view : View): Address {
        return Address(
            view.findViewById<EditText>(R.id.dialog_houseNb_ed).text.toString(),
            view.findViewById<EditText>(R.id.dialog_street_ed).text.toString(),
            view.findViewById<EditText>(R.id.dialog_buildingNb_ed).text.toString(),
            view.findViewById<EditText>(R.id.dialog_zipcode_ed).text.toString().toInt(),
            view.findViewById<EditText>(R.id.dialog_city_ed).text.toString()
        )
    }

    companion object {
        const val TAG = "EditAddressDialog"
    }
}