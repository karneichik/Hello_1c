package by.karneichik.hello1c

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import by.karneichik.hello1c.adapters.ProductListAdapter
import by.karneichik.hello1c.viewModels.OrderViewModel
import by.karneichik.hello1c.viewModels.ProductsViewModel
import kotlinx.android.synthetic.main.activity_order_detail.*

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel
    private lateinit var productsViewModel: ProductsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        if (!intent.hasExtra(EXTRA_UID_ORDER)) {
            finish()
            return
        }
        val uid = intent.getStringExtra(EXTRA_UID_ORDER)

        viewModel = ViewModelProviders.of(this)[OrderViewModel::class.java]
        viewModel.getOrderInfo(uid).observe(this, Observer {
            with(it.order) {
                tvClient_FIO.text = client_fio
                tvClient_phone.text = client_phone.toString()
                tvAddress.text = address
                tvTotalSum.text = totalsum.toString()
                tvPayForm.text = payform
                tvTime.text = time
                tvNumber.text = number
            }
        })

        val adapter = ProductListAdapter(this)
        rvProductList.adapter = adapter

        productsViewModel = ViewModelProviders.of(this)[ProductsViewModel::class.java]
        productsViewModel.getProductsList(uid).observe(this, Observer {
            adapter.productInfoList = it
        })
//        productsViewModel.getOrderInfo(uid).observe(this, Observer {
//            adapter.productInfoList = it.productsList
//        })

    }

    companion object {
        private const val EXTRA_UID_ORDER = "uid"

        fun newIntent(context: Context, uid: String): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(EXTRA_UID_ORDER, uid)
            return intent
        }
    }
}
