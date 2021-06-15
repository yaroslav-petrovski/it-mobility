package mumayank.com.itmobilityproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_select_city.*

/**
 * Select city activity
 *
 * Type the city name with the keyboard
 */
class SelectCityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        /**
         * Set layout title
         */
        title = "LIDL APP 2.0"

        /**
         * Database Firebase
         */
        val database = FirebaseDatabase.getInstance()

        /**
         * Reference The cities node in the database
         */
        val reference = database.getReference("cities")
        reference.keepSynced(true)

        /**
         * Cities array Array for all cities
         */
        var citiesArray = mutableListOf<String>()

        /**
         * Get cities from Database
         */
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    citiesArray.add(it.key.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                citiesArray.clear()
            }
        })


        /**
         * Adapter For the array of cities to show by the input
         */
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, citiesArray)

        CityEditText.setAdapter(adapter)

        /**
         * After Ok button clicked: go to next activity
         */
        getCityName.setOnClickListener {
            val cityName = CityEditText.text.toString()
            nextActivity(cityName)
        }
    }

    /**
     * Next activity
     * Go to Start Activity
     * @param cityName The name which next activity get as extra
     */
    private fun nextActivity(cityName: String){
        val intent = Intent(this, StartActivity::class.java)
        intent.putExtra("City", cityName)
        intent.putExtra("Lat", 0.0)
        intent.putExtra("Lon", 0.0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}