package mumayank.com.itmobilityproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_select_city.*

class SelectCityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        getCityName.setOnClickListener {
            var cityName = CityEditText.text.toString()
            Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show()
            //println("TESTTEST____$cityName")
            nextActivity(cityName)

        }
    }

    private fun nextActivity(cityName: String){
        val intent = Intent(this, StartActivity::class.java)
        intent.putExtra("City", cityName)
        /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)*/
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}