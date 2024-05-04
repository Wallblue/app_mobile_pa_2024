package com.example.autempsdonne

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class EditAddressDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog{
        val view = requireActivity().layoutInflater.inflate(R.layout.edit_address_dialog, null)

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle(R.string.EditAddress)
            .setPositiveButton(getString(R.string.OK)) { _,_ -> }
            .setNegativeButton(R.string.Cancel, null)
            .create()
    }

    companion object {
        const val TAG = "EditAddressDialog"
    }
}