package by.karneichik.hello1c.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import by.karneichik.hello1c.R
import by.karneichik.hello1c.pojo.Order
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters


class OrdersSection(private val list: List<Order>,private val title:String) : Section(
    SectionParameters.builder()
        .headerResourceId(R.layout.item_order_header)
        .itemResourceId(R.layout.item_order_info)
        .build()) {

    var onOrderClickListener: OnOrderClickListener? = null


    override fun getContentItemsTotal(): Int {
        return list.size
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val itemHolder :OrderViewHolder = holder as OrderViewHolder

        val order = list[position]
        with(itemHolder) {
            with(order) {
                tvNumber.text   = number
                tvAddress.text  = address
                tvTime.text     = time
                itemView.setOnClickListener {
                    onOrderClickListener?.onOrderClick(this)
                }
            }
        }

    }

    override fun getItemViewHolder(view: View): OrderViewHolder {
        return OrderViewHolder(view)
    }

    override  fun getHeaderViewHolder(view:View):RecyclerView.ViewHolder{
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder : RecyclerView.ViewHolder ) {

        val headerHolder = holder as HeaderViewHolder

        headerHolder.tvTitle.text = title

    }


    interface OnOrderClickListener {
        fun onOrderClick(order: Order)
    }

}