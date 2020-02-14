package by.karneichik.hello1c.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import by.karneichik.hello1c.R
import by.karneichik.hello1c.pojo.Product
import kotlinx.android.synthetic.main.item_product_info.view.*

class ProductListAdapter(private val context: Context) : RecyclerView.Adapter<ProductListAdapter.ProductViewHolder>() {

    var productInfoList: List<Product> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    //var onOrderClickListener: OnOrderClickListener? = null

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

                tvGoods.text  = goods
                tvPrice.text = price.toString()
                tvSum.text = sum.toString()
                tvCount.text   = count.toString()

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
    }

//    interface OnOrderClickListener {
//        fun onOrderClick(order: Order)
//    }
}