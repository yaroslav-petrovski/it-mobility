package mumayank.com.itmobilityproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_select_city.*

class SelectCityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_city)

        getCityName.setOnClickListener {
            var cityName = CityEditText.text
            Toast.makeText(this, cityName, Toast.LENGTH_SHORT).show()
        }
    }
}