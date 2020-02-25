package by.karneichik.DeliveryService.viewModels

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.karneichik.DeliveryService.api.ApiFactory
import by.karneichik.DeliveryService.database.AppDatabase
import by.karneichik.DeliveryService.helpers.PrefHelper
import by.karneichik.DeliveryService.pojo.Order
import by.karneichik.DeliveryService.pojo.Orders
import by.karneichik.DeliveryService.pojo.Product
import com.google.firebase.iid.FirebaseInstanceId
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
    private lateinit var orders: LiveData<List<Order>>
    private val context = application.applicationContext
    private val _index = MutableLiveData<Int>()
    var orderList = db.orderInfoDao().getOrdersList1()

    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }

    fun setIndex(index: Int) {
        _index.value = index
        orderList = when (index) {
            0-> db.orderInfoDao().getOrdersList1()
            1-> db.orderInfoDao().getOrdersList2()
            2-> db.orderInfoDao().getOrdersList3()
            else -> db.orderInfoDao().getOrdersList1()
        }
    }

    fun getOrderInfo(uid: String): LiveData<Order> {
        order = db.orderInfoDao().getOrderInfoLiveDate(uid)
        return order
    }

    fun getProductsList(uid:String): LiveData<List<Product>> {
        allProducts = db.orderProductsInfoDao().getOrderProductsLiveData(uid)
        return allProducts
    }

    fun updateProduct(product: Product) {
        db.orderProductsInfoDao().insertProduct(product)
    }

    fun cancelOrder(uid:String) {

        AsyncTask.execute{

            val order = db.orderInfoDao().getOrderInfo(uid)
            order.isCancelled = true
            order.isDelivered = true
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
            order.isCancelled = false
            db.orderInfoDao().insertOrder(order)

            syncOrders()
        }
    }

    private fun toastMessage(msg:String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getHeaders() : Map<String,String>? {

        val accessToken = PrefHelper.preferences.getString("accessToken", null) ?: return null

        val fcmToken = context.getSharedPreferences("_", Context.MODE_PRIVATE).getString("fb",null)

        if (fcmToken == null) {
            toastMessage("Токен не получен от сервера Google")
            return null
        }

        return mapOf("AccessToken" to accessToken,"FCMToken" to fcmToken)
    }

    private fun syncOrders() {

        val listData = db.orderInfoDao().getAllOrders()
        val listOrders = listData.map { it.order.apply { products = it.productsList } }

        val headers = getHeaders() ?: return

        ApiFactory.apiService.syncOrders(Orders(listOrders),headers ).enqueue(
            object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    Log.d("TEST_OF_LOADING_DATA", "Failure: ${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Toast.makeText(context, response.body().toString(), Toast.LENGTH_LONG)
                        .show()
                    loadData()
                    Log.d("TEST_OF_LOADING_DATA", "Success: $response")

                }
            }
        )

    }

    private fun loadData(srlMainView: SwipeRefreshLayout? = null) {

        val headers = getHeaders()

        if (headers == null) {
            srlMainView?.isRefreshing = false
            return
        }

        val disposable = ApiFactory.apiService.getOrders(headers)
            .map { it -> it.orders.map { it } }
            .subscribeOn(Schedulers.io())
            .doFinally{if ( srlMainView != null ) srlMainView.isRefreshing = false }
            .subscribe({ it ->

                //TODO add sync to server
                db.orderInfoDao().deleteAllOrders()
                db.orderProductsInfoDao().deleteAllProducts()

                db.orderInfoDao().insertOrders(it)
                val allProducts = it.flatMap { it.products }
                db.orderProductsInfoDao().insertProducts(allProducts)

                Log.d("TEST_OF_LOADING_DATA", "Success: $it")
            }, {
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
            })
        compositeDisposable.add(disposable)


    }

    fun refreshData(srlMainView: SwipeRefreshLayout? = null) {
        loadData(srlMainView)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

//    private fun splitDataToSection(list:List<Order>) : TreeMap<String, List<Order>>? {
//
//        val treeMap : TreeMap<String, List<Order>> = TreeMap()
//
//        val listOnDelivery:List<Order>  = list.filter { !it.isCancelled && !it.isDelivered }
//        val listOrderToReturn:List<Order>   = list.filter { it.isCancelled || it.isDelivered }
//
//        if (listOrderToReturn.isNotEmpty())
//            treeMap[context.resources.getString(R.string.order_to_return)]      = listOrderToReturn
//        if (listOnDelivery.isNotEmpty())
//            treeMap[context.resources.getString(R.string.order_on_delivery)]    = listOnDelivery
//
//
//        return treeMap
//
//    }
}