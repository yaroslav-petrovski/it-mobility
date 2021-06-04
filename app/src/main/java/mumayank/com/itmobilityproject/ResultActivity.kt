package mumayank.com.itmobilityproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*

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

        // Get city from previous Activity
        cityName = intent.getStringExtra("City").toString()
        productName = intent.getStringExtra("Product").toString()

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
                } else {
                    println("CHILD DOES NOT EXIST")
                    Toast.makeText(
                        applicationContext,
                        "There are no sops in $cityName",
                        Toast.LENGTH_LONG
                    ).show()
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