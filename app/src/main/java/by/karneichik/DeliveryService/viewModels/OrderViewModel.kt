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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import by.karneichik.DeliveryService.api.ApiFactory
import by.karneichik.DeliveryService.database.AppDatabase
import by.karneichik.DeliveryService.helpers.PrefHelper
import by.karneichik.DeliveryService.pojo.Order
import by.karneichik.DeliveryService.pojo.OrderWithProducts
import by.karneichik.DeliveryService.pojo.Orders
import by.karneichik.DeliveryService.pojo.Product
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlin.math.round


class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()
    private lateinit var allProducts : LiveData<List<Product>>
    private lateinit var order : LiveData<Order>
    private val context = application.applicationContext
    private val _index = MutableLiveData<Int>()
    var orderList = db.orderInfoDao().getOrdersList1()
    private var srlMainView: SwipeRefreshLayout? = null
    private lateinit var headers:Map<String,String>

    private fun cancelSRLrefreshing() {

        val mainHandler = Handler(context.mainLooper)

        val myRunnable = Runnable {
            if (srlMainView != null || srlMainView!!.isRefreshing) {
                srlMainView?.isRefreshing = false
            }
        }
        mainHandler.post(myRunnable)

    }

    fun setIndex(index: Int) {
        _index.value = index
        updateOrderList()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun toastMessage(msg:String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getHeaders() : Map<String, String>? {

        val accessToken = PrefHelper.preferences.getString("accessToken", null) //?: return null

        if (accessToken.isNullOrEmpty()) {
            toastMessage("Не заполнены настройки")
            cancelSRLrefreshing()
            return null
        }

        val fcmToken = context.getSharedPreferences("_", Context.MODE_PRIVATE).getString("fb",null)

        if (fcmToken.isNullOrEmpty()) {
            toastMessage("Токен не получен от сервера Google")
            cancelSRLrefreshing()
            return null
        }

        return mapOf("AccessToken" to accessToken,"FCMToken" to fcmToken)
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

    private fun getModifiedOrders() : Single<List<OrderWithProducts>> = db.orderInfoDao().getModifiedOrdersList()

    fun cancelOrder(uid: String? = null) {
        if (uid != null) {
            order = db.orderInfoDao().getOrderInfoLiveDate(uid)
            allProducts = db.orderProductsInfoDao().getOrderProductsLiveData(uid)
        }

        with(order.value) {
            this?.modified = true
            this?.isDelivered = true
            this?.isCancelled = true
        }

        insertAndSyncOrder()
    }

    fun saveOrder() {

        with(order.value) {
            this?.modified = true
            this?.isDelivered = true
            this?.isCancelled = false
        }

        insertAndSyncOrder()
    }

    private fun insertAndSyncOrder() {

        val orderToSent = order.value ?:return
        val productsList = allProducts.value ?:return

        AsyncTask.execute {
            db.orderInfoDao().insertOrder(orderToSent)
            if (orderToSent.isCancelled) {
                for (product in productsList) {
                    if (product.delivered) {
                        product.delivered = false
                        db.orderProductsInfoDao().insertProduct(product)
                    }
                }
            }
        }

        orderToSent.apply { products = productsList }

        sendData(orderToSent)
    }

    fun splitProduct(product: Product,newCount:Int) {
        splitProductPrivate(product,newCount)
    }

    fun refreshData(srlMainView: SwipeRefreshLayout? = null) {
        this.srlMainView = srlMainView
        updateOrders()
    }

    fun updateToken(){

        headers = getHeaders() ?: return

        val disposable = ApiFactory.apiService.updateToken(headers)
            .subscribeOn(Schedulers.io())
            .subscribe{ message ->
                toastMessage(message)
            }

        compositeDisposable.add(disposable)

    }

    fun recalculateTotal() {

        val products = allProducts.value
        val filteredList = products?.filter { it.delivered}
        order.value?.totalsum = filteredList?.sumByDouble { it.sum } ?:0.0
        if (order.value != null) {
            db.orderInfoDao().insertOrder(order.value as Order)
        }
    }

    private fun splitProductPrivate(product: Product,newCount:Int) {
        val newProduct = product.copy(
            count = product.count - newCount,
            id = null,
            sum = round(100 * product.price * (product.count - newCount)) / 100
        )
        product.count = newCount
        product.sum = round(100 * product.price * newCount) / 100

        AsyncTask.execute {
            db.orderProductsInfoDao().insertProduct(newProduct)
            db.orderProductsInfoDao().insertProduct(product)
        }

    }

    private fun updateOrders() {

        headers = getHeaders() ?: return

        val disposable =  getModifiedOrders()
            .subscribeOn(Schedulers.io())
            .subscribe({ modifiedOrders ->

                val listOrders = modifiedOrders.map { owp ->
                    owp.order.apply {
                        products = owp.productsList
                    }
                }
                sendData(listOrders)

            }, {
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
                toastMessage("Обмен данными: ${it.message}")
            })



        compositeDisposable.add(disposable)

    }

    private fun updateOrderList(){
        orderList = when (_index.value) {
            0-> db.orderInfoDao().getOrdersList1()
            1-> db.orderInfoDao().getOrdersList2()
            2-> db.orderInfoDao().getOrdersList3()
            else -> db.orderInfoDao().getOrdersList1()
        }
    }

    private fun updateOrdersDB(listOrders: List<Order>) {

        db.orderInfoDao().deleteAllOrders()
        db.orderProductsInfoDao().deleteAllProducts()

        db.orderInfoDao().insertOrders(listOrders)
        val allProducts = listOrders.flatMap { it.products }
        db.orderProductsInfoDao().insertProducts(allProducts)

        updateOrderList()
    }

    private fun onReceiveOrders(orders: List<Order>) {
        orders.apply {
            this.forEach {order ->
                order.modified = false
                val filteredList= order.products.filter { it.delivered}
                order.totalsum = filteredList.sumByDouble { it.sum }
            }
        }
        updateOrdersDB(orders)
        Log.d("TEST_OF_LOADING_DATA", "Success: $orders")
        toastMessage("Обмен данными: ОК!")
    }

    private fun onReceiveOrdersError(error:Throwable){
        Log.d("TEST_OF_LOADING_DATA", "Failure: ${error.message}")
        toastMessage("Обмен данными: ${error.message}")
    }


    private fun sendData(
        listOrders: List<Order>? = null) {

        headers = getHeaders() ?: return

        val disposable : Disposable

        if (listOrders.isNullOrEmpty()) {
            disposable = ApiFactory.apiService.getOrders(headers)
            .map { it.orders.map { it } }
            .subscribeOn(Schedulers.io())
            .subscribe({
                onReceiveOrders(it)
                cancelSRLrefreshing()
            }, {
                onReceiveOrdersError(it)
                cancelSRLrefreshing()
            })
        } else {
            disposable = ApiFactory.apiService.syncOrders(Orders(listOrders),headers )
            .subscribeOn(Schedulers.io())
            .map { it -> it.orders.map { it } }
            .subscribe({
                onReceiveOrders(it)
                cancelSRLrefreshing()
            },{
                onReceiveOrdersError(it)
                cancelSRLrefreshing()
            })
        }

        compositeDisposable.add(disposable)
    }

    private fun sendData( order: Order?) {
        if (order != null) sendData(listOf(order))
    }

}

