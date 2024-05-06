package com.example.autempsdonne

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.widget.Toast

class NfcManagement {

    companion object{
        fun writeUriInTag(context: Context, tag: Tag?, url: String?) {
            val ndefMsg = NdefMessage(NdefRecord.createUri(url))

            val ndef = Ndef.get(tag)
            ndef.connect()

            if(!ndef.isWritable){
                Toast.makeText(context, R.string.NoWritingErr, Toast.LENGTH_LONG).show()
                return
            }

            // Is there enough space in tag
            if(ndef.maxSize < ndefMsg.toByteArray().size){
                Toast.makeText(context, R.string.NoSpaceTagErr, Toast.LENGTH_LONG).show()
                return
            }

            ndef.writeNdefMessage(ndefMsg)
            ndef.close()

            Toast.makeText(context, R.string.Written, Toast.LENGTH_LONG).show()
        }

        fun readTag(intent: Intent) : String {
            val ndefMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val messages: List<NdefMessage>? = ndefMessages?.map { it as NdefMessage }
            var res = ""
            if (messages != null)
                for (message in messages)
                    for(record in message.records)
                        res += String(record.payload, Charsets.UTF_8) + " "
            return res
        }

        fun disable(nfcAdapter: NfcAdapter?, activity: Activity) {
            nfcAdapter?.disableForegroundDispatch(activity)
        }

        fun enable(nfcAdapter: NfcAdapter?, activity: Activity, pendingIntent: PendingIntent?) {
            nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, null, null)
        }

        fun createPendingIntent(context: Context, classname : Class<Any>): PendingIntent? {
            return PendingIntent.getActivity(
                context,
                0,
                Intent(context, classname).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE
            )
        }
    }
}