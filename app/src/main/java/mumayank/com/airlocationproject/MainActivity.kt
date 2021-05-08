package mumayank.com.airlocationproject

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*

//using AirLocation library: https://github.com/mumayank/AirLocation

class MainActivity : AppCompatActivity() {

    private val airLocation = AirLocation(this, object : AirLocation.Callback {
        override fun onSuccess(locations: ArrayList<Location>) {
            progressBar.visibility = View.GONE
            var string = "\n"
            for (location in locations) {
                string = "${location.longitude}, ${location.latitude}\n$string"
            }
            string = "$string${textView2.text}"
            textView2.text = string
        }

        override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
            progressBar.visibility = View.GONE
            Toast.makeText(this@MainActivity, locationFailedEnum.name, Toast.LENGTH_SHORT)
                .show()
        }
    }, isLocationRequiredOnlyOneTime = true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        progressBar.visibility = View.GONE

        button2.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            airLocation.start()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}