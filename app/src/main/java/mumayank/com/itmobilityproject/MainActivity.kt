package mumayank.com.itmobilityproject

import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.SearchView
import kotlinx.android.synthetic.main.main_activity.*
import mumayank.com.airlocationlibrary.AirLocation
import java.util.*
import kotlin.concurrent.schedule


//using AirLocation library: https://github.com/mumayank/AirLocation

class MainActivity : AppCompatActivity() {

    //VARIABLES:
    var lat = 0.0
    var long = 0.0
    val geocoder = Geocoder(this)

    private val airLocation = AirLocation(this, object : AirLocation.Callback {
        override fun onSuccess(locations: ArrayList<Location>) {
            //progressBar.visibility = View.GONE

            lat = locations.last().latitude.toDouble()
            long = locations.last().longitude.toDouble()
            var string = geocoder.getFromLocation(lat, long, 1)[0].locality
            //textView2.text = string
            println(string)
        }

        override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
            //progressBar.visibility = View.GONE
            Toast.makeText(this@MainActivity, locationFailedEnum.name, Toast.LENGTH_SHORT)
                .show()
        }
    }/*, isLocationRequiredOnlyOneTime = true*/)


    //FUNCTIONS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        //progressBar.visibility = View.VISIBLE
        airLocation.start()

        val intent = Intent(this, StartActivity::class.java)
        Timer("nextAct", false).schedule(3000){
            startActivity(intent)
        }

        /*
        button3.setOnClickListener {
            val intent = Intent(this, StartActivity::class.java)
            startActivity(intent)
        }
        */

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

    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.my_menu, menu)
        val search = menu.findItem(R.id.search)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Your city..."

        return true
    }
    */

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when (item.itemId){
            R.id.location_search -> {
                progressBar.visibility = View.VISIBLE
                airLocation.start()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    */

}