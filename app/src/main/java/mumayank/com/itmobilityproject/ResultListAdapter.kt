package mumayank.com.itmobilityproject

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

/**
 * Result list adapter
 *
 * Adapt List with shops to the listView
 */
class ResultListAdapter(private val context: Activity, private val items: MutableList<ResultItem>) :
    ArrayAdapter<ResultItem>(context, R.layout.list_row, items) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.list_row, null, true)

        /**
         * TextViews from layout
         */
        val adressText = rowView.findViewById(R.id.adress) as TextView
        val cntText = rowView.findViewById(R.id.cnt) as TextView
        val distText = rowView.findViewById(R.id.distance) as TextView

        /**
         * set address textView
         */
        adressText.text = items[position].Adress

        /**
         * set product count textView
         */
        val stringForCntText = items[position].cntProducts.toString() + " products left"
        cntText.text = stringForCntText

        /**
         * Set distance textView
         * If user position unknown: hide textView
         */
        if (items[position].distanceToUser == 0.0F)
            distText.visibility = View.INVISIBLE
        val stringForDistText = String.format("%.2f", items[position].distanceToUser / 1000) + " km"
        distText.text = stringForDistText

        return rowView
    }
}