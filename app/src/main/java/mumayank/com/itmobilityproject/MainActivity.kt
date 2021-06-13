package mumayank.com.itmobilityproject

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.main_activity.*
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*
import kotlin.concurrent.schedule


//using AirLocation library: https://github.com/mumayank/AirLocation

class MainActivity : AppCompatActivity() {

    //VARIABLES:
    var lat = 0.0
    var long = 0.0
    //lateinit var location: Location
    val geocoder = Geocoder(this)
    var cityName = "NaN"

    private val airLocation = AirLocation(this, object : AirLocation.Callback {
        override fun onSuccess(locations: ArrayList<Location>) {
            //progressBar.visibility = View.GONE

            lat = locations.last().latitude.toDouble()
            long = locations.last().longitude.toDouble()
            nextActivity()
        }

        override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
            Toast.makeText(this@MainActivity, locationFailedEnum.name, Toast.LENGTH_SHORT)
                .show()
            nextActivity()
        }
    }/*, isLocationRequiredOnlyOneTime = true*/)


    //FUNCTIONS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //In the first Activity because can be set one time
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        airLocation.start()

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

    private fun nextActivity(){

        if (!lat.equals(0.0) && !long.equals(0.0)){
            cityName = geocoder.getFromLocation(lat, long, 1)[0].locality
        }

        Timer("nextAct", false).schedule(2000){
            if(cityName != "NaN"){
                val intent = Intent(this@MainActivity, StartActivity::class.java)
                intent.putExtra("City", cityName)
                intent.putExtra("Lat", lat)
                intent.putExtra("Lon", long)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(0, 0)
            } else {
                val intent = Intent(this@MainActivity, SelectCityActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
    }

}