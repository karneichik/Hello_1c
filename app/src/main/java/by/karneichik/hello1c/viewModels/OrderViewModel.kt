package by.karneichik.hello1c.viewModels

import android.app.Application
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import by.karneichik.hello1c.api.ApiFactory
import by.karneichik.hello1c.database.AppDatabase
import by.karneichik.hello1c.helpers.PrefHelper
import by.karneichik.hello1c.pojo.Order
import by.karneichik.hello1c.pojo.Orders
import by.karneichik.hello1c.pojo.Product
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()
    private lateinit var allProducts : LiveData<List<Product>>
    private lateinit var order : LiveData<Order>
    private val context = application.applicationContext

    val orderList = db.orderInfoDao().getOrdersList()

    fun getOrderInfo(uid: String): LiveData<Order> {
        order = db.orderInfoDao().getOrderInfoLiveDate(uid)
        return order
    }

    fun getProductsList(uid:String): LiveData<List<Product>> {
        allProducts = db.orderProductsInfoDao().getOrderProductsLiveData(uid)
        return allProducts
    }

    fun update(product: Product) {
        db.orderProductsInfoDao().insertProduct(product)
    }

    fun cancelOrder(uid:String) {

        AsyncTask.execute{

            val order = db.orderInfoDao().getOrderInfo(uid)
            order.isCancelled = true
            db.orderInfoDao().insertOrder(order)

            val products = db.orderProductsInfoDao().getOrderProductsList(uid)
            for (product in products) product.delivered = false
            db.orderProductsInfoDao().insertProducts(products)

            syncOrders()
        }

    }

    fun saveOrder(uid:String) {

        AsyncTask.execute{

            val order = db.orderInfoDao().getOrderInfo(uid)
            order.isDelivered = true
            db.orderInfoDao().insertOrder(order)

            syncOrders()
        }
    }

    private fun syncOrders() {

        val listData = db.orderInfoDao().getAllOrders()
        val listOrders = listData.map { it.order.apply { products = it.productsList } }

        ApiFactory.apiService.syncOrders(Orders(listOrders)).enqueue(
            object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    Log.d("TEST_OF_LOADING_DATA", "Failure: ${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Toast.makeText(context, response.body().toString(), Toast.LENGTH_LONG)
                        .show()
                    Log.d("TEST_OF_LOADING_DATA", "Success: $response")

                }
            }
        )

    }

    private fun loadData(layout: ConstraintLayout? = null) {
        val accessToken = PrefHelper.preferences.getString("accessToken", null) ?: return
//        val context:Context = getApplication()
//        val toast = Toast(context)
        val disposable = ApiFactory.apiService.getOrders(mapOf("AccessToken" to accessToken))
            .map { it -> it.orders.map { it } }
            .subscribeOn(Schedulers.io())
            .subscribe({ it ->

                //TODO add sync to server
                db.orderInfoDao().deleteAllOrders()
                db.orderProductsInfoDao().deleteAllProducts()

                db.orderInfoDao().insertOrders(it)
                val allProducts = it.flatMap { it.products }
                db.orderProductsInfoDao().insertProducts(allProducts)

                Handler(Looper.getMainLooper()).post {
                    layout?.visibility = View.GONE
                    Toast.makeText(context,"Success:",Toast.LENGTH_SHORT).show()
                }
                Log.d("TEST_OF_LOADING_DATA", "Success: $it")
            }, {
                Handler(Looper.getMainLooper()).post {
                    layout?.visibility = View.GONE
                    Toast.makeText(context,it.message,Toast.LENGTH_SHORT).show()
                }
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
            })
        compositeDisposable.add(disposable)


    }

    fun refreshData(layout: ConstraintLayout) {
        loadData(layout)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}