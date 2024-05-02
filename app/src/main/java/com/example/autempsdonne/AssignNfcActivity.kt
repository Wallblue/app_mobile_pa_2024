package com.example.autempsdonne

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AssignNfcActivity : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var currentVolunteer: Volunteer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_assign_nfc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /*this.currentVolunteer = Volunteer(
            intent.getLongExtra("id", 0),
            intent.getLongExtra("userId", 0),
            intent.getStringExtra("name"),
            intent.getStringExtra("firstName"),
            intent.getStringExtra("username"),
            intent.getStringExtra("email"),
            intent.getStringExtra("phone"),
            intent.getIntExtra("license", 0),
            intent.getIntExtra("authLevel", 0),
            intent.getLongExtra("siteId", 0),
            intent.getStringExtra("url")
        )

        if(! currentVolunteer!!.isOkay()){
            Toast.makeText(applicationContext, R.string.Err, Toast.LENGTH_SHORT).show()
            finish()
        }*/

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        this.nfcAdapter?.let {
            println("coucou")
            this.pendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_IMMUTABLE)
        }
    }

    override fun onResume() {
        super.onResume()
        enable()
    }

    private fun enable() {
        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val tech = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val tag = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val filterArray = arrayOf(ndef, tech, tag)

        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filterArray, null)
    }

    override fun onPause() {
        super.onPause()
        disable()
    }

    private fun disable() {
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(i: Intent?) {
        super.onNewIntent(i)
        resolveIntent(i)
    }

    private fun resolveIntent(i: Intent?) {
        val action = i?.action
        println(i.toString())

        if(action == NfcAdapter.ACTION_NDEF_DISCOVERED){
            println("oui")
        } else if (action == NfcAdapter.ACTION_TECH_DISCOVERED){
            println("oui2")
        } else if (action == NfcAdapter.ACTION_TAG_DISCOVERED){
            println("oui3")
        } else if (i?.type != null && i.type == "application/$packageName") {
            println("oui4")
        }else{
            println("non")
        }
    }
}