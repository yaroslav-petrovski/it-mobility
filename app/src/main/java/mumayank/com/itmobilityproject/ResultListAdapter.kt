package mumayank.com.itmobilityproject

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ResultListAdapter(private val context: Activity, private val items: MutableList<ResultItem>)
    : ArrayAdapter<ResultItem>(context, R.layout.list_row, items) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_row, null, true)

        val adressText = rowView.findViewById(R.id.adress) as TextView
        val productText = rowView.findViewById(R.id.Product) as TextView
        val cntText = rowView.findViewById(R.id.cnt) as TextView

        adressText.text = items[position].Adress
        productText.text = "TEST"
        cntText.text = items[position].cntProducts.toString()

        return rowView
    }
}