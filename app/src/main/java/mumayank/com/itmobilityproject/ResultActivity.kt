package mumayank.com.itmobilityproject

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    //val sharedPref = this.getPreferences(Context.MODE_PRIVATE)

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

        val sharedPref = this@ResultActivity.getPreferences(Context.MODE_PRIVATE)

        // Get city from previous Activity
        cityName = intent.getStringExtra("City").toString()
        productName = intent.getStringExtra("Product").toString()
        if (productName == "NaN") {
            productName = sharedPref.getString("Product", "NaN").toString()
            results_text.text = "Results may be not actual!"
        }

        // Cities node in the DB
        database = FirebaseDatabase.getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        reference = database.getReference("cities")
        reference.keepSynced(true)

        getData()

    }

    private fun getData() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                shops.clear()

                // Check if the city exists in the DB
                if (snapshot.hasChild(cityName)) {
                    // Store shops in th list
                    snapshot.child(cityName).children.first().children.forEach {
                        val productCnt = it.child("Products").child(productName).value.toString().toInt()
                        shops.add(ResultItem(it.key.toString(), productCnt))
                    }
                    if (shops.isEmpty()){
                        val message = "No " + productName + "found:("
                        results_text.text = message
                    } else {

                        val sharedPref = this@ResultActivity.getPreferences(Context.MODE_PRIVATE)

                        with(sharedPref.edit()){
                            putString("Product", productName)
                            apply()
                        }

                    }
                } else {
                    val message = "There are no shops in " + cityName + ":("
                    results_text.text = message
                }

                // Show Shops in th listview
                val arrayAdapter: ArrayAdapter<*>
                var mListView = findViewById<ListView>(R.id.results_list)
                shops.sortWith(compareByDescending { it.cntProducts })
                //arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, shops)
                arrayAdapter = ResultListAdapter(this@ResultActivity, shops, productName)
                mListView.adapter = arrayAdapter
            }

            // When DB-Error
            override fun onCancelled(error: DatabaseError) {
                println("Cancelled (DatabaseError)")
            }
        })

    }

}