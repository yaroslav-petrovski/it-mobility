package mumayank.com.itmobilityproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.Result
import kotlinx.android.synthetic.main.activity_qr_scan.*
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QrScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    var city = "NaN"
    var product = "NaN"
    var lat = 0.0
    var lon = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_scan)

        city = intent.getStringExtra("City").toString()
        lat = intent.getDoubleExtra("Lat", 0.0)
        lon = intent.getDoubleExtra("Lon", 0.0)

        setScannerProperties()
    }

    private fun setScannerProperties() {
        qrCodeScanner.setFormats(listOf(BarcodeFormat.QR_CODE))
        qrCodeScanner.setAutoFocus(true)
        qrCodeScanner.setLaserColor(R.color.colorAccent)
        qrCodeScanner.setMaskColor(R.color.colorAccent)
        if (Build.MANUFACTURER.equals("huawei", ignoreCase = true))
            qrCodeScanner.setAspectTolerance(0.5f)
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    6515)
                return
            }
        }
        qrCodeScanner.startCamera()
        qrCodeScanner.setResultHandler(this)
    }

    override fun handleResult(p0: Result?) {
        if (p0 != null) {
            product = p0.text
            Toast.makeText(this,product,Toast.LENGTH_LONG).show()
            resumeCamera()
            nextActivity()
        }
    }

    override fun onPause() {
        super.onPause()
        qrCodeScanner.stopCamera()
    }

    fun nextActivity(){
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("City", city)
        intent.putExtra("Product", product)
        intent.putExtra("Lat", lat)
        intent.putExtra("Lon", lon)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun resumeCamera() {
        Toast.LENGTH_LONG
        val handler = Handler()
        handler.postDelayed({ qrCodeScanner.resumeCameraPreview(this@QrScanActivity) }, 2000)
    }
}