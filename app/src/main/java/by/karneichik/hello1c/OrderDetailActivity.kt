package by.karneichik.hello1c

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_order_detail.*

class OrderDetailActivity: AppCompatActivity() {

    private lateinit var viewModel: OrderViewModel

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
            tvClient_FIO.text = it.client_fio
            tvClient_phone.text = it.client_phone.toString()
            tvAddress.text = it.address
            tvTotalSum.text = it.totalsum.toString()
            tvPayForm.text = it.payform
            tvTime.text = it.time
            tvNumber.text = it.number
        })
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
