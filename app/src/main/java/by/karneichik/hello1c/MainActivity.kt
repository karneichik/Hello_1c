@file:Suppress("SpellCheckingInspection")

package by.karneichik.hello1c

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
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

        val adapter = SectionedRecyclerViewAdapter()

        rvOrderList.adapter = adapter
        viewModel = ViewModelProviders.of(this)[OrderViewModel::class.java]
        viewModel.orderList.observe(this, Observer { it ->
            val splitedDate = splitDataToSection(it)
            adapter.removeAllSections()
            for (date in splitedDate!!.entries.sortedByDescending { it.key }) {

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
            }

            adapter.notifyDataSetChanged()

        })

        srlMainView.setOnRefreshListener {
            foreground.visibility = View.VISIBLE
            viewModel.refreshData(foreground)
            srlMainView.isRefreshing = false
        }

        foreground.visibility = View.GONE

    }

    private fun splitDataToSection(list:List<Order>) : TreeMap<String,List<Order>>? {

        val treeMap : TreeMap<String,List<Order>> = TreeMap()

        val listCanceled:List<Order> = list.filter {it.isCancelled}
        val listOnDelivery:List<Order> = list.filter { !it.isCancelled && !it.isDelivered }
        val listDelivered:List<Order> = list.filter { it.isDelivered }

        if (listCanceled.isNotEmpty()) treeMap[resources.getString(R.string.canceled)] = listCanceled
        if (listDelivered.isNotEmpty()) treeMap[resources.getString(R.string.delivered)] = listDelivered
        if (listOnDelivery.isNotEmpty()) treeMap[resources.getString(R.string.on_delivery)] = listOnDelivery


        return treeMap

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

}

