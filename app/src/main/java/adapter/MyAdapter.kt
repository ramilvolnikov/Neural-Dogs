package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.tensorflow.lite.examples.classification.R

// Adapter for recycler view in ClassifyActivity
class MyAdapter(private val breedValues: List<String>, private val confidenceValues: List<String>, private val iconIds: List<Int>, onLearnListener : OnLearnListener): RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    // Item click listener
    var mOnLearnListener = onLearnListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return ViewHolder(itemView, mOnLearnListener)
    }

    override fun getItemCount() = breedValues.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView?.text = breedValues[position]
        holder.textView2?.text = confidenceValues[position]
        holder.icon?.setImageResource(iconIds[position])

    }

    class ViewHolder(itemView: View, onLearnListener: OnLearnListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var textView: TextView? = null
        var textView2: TextView? = null
        var icon: ImageView? = null
        var onLearnListener : OnLearnListener

        init {
            textView = itemView.findViewById(R.id.text_breed)
            textView2 = itemView.findViewById(R.id.text_confidence)
            icon = itemView.findViewById(R.id.top_icon)
            itemView.setOnClickListener(this)
            this.onLearnListener = onLearnListener
        }

        // Redirect onClick to click listener in interface OnLearnListener
        override fun onClick(p0: View?) {
            onLearnListener.onLearnClick(adapterPosition)
        }
    }

    interface OnLearnListener{
        fun onLearnClick(position: Int)
    }
}