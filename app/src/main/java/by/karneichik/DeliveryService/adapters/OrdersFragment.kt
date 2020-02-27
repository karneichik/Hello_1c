package by.karneichik.DeliveryService.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.karneichik.DeliveryService.OrderDetailActivity
import by.karneichik.DeliveryService.R
import by.karneichik.DeliveryService.pojo.Order
import by.karneichik.DeliveryService.viewModels.OrderViewModel

//class OrdersFragment(private val position: Int) : Fragment() {
class OrdersFragment() : Fragment() {

    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val position = arguments?.getInt("position")?:0
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        viewModel.setIndex(position)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_main, container, false)
        val adapter = OrderRecycleViewAdapter()
        val recycleView = root.findViewById<RecyclerView>(R.id.rvOrderList)

        adapter.onOrderClickListener = object : OrderRecycleViewAdapter.OnOrderClickListener {
            override fun onOrderClick(order: Order) {
                val intent = OrderDetailActivity.newIntent(context, order.uid)
                startActivity(intent)
            }
        }

        recycleView.adapter = adapter

        viewModel.orderList.observe(viewLifecycleOwner, Observer { adapter.orderInfoList = it })

        val swipeRefreshLayout = root.findViewById<SwipeRefreshLayout>(R.id.srlMainView)

        swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData(swipeRefreshLayout)
            swipeRefreshLayout.isRefreshing = true
        }

        return root
    }

}