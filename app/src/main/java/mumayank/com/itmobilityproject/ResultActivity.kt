package mumayank.com.itmobilityproject

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_result.*

/**
 * Result activity
 *
 * Activity to show all shops in the city with the count of products
 */
class ResultActivity : AppCompatActivity() {

    /**
     * City name Name of the city
     */
    var cityName = "NaN"

    /**
     * Lat user Latitude use position
     */
    var latUser = 0.0

    /**
     * Lon user Longitude user position
     */
    var lonUser = 0.0

    /**
     * Product name Name of the product
     */
    var productName = "NaN"

    /**
     * List of shops to store results
     */
    var shops = mutableListOf<ResultItem>()

    /**
     * Database Firebase connection
     */
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    /**
     * On create
     * Get information from Firebase and show in the listView
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        /**
         * M list view
         * ListView in the layout
         */
        var mListView = findViewById<ListView>(R.id.results_list)

        /**
         * Shared pref
         * Shared preferences for last search
         */
        val sharedPref = this@ResultActivity.getPreferences(Context.MODE_PRIVATE)

        /**
         * Get city, position and product name from previous Activity
         */
        cityName = intent.getStringExtra("City").toString()
        productName = intent.getStringExtra("Product").toString()
        latUser = intent.getDoubleExtra("Lat", 0.0)
        lonUser = intent.getDoubleExtra("Lon", 0.0)

        /**
         * If city == NaN: show previous search (s. Start Activity)
         * Get product name from shared preferences
         */
        if (productName == "NaN") {
            productName = sharedPref.getString("Product", "NaN").toString()
            results_text.text = "Results may be not actual!"
        }

        /**
         * Set layout title
         */
        title = productName

        /**
         * Cities node in the DB (Persistence is enabled in MainActivity)
         */
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("cities")
        reference.keepSynced(true)

        /**
         * Get data from Firebase
         */
        getData()

        /**
         * If user clicks on list item start navigation in Google Maps
         */
        mListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as ResultItem
                val gmmIntentUri = Uri.parse(
                    "geo:"
                            + selectedItem.lat.toString() + ","
                            + selectedItem.long.toString()
                            + "?q=" + selectedItem.lat.toString() + ","
                            + selectedItem.long.toString() + "(Label+Name)"
                )
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                mapIntent.resolveActivity(packageManager)?.let {
                    startActivity(mapIntent)
                }
            }

    }

    /**
     * Get data
     * Request data from Firebase
     */
    private fun getData() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                /**
                 * Location user
                 * initialize zser location with longitude and latitude
                 */
                val locationUser = Location("dummyprovider")
                locationUser.latitude = latUser
                locationUser.longitude = lonUser
                val locationShop = Location("dummyprovider")

                /**
                 * M list view
                 * ListView from the layout
                 */
                var mListView = findViewById<ListView>(R.id.results_list)

                /**
                 * Remove all data before new request
                 */
                shops.clear()

                /**
                 * Check if the city exists in the DB
                 */
                if (snapshot.hasChild(cityName)) {
                    /**
                     * Store shops in th list (if the city exists)
                     */
                        snapshot.child(cityName).children.first().children.forEach {
                            /**
                             * show product if exists
                             */
                        if (it.child("Products").hasChild(productName)) {
                            val productCnt =
                                it.child("Products").child(productName).value.toString().toInt()
                            val lat = it.child("lat").value.toString().toDouble()
                            val long = it.child("lon").value.toString().toDouble()
                            locationShop.latitude = lat
                            locationShop.longitude = long
                            /**
                             * Distance to user
                             * Calculate distance between user position and shop position
                             */
                            var distanceToUser = locationShop.distanceTo(locationUser)
                            if (locationUser.latitude == 0.0)
                                distanceToUser = 0.0F

                            /**
                             * Add product to the shops list
                             */
                            shops.add(
                                ResultItem(
                                    it.key.toString(),
                                    productCnt,
                                    lat,
                                    long,
                                    distanceToUser
                                )
                            )

                            /**
                             * Store last searched product name in the shared preferences
                             */
                            val sharedPref =
                                this@ResultActivity.getPreferences(Context.MODE_PRIVATE)

                            with(sharedPref.edit()) {
                                putString("Product", productName)
                                apply()
                            }
                        } else {
                            /**
                             * Show message if no products with product name found
                             */
                            val message = "No $productName found:("
                            results_text.text = message
                        }
                    }
                } else {
                    /**
                     * Show message if no city with city name found
                     */
                    val message = "There are no shops in $cityName:("
                    results_text.text = message
                }

                /**
                 * Array adapter
                 * Show Shops in the listView
                 */
                val arrayAdapter: ArrayAdapter<*>
                mListView = findViewById<ListView>(R.id.results_list)
                shops.sortWith(compareBy { it.distanceToUser })
                arrayAdapter = ResultListAdapter(this@ResultActivity, shops)
                mListView.adapter = arrayAdapter
            }

            /**
             * On cancelled
             *
             * Show message if DB-Error
             */
            override fun onCancelled(error: DatabaseError) {
                results_text.text = "Database error:("
            }
        })

    }

    /**
     * On create options menu
     *
     * Show menu on actions bar
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    /**
     * On options item selected
     *
     * Menu buttons functionality
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when (item.itemId) {
            /**
             * Sort shops by number of products
             */
            R.id.sortByCnt -> {
                var mListView = findViewById<ListView>(R.id.results_list)
                val arrayAdapter: ArrayAdapter<*>
                mListView = findViewById<ListView>(R.id.results_list)
                shops.sortWith(compareByDescending { it.cntProducts })
                arrayAdapter = ResultListAdapter(this@ResultActivity, shops)
                mListView.adapter = arrayAdapter
                return true
            }
            /**
             * Sort shops by distance between shop and user
             */
            R.id.sortByDistance -> {
                var mListView = findViewById<ListView>(R.id.results_list)
                val arrayAdapter: ArrayAdapter<*>
                mListView = findViewById<ListView>(R.id.results_list)
                shops.sortWith(compareBy { it.distanceToUser })
                arrayAdapter = ResultListAdapter(this@ResultActivity, shops)
                mListView.adapter = arrayAdapter
                return true
            }
            /**
             * Show product in the Lidl Web shop (go to the browser)
             */
            R.id.buyOnline -> {
                val lidlString = "https://www.lidl.de/de/search?query=$productName"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(lidlString))
                startActivity(browserIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * On back pressed
     * Go to Start Activity after back button click
     */
    override fun onBackPressed() {
        val intent = Intent(this@ResultActivity, StartActivity::class.java)
        intent.putExtra("City", cityName)
        intent.putExtra("Lat", latUser)
        intent.putExtra("Lon", lonUser)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

}