package mumayank.com.itmobilityproject

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start.*

/**
 * Start activity
 *
 * Activity to choose between scan Qr-Code or show last search
 */
class StartActivity : AppCompatActivity() {

    /**
     * City Name of the city
     */
    var city = "NaN"

    /**
     * Lat Latitude
     */
    var lat = 0.0

    /**
     * Lon Longitude
     */
    var lon = 0.0

    /**
     * On create
     *
     * Get extras and go to the next activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        /**
         * Layout title
         */
        title = "LIDL APP 2.0"

        /**
         * get extras from previous activity
         */
        city = intent.getStringExtra("City").toString()
        lat = intent.getDoubleExtra("Lat", 0.0)
        lon = intent.getDoubleExtra("Lon", 0.0)


        /**
         * Feedback for user
         */
        cityName.text = city

        /**
         * Connectivity
         * Check if internet connection is there
         * if not: disable QR-Button
         */
        val connectivity =
            this.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo
        btnQR.isEnabled = networkInfo != null && networkInfo.isConnected == true
        if (!btnQR.isEnabled) {
            btnQR.background = resources.getDrawable(R.drawable.button_shape_enabled)
        }

        /**
         * Go to QR-Scan if Button clicked
         */
        btnQR.setOnClickListener {
            val intent = Intent(this, QrScanActivity::class.java)
            intent.putExtra("City", city)
            intent.putExtra("Lat", lat)
            intent.putExtra("Lon", lon)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        /**
         * Go to results if button clicked
         * "NaN" is the signal for result activity, that last result has to be shown
         */
        btnPrevSearch.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("City", city)
            intent.putExtra("Product", "NaN")
            intent.putExtra("Lat", lat)
            intent.putExtra("Lon", lon)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        return
    }

}