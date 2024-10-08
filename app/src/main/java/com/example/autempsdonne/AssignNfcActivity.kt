package com.example.autempsdonne

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AssignNfcActivity : AppCompatActivity() {
    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var contentToWrite: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_assign_nfc)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.contentToWrite = API_URL_ROOT + "volunteer/" + intent.getLongExtra("volunteerId", 0) + "/slim"

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        this.nfcAdapter?.let {
            NfcManagement.createPendingIntent(this, javaClass)
            println(javaClass)
            this.pendingIntent = PendingIntent.getActivity(this, 0,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE)
        }
    }

    override fun onResume() {
        super.onResume()
        NfcManagement.enable(nfcAdapter, this, pendingIntent)
    }

    override fun onPause() {
        super.onPause()
        NfcManagement.disable(nfcAdapter, this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            // We get the scanned tag
            val tagFromIntent: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)

            NfcManagement.writeUriInTag(applicationContext, tagFromIntent, contentToWrite)
        }
    }
}