package by.karneichik.DeliveryService

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import by.karneichik.DeliveryService.api.ApiFactory
import by.karneichik.DeliveryService.helpers.PrefHelper
import by.karneichik.DeliveryService.viewModels.OrderViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_orders_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderListActivity : AppCompatActivity() {

    private lateinit var orderViewModel : OrderViewModel

    private fun onSelectAnimation (view: View) {
        (AnimatorInflater.loadAnimator(this, R.animator.avd_anim) as AnimatorSet).apply {
            setTarget(view)
            start()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders_list)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_on_delivery, R.id.navigation_to_return, R.id.navigation_delivered
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        bottom_nav.setupWithNavController(navController)
        bottom_nav.setOnNavigationItemSelectedListener{
            when (it.itemId) {
                R.id.navigation_on_delivery -> {
                    onSelectAnimation(bottom_nav[0].findViewById<View>(R.id.navigation_on_delivery))
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_on_delivery)
                }
                R.id.navigation_to_return -> {
                    onSelectAnimation(bottom_nav[0].findViewById<View>(R.id.navigation_to_return))
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_to_return)
                }
                R.id.navigation_delivered -> {
                    onSelectAnimation(bottom_nav[0].findViewById<View>(R.id.navigation_delivered))
                    findNavController(R.id.nav_host_fragment).navigate(R.id.navigation_delivered)
                }
            }
            return@setOnNavigationItemSelectedListener true
        }

        orderViewModel = OrderViewModel(application)

        AsyncTask.execute{
            val fcmToken = getSharedPreferences("_", Context.MODE_PRIVATE).getString("fb",null)
            val token = FirebaseInstanceId.getInstance().getToken("696540481598","FCM") ?: return@execute

            if (fcmToken != token ) {
                getSharedPreferences("_", Context.MODE_PRIVATE).edit().putString("fb", token)
                    .apply()
                orderViewModel.updateToken()
            }
        }
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
