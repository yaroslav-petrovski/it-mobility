package mumayank.com.itmobilityproject

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    // Variables for DB Request
    var cityName = "NaN"
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
        if (productName == "NaN") {
            productName = sharedPref.getString("Product", "NaN").toString()
            results_text.text = "Results may be not actual!"
        }

        // Cities node in the DB (Persistence is enabled in MainActivity)
        database = FirebaseDatabase.getInstance()
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        reference = database.getReference("cities")
        reference.keepSynced(true)

        getData()

        mListView.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
            val selectedItem = parent.getItemAtPosition(position) as ResultItem
            /*Toast.makeText(this@ResultActivity, selectedItem.lat.toString(), Toast.LENGTH_SHORT)
                .show()*/
            val gmmIntentUri = Uri.parse("geo:"
                    +selectedItem.lat.toString()+","
                    +selectedItem.long.toString()
                    +"?q="+selectedItem.lat.toString()+","
                    +selectedItem.long.toString()+"(Label+Name)"
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

                var mListView = findViewById<ListView>(R.id.results_list)

                shops.clear()

                // Check if the city exists in the DB
                if (snapshot.hasChild(cityName)) {
                    // Store shops in th list (if the city exists)
                    snapshot.child(cityName).children.first().children.forEach {
                        //show product if exists
                        if(it.child("Products").hasChild(productName)){
                            val productCnt = it.child("Products").child(productName).value.toString().toInt()
                            val lat = it.child("lat").value.toString().toFloat()
                            val long = it.child("lon").value.toString().toFloat()
                            shops.add(ResultItem(it.key.toString(), productCnt, lat, long))

                            val sharedPref = this@ResultActivity.getPreferences(Context.MODE_PRIVATE)

                            with(sharedPref.edit()){
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

                // Show Shops in th listview
                val arrayAdapter: ArrayAdapter<*>
                mListView = findViewById<ListView>(R.id.results_list)
                shops.sortWith(compareByDescending { it.cntProducts })
                //arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, shops)
                arrayAdapter = ResultListAdapter(this@ResultActivity, shops, productName)
                mListView.adapter = arrayAdapter
            }

            // When DB-Error
            override fun onCancelled(error: DatabaseError) {
                results_text.text = "Database error:("
            }
        })

    }

    override fun onBackPressed() {
        val intent = Intent(this@ResultActivity, StartActivity::class.java)
        intent.putExtra("City", cityName)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

}