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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.roundToLong


class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()
    private lateinit var allProducts : LiveData<List<Product>>
    private lateinit var order : LiveData<Order>
    private val context = application.applicationContext
    private val _index = MutableLiveData<Int>()
    var orderList = db.orderInfoDao().getOrdersList1()
    private var srlMainView: SwipeRefreshLayout? = null

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
        updateOrderAndSync(uid,isCancelled = true, isDelivered = true)
    }

    fun saveOrder(uid:String) {
        updateOrderAndSync(uid,isCancelled = false, isDelivered = true)
    }

    fun splitProduct(product: Product,newCount:Int) {
        splitProductPrivate(product,newCount)
    }

    fun refreshData(srlMainView: SwipeRefreshLayout? = null) {
        this.srlMainView = srlMainView
        loadData()
    }

    fun updateToken(){

        val headers = getHeaders() ?: return

        val disposable = ApiFactory.apiService.updateToken(headers)
            .subscribeOn(Schedulers.io())
            .subscribe{ it ->
                toastMessage(it)
            }

        compositeDisposable.add(disposable)

    }

    private fun splitProductPrivate(product: Product,newCount:Int) {
        val newProduct = product.copy(
            count = product.count - newCount,
            id = null,
            sum = round(100 * product.price * (product.count - newCount)) / 100
        )
        product.count = newCount
        product.sum = round(100 * product.price * newCount) / 100


        var disposable = db.orderProductsInfoDao().insertProduct(newProduct)
            .subscribeOn(Schedulers.single())
            .subscribe()
        compositeDisposable.add(disposable)
        disposable = db.orderProductsInfoDao().insertProduct(product)
            .subscribeOn(Schedulers.single())
            .subscribe()
        compositeDisposable.add(disposable)


    }

    private fun getDataFromServer(headers:Map<String,String>) {

        val disposable = ApiFactory.apiService.getOrders(headers)
            .map { it -> it.orders.map { it } }
            .subscribeOn(Schedulers.single())
            .doFinally{srlMainView?.isRefreshing = false }
            .subscribe({ it ->

                db.orderProductsInfoDao().deleteAllProducts()

                db.orderInfoDao().insertOrders(it)
                val allProducts = it.flatMap { it.products }
                db.orderProductsInfoDao().insertProducts(allProducts)

                orderList = when (_index.value) {
                    0-> db.orderInfoDao().getOrdersList1()
                    1-> db.orderInfoDao().getOrdersList2()
                    2-> db.orderInfoDao().getOrdersList3()
                    else -> db.orderInfoDao().getOrdersList1()
                }

                Log.d("TEST_OF_LOADING_DATA", "Success: $it")
                toastMessage("Получение данных: ОК!")
            }, {
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
                toastMessage("Получение данных: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    private fun sendData(listOrders: List<Order>,headers:Map<String,String>) {

        val disposable = ApiFactory.apiService.syncOrders(Orders(listOrders),headers )
            .subscribeOn(Schedulers.single())
            .subscribe({
                toastMessage("Отправка данных: $it")
                Log.d("TEST_OF_LOADING_DATA", "Success: $it")
                getDataFromServer(headers)
            },{
                toastMessage("Отправка данных: ${it.message}")
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
                srlMainView?.isRefreshing = false
            })

        compositeDisposable.add(disposable)
    }

    private fun syncOrders(headers:Map<String,String>) {

        val disposable = db.orderInfoDao().getAllOrders()
            .subscribeOn(Schedulers.single())
            .subscribe{ listData ->
                if (listData.isEmpty()) return@subscribe

                val listOrders = listData.map { it.order.apply { products = it.productsList } }

                sendData(listOrders,headers)

            }
        compositeDisposable.add(disposable)

    }

    private fun loadData() {

        val headers = getHeaders()

        if (headers == null) {
            srlMainView?.isRefreshing = false
            return
        }

        syncOrders(headers)

    }

    private fun updateOrderAndSync (uid: String,isCancelled:Boolean,isDelivered:Boolean) {

        val disposable = db.orderInfoDao().getOrderInfo(uid)
            .map { it -> it.apply {
                it.isCancelled = isCancelled
                it.isDelivered = isDelivered
            }}
            .subscribeOn(Schedulers.single())
            .doAfterSuccess { loadData() }
            .subscribe { it ->

                db.orderInfoDao().insertOrder(it)
                val products = db.orderProductsInfoDao().getOrderProductsList(uid)

                if (isCancelled) {
                    for (product in products) product.delivered = false
                    db.orderProductsInfoDao().insertProducts(products)
                }

            }
        compositeDisposable.add(disposable)
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

    private fun getHeaders() : Map<String,String>? {

        val accessToken = PrefHelper.preferences.getString("accessToken", null) ?: return null

        val fcmToken = context.getSharedPreferences("_", Context.MODE_PRIVATE).getString("fb",null)

        if (fcmToken == null) {
            toastMessage("Токен не получен от сервера Google")
            return null
        }

        return mapOf("AccessToken" to accessToken,"FCMToken" to fcmToken)
    }

}