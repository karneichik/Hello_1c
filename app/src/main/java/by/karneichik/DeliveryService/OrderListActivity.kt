package by.karneichik.DeliveryService

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import by.karneichik.DeliveryService.adapters.TabsPagerAdapter
import by.karneichik.DeliveryService.helpers.PrefHelper
import by.karneichik.DeliveryService.viewModels.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_orders_list.*


class OrderListActivity : AppCompatActivity() {

    private val TAG = "TEST_OF_LOADING_DATA"
    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_list)

        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)

        srlMainView.setOnRefreshListener {
            viewModel.refreshData(srlMainView)
            srlMainView.isRefreshing = true
        }

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        val tabsPagerAdapter = TabsPagerAdapter(this,supportFragmentManager)
        view_pager.adapter = tabsPagerAdapter
        tabs.setupWithViewPager(view_pager)

        AsyncTask.execute{

            val fcmToken = getSharedPreferences("_", Context.MODE_PRIVATE).getString("fb",null)

            if (fcmToken == null ) {
                val token =
                    FirebaseInstanceId.getInstance().getToken("696540481598","FCM")
                getSharedPreferences("_", Context.MODE_PRIVATE).edit().putString("fb", token)
                    .apply()
            }

        }

//        floatingActionButton.setOnClickListener {
//            FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.w(TAG, "getInstanceId failed", task.exception)
//                        return@OnCompleteListener
//                    }
//
//                    // Get new Instance ID token
//                    val token = task.result?.token
//
//                    // Log and toast
//                    val msg = getString(R.string.msg_token_fmt, token)
//                    Log.d(TAG, msg)
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                })
//
//        }
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
            } R.id.action_sign_out -> {
                signOut()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, OrderListActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private fun signOut() {
        startActivity(MainActivity.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut()
        finish()
    }



}
