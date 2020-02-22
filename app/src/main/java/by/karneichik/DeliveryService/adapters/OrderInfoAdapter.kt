package by.karneichik.DeliveryService.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.karneichik.DeliveryService.R
import by.karneichik.DeliveryService.pojo.Order

class OrderInfoAdapter(private val context: Context) : RecyclerView.Adapter<OrderViewHolder>() {

    var orderInfoList: List<Order> = listOf()
//    var orderInfoList: TreeMap<String,List<Order>> = TreeMap()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
//    var onOrderClickListener: OrdersSection.OnOrderClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_info, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount() = orderInfoList.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
//        val order = orderInfoList[position]
//        with(holder) {
//            with(order) {
//                tvNumber.text   = order.number
//                tvAddress.text  = order.address
//                tvTime.text     = order.time
//                itemView.setOnClickListener {
//                    onOrderClickListener?.onOrderClick(this)
//                }
//            }
//        }
    }

//    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
////        val tvNumber = itemView.tvNumber
////        val tvAddress = itemView.tvAddress
////        val tvTime = itemView.tvTime
////    }


}