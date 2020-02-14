@file:Suppress("SpellCheckingInspection")

package by.karneichik.hello1c

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import by.karneichik.hello1c.adapters.OrderInfoAdapter
import by.karneichik.hello1c.pojo.Order
import by.karneichik.hello1c.viewModels.OrderViewModel
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

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
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adapter = OrderInfoAdapter(this)
        adapter.onOrderClickListener = object : OrderInfoAdapter.OnOrderClickListener {
            override fun onOrderClick(order: Order) {
                val intent = OrderDetailActivity.newIntent(
                    this@MainActivity,
                    order.uid
                )
                startActivity(intent)
            }
        }
        rvOrderList.adapter = adapter
        viewModel = ViewModelProviders.of(this)[OrderViewModel::class.java]
        viewModel.orderList.observe(this, Observer {
            adapter.orderInfoList = it
        })


    }
}

