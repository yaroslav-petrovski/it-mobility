package mumayank.com.itmobilityproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.*
import java.util.ArrayList

class ResultActivity : AppCompatActivity() {

    //val rootRef = FirebaseDatabase.getInstance().reference
    //val cities = rootRef.child("cities")
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        database = FirebaseDatabase.getInstance()
        reference = database.getReference("cities")


        getData()

    }

    private fun getData() {
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //var list = ArrayList<String>()
                if (snapshot.hasChild("Darmstadt")){
                    println("CHILD EXISTS")
                } else{
                    println("CHILD DOES NOT EXIST")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Cancelled (DatabaseError)")
            }
        })

    }


}