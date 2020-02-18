@file:Suppress("SpellCheckingInspection")

package by.karneichik.hello1c

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import by.karneichik.hello1c.adapters.OrdersSection
import by.karneichik.hello1c.helpers.PrefHelper
import by.karneichik.hello1c.pojo.Order
import by.karneichik.hello1c.viewModels.OrderViewModel
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        PrefHelper.init(this)

        val adapter = SectionedRecyclerViewAdapter()//OrderInfoAdapter(this)
//        adapter.onOrderClickListener = object : OrdersSection.OnOrderClickListener {
//            override fun onOrderClick(order: Order) {
//                val intent = OrderDetailActivity.newIntent(
//                    this@MainActivity,
//                    order.uid
//                )
//                startActivity(intent)
//            }
//        }


        rvOrderList.adapter = adapter
        viewModel = ViewModelProviders.of(this)[OrderViewModel::class.java]
        viewModel.orderList.observe(this, Observer {
            val splitedDate = splitDataToSection(it)
            for (date in splitedDate!!.entries) {

                val section = OrdersSection(date.value,date.key)
                section.onOrderClickListener = object : OrdersSection.OnOrderClickListener {
                    override fun onOrderClick(order: Order) {
                        val intent = OrderDetailActivity.newIntent(
                            this@MainActivity,
                            order.uid
                        )
                        startActivity(intent)
                    }
                }
                adapter.addSection(section)


//                val sectionPos = adapter.getAdapterForSection(section).sectionPosition;
//                adapter.notifyItemInserted(sectionPos);
//                rvOrderList.smoothScrollToPosition(sectionPos);

            }

            adapter.notifyDataSetChanged()

//            adapter.orderInfoList = it
        })

        srlMainView.setOnRefreshListener {
            viewModel.refreshData()
            srlMainView.isRefreshing = false
        }


    }

    fun splitDataToSection(list:List<Order>) : TreeMap<String,List<Order>>? {

        var treeMap : TreeMap<String,List<Order>> = TreeMap()

        val listCanceled:List<Order> = list.filter {it.isCancelled}
        val listOnDelivery:List<Order> = list.filter { !it.isCancelled && !it.isDelivered }
        val listDelivered:List<Order> = list.filter { it.isDelivered }

        if (listOnDelivery.isNotEmpty()) treeMap.put(resources.getString(R.string.on_delivery),listOnDelivery)
        if (listDelivered.isNotEmpty()) treeMap.put(resources.getString(R.string.delivered),listCanceled)
        if (listCanceled.isNotEmpty()) treeMap.put(resources.getString(R.string.canceled),listDelivered)

        return treeMap

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

}

