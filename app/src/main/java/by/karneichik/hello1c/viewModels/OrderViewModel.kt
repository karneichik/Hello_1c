package by.karneichik.hello1c.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import by.karneichik.hello1c.api.ApiFactory
import by.karneichik.hello1c.database.AppDatabase
import by.karneichik.hello1c.helpers.PrefHelper
import by.karneichik.hello1c.pojo.OrderWithProducts
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    val orderList = db.orderInfoDao().getOrdersList()

    fun getOrderInfo(uid: String): LiveData<OrderWithProducts> {
        return db.orderInfoDao().getOrderInfo(uid)
    }

//    init {
//        loadData()
//    }

    private fun loadData() {
        val accessToken = PrefHelper.preferences.getString("accessToken", null) ?: return
        val disposable = ApiFactory.apiService.getOrders(mapOf("AccessToken" to accessToken))
            .map { it.orders.map { it } }
            .subscribeOn(Schedulers.io())
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

    fun refreshData() {
        loadData()
    }

//    private fun getPriceListFromRawData(
//        coinPriceInfoRawData: CoinPriceInfoRawData
//    ): List<CoinPriceInfo> {
//        val result = ArrayList<CoinPriceInfo>()
//        val jsonObject = coinPriceInfoRawData.coinPriceInfoJsonObject ?: return result
//        val coinKeySet = jsonObject.keySet()
//        for (coinKey in coinKeySet) {
//            val currencyJson = jsonObject.getAsJsonObject(coinKey)
//            val currencyKeySet = currencyJson.keySet()
//            for (currencyKey in currencyKeySet) {
//                val priceInfo = Gson().fromJson(
//                    currencyJson.getAsJsonObject(currencyKey),
//                    CoinPriceInfo::class.java
//                )
//                result.add(priceInfo)
//            }
//        }
//        return result
//    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}