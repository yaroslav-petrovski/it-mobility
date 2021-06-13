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

class SelectCityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        title="LIDL APP 2.0"

        var database = FirebaseDatabase.getInstance()
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        var reference = database.getReference("cities")
        reference.keepSynced(true)

        //var cities = arrayOf("Darmstadt", "Offenbach am Main", "Frankfurt am Main")
        var citiesArray = mutableListOf<String>()

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
        

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, citiesArray)

        CityEditText.setAdapter(adapter)

        getCityName.setOnClickListener {
            var cityName = CityEditText.text.toString()
            nextActivity(cityName)

        }
    }

    private fun nextActivity(cityName: String){
        val intent = Intent(this, StartActivity::class.java)
        intent.putExtra("City", cityName)
        intent.putExtra("Lat", 0.0)
        intent.putExtra("Lon", 0.0)
        /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)*/
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}