package by.karneichik.hello1c.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.karneichik.hello1c.R
import by.karneichik.hello1c.pojo.Product
import kotlinx.android.synthetic.main.item_product_info.view.*

class ProductListAdapter(private val context: Context) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    var productInfoList: List<Product> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun itemUpdate() {
        notifyDataSetChanged()
    }
//    fun removeItem(model: Product, position: Int) {
//        notifyDataSetChanged()
//    }
//    fun removeItem(model: Product, position: Int) {
//        notifyDataSetChanged()
//    }

    fun getProduct(position: Int) = productInfoList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_product_info, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount() = productInfoList.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productInfoList[position]
        with(holder) {
            with(product) {

                if (delivered)
                lProductItem.setBackgroundColor(context.getColor(R.color.colorDelivered))
                else lProductItem.setBackgroundColor(context.getColor(R.color.colorCancel))

                tvGoods.text  = goods
                tvPrice.text = price.toString()
                tvSum.text = sum.toString()
                tvCount.text   = count.toString()

                isDelivered = delivered

                if (serial != "") tvSerial.text = serial else tvSerial.visibility = View.GONE

            }
        }
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCount = itemView.tvCount
        val tvGoods = itemView.tvGoods
        val tvSerial = itemView.tvSerial
        val tvPrice = itemView.tvPrice
        val tvSum = itemView.tvSum
        val lProductItem = itemView.lProductItem
        var isDelivered : Boolean = true
    }

//    interface OnOrderClickListener {
//        fun onOrderClick(order: Order)
//    }
}