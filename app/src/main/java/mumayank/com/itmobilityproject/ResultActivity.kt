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
    var productName = "Mikrowelle"

    // List to store results
    var shops = mutableListOf<String>()

    // DB connection
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // Get city from previous Activity
        cityName = intent.getStringExtra("City").toString()

        // Cities node in the DB
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("cities")

        getData()

    }

    private fun getData() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Check if the city exists in the DB
                if (snapshot.hasChild(cityName)) {
                    // Store shops in th list
                    snapshot.child(cityName).children.first().children.forEach {
                        shops.add(it.key.toString())
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
                arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, shops)
                mListView.adapter = arrayAdapter
            }

            // When DB-Error
            override fun onCancelled(error: DatabaseError) {
                println("Cancelled (DatabaseError)")
            }
        })

    }

}