package mumayank.com.itmobilityproject

import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    var city = "NaN"
    var lat = 0.0
    var lon = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        title="LIDL APP 2.0"

        city = intent.getStringExtra("City").toString()
        lat = intent.getDoubleExtra("Lat", 0.0)
        lon = intent.getDoubleExtra("Lon", 0.0)


        cityName.text = city

        val connectivity = this.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivity.activeNetworkInfo
        btnQR.isEnabled = networkInfo != null && networkInfo.isConnected == true
        if(!btnQR.isEnabled){
            btnQR.background = resources.getDrawable(R.drawable.button_shape_enabled)
        }

        btnQR.setOnClickListener {
            val intent = Intent(this, QrScanActivity::class.java)
            intent.putExtra("City", city)
            intent.putExtra("Product", "Mikrowelle")
            intent.putExtra("Lat", lat)
            intent.putExtra("Lon", lon)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

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