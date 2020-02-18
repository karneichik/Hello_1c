package by.karneichik.hello1c.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_order_info.view.*

class OrderViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvNumber = itemView.tvNumber
    val tvAddress = itemView.tvAddress
    val tvTime = itemView.tvTime
}