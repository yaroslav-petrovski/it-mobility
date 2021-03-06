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

/**
 * Main activity
 *
 * Activity to show the logo and determine position
 */
class MainActivity : AppCompatActivity() {

    /**
     * Lat
     * Latitude
     */
    var lat = 0.0

    /**
     * Long
     * Longitude
     */
    var long = 0.0

    /**
     * Geocoder
     * Geocoder to determine city name
     */
    val geocoder = Geocoder(this)

    /**
     * City name
     * The name of the city of user
     */
    var cityName = "NaN"

    /**
     * Air location
     * The object from Airlocation library to determine location
     */
    private val airLocation = AirLocation(this, object : AirLocation.Callback {
        /**
         * On success
         * if location determination was successful go to the next activity
         * @param locations the list of locations
         */
        override fun onSuccess(locations: ArrayList<Location>) {
            lat = locations.last().latitude.toDouble()
            long = locations.last().longitude.toDouble()
            nextActivity()
        }

        /**
         * On failure
         * if no location determined go to next activity
         * @param locationFailedEnum error text
         */
        override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
            Toast.makeText(this@MainActivity, locationFailedEnum.name, Toast.LENGTH_SHORT)
                .show()
            nextActivity()
        }
    }/*, isLocationRequiredOnlyOneTime = true*/)


    /**
     * On create
     * If activity created: hide action bar, enable database persistence, determine location
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        this.supportActionBar?.hide()

        //In the first Activity because can be set one time only
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        airLocation.start()

    }

    /**
     * On activity result
     * Necessary for Airlocaton Library
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * On request permissions result
     * Ask user to grant permissions
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Next activity
     * If location determined go to Start Activity (with city and positions as extra),
     * if not go to Select City Activity
     *
     * The flags are used to clear history and not return to this activity
     */
    private fun nextActivity() {

        if (!lat.equals(0.0) && !long.equals(0.0)) {
            cityName = geocoder.getFromLocation(lat, long, 1)[0].locality
        }

        Timer("nextAct", false).schedule(2000) {
            if (cityName != "NaN") {
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