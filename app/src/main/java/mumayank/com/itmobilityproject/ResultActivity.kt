package mumayank.com.itmobilityproject

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    // Variables for DB Request
    var cityName = "NaN"
    var latUser = 0.0
    var lonUser = 0.0
    var productName = "NaN"

    // List to store results
    var shops = mutableListOf<ResultItem>()

    // DB connection
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        var mListView = findViewById<ListView>(R.id.results_list)

        //Shared preferences for last search
        val sharedPref = this@ResultActivity.getPreferences(Context.MODE_PRIVATE)

        // Get city from previous Activity or shared preferences
        cityName = intent.getStringExtra("City").toString()
        productName = intent.getStringExtra("Product").toString()
        latUser = intent.getDoubleExtra("Lat", 0.0)
        lonUser = intent.getDoubleExtra("Lon", 0.0)

        if (productName == "NaN") {
            productName = sharedPref.getString("Product", "NaN").toString()
            results_text.text = "Results may be not actual!"
        }

        title = productName

        // Cities node in the DB (Persistence is enabled in MainActivity)
        database = FirebaseDatabase.getInstance()
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        reference = database.getReference("cities")
        reference.keepSynced(true)

        getData()

        mListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as ResultItem
                /*Toast.makeText(this@ResultActivity, selectedItem.lat.toString(), Toast.LENGTH_SHORT)
                    .show()*/
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

    private fun getData() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val locationUser = Location("dummyprovider")
                locationUser.latitude = latUser
                locationUser.longitude = lonUser
                val locationShop = Location("dummyprovider")

                var mListView = findViewById<ListView>(R.id.results_list)

                shops.clear()

                // Check if the city exists in the DB
                if (snapshot.hasChild(cityName)) {
                    // Store shops in th list (if the city exists)
                    snapshot.child(cityName).children.first().children.forEach {
                        //show product if exists
                        if (it.child("Products").hasChild(productName)) {
                            val productCnt =
                                it.child("Products").child(productName).value.toString().toInt()
                            val lat = it.child("lat").value.toString().toDouble()
                            val long = it.child("lon").value.toString().toDouble()
                            locationShop.latitude = lat
                            locationShop.longitude = long
                            var distanceToUser = locationShop.distanceTo(locationUser)
                            if (locationUser.latitude == 0.0)
                                distanceToUser = 0.0F

                            shops.add(
                                ResultItem(
                                    it.key.toString(),
                                    productCnt,
                                    lat,
                                    long,
                                    distanceToUser
                                )
                            )

                            val sharedPref =
                                this@ResultActivity.getPreferences(Context.MODE_PRIVATE)

                            with(sharedPref.edit()) {
                                putString("Product", productName)
                                apply()
                            }
                        } else {
                            val message = "No $productName found:("
                            results_text.text = message
                        }
                    }
                } else {
                    val message = "There are no shops in $cityName:("
                    results_text.text = message
                }

                // Show Shops in the listview
                val arrayAdapter: ArrayAdapter<*>
                mListView = findViewById<ListView>(R.id.results_list)
                shops.sortWith(compareBy { it.distanceToUser })
                arrayAdapter = ResultListAdapter(this@ResultActivity, shops)
                mListView.adapter = arrayAdapter
            }

            // When DB-Error
            override fun onCancelled(error: DatabaseError) {
                results_text.text = "Database error:("
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item)
        return when (item.itemId) {
            R.id.sortByCnt -> {
                var mListView = findViewById<ListView>(R.id.results_list)
                val arrayAdapter: ArrayAdapter<*>
                mListView = findViewById<ListView>(R.id.results_list)
                shops.sortWith(compareByDescending { it.cntProducts })
                arrayAdapter = ResultListAdapter(this@ResultActivity, shops)
                mListView.adapter = arrayAdapter
                return true
            }
            R.id.sortByDistance -> {
                var mListView = findViewById<ListView>(R.id.results_list)
                val arrayAdapter: ArrayAdapter<*>
                mListView = findViewById<ListView>(R.id.results_list)
                shops.sortWith(compareBy { it.distanceToUser })
                arrayAdapter = ResultListAdapter(this@ResultActivity, shops)
                mListView.adapter = arrayAdapter
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

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