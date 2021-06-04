package mumayank.com.itmobilityproject

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    var city = "NaN"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        city = intent.getStringExtra("City").toString()

        cityName.text = city

        btnQR.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("City", city)
            intent.putExtra("Product", "Mikrowelle")
            startActivity(intent)
        }

        btnPrevSearch.setOnClickListener {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("City", city)
            intent.putExtra("Product", "NaN")
            startActivity(intent)
        }
    }

}