package com.example.autempsdonne

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.webkit.URLUtil
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.util.isNotEmpty
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector

class QRScanActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_QR = "productLink"
        const val EXTRA_MODE = "scanMode"

        private const val CAMERA_REQUEST_CODE = 1
    }

    private lateinit var scanSv: SurfaceView
    private lateinit var qrDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource

    private var badQrCode : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrscan)

        scanSv = findViewById(R.id.scan_sv)
    }

    override fun onResume() {
        super.onResume()
        initDetector()
    }

    override fun onPause() {
        super.onPause()
        qrDetector.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(badQrCode)
            Toast.makeText(applicationContext, R.string.InvalidQrErr, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(cameraPermissionGranted(requestCode, grantResults)){
            finish()
            startActivity(intent)
        } else {
            Toast.makeText(this, R.string.NeedCameraErr, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun cameraPermissionGranted(requestCode: Int, grantResults: IntArray): Boolean {
        return requestCode == CAMERA_REQUEST_CODE
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }

    private fun initDetector() {
        qrDetector = BarcodeDetector.Builder(this)
            .setBarcodeFormats(Barcode.QR_CODE)
            .build()

        initCameraSource()
        initScanSurfaceView()

        qrDetector.setProcessor(object: Detector.Processor<Barcode> {
            override fun release() { }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val qrCodes = detections.detectedItems

                if (qrCodes.isNotEmpty()) {
                    val qrCode = qrCodes.valueAt(0)
                    if (qrCode.displayValue.isNotEmpty())
                        onQrScan(qrCode.displayValue)
                }
            }

        })
    }

    private fun onQrScan(displayValue: String) {
        if (isProductLink(displayValue)) {
            val i = Intent(this, getNextActivityClass())
            i.putExtra(EXTRA_QR, displayValue)
            startActivity(i)
        } else {
            this.badQrCode = true
            finish()
        }
    }

    private fun getNextActivityClass(): Class<*>? {
        val mode = intent.getSerializableExtra(EXTRA_MODE, QrScanModes::class.java)
        return when (mode) {
            QrScanModes.SHOW_MODE -> ProductActivity::class.java
            QrScanModes.STOCK_MODE -> ProductStorageManagementActivity::class.java
            else -> null
        }
    }

    private fun isProductLink(value: String): Boolean {
        val regex = Regex(API_URL_ROOT + "addresses/warehouses/products/[1-9]\\d*")

        return URLUtil.isValidUrl(value) && value.matches(regex)
    }

    private fun initCameraSource() {
        cameraSource = CameraSource.Builder(this, qrDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()
    }

    private fun initScanSurfaceView() {
        scanSv.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if ( PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this@QRScanActivity, Manifest.permission.CAMERA ) )
                    cameraSource.start(scanSv.holder)
                else
                    ActivityCompat.requestPermissions(
                        this@QRScanActivity,
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_REQUEST_CODE
                    )
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) { }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.release()
            }

        })
    }
}