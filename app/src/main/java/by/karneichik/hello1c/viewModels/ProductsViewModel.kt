package by.karneichik.hello1c.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import by.karneichik.hello1c.api.ApiFactory
import by.karneichik.hello1c.database.AppDatabase
import by.karneichik.hello1c.helpers.PrefHelper
import by.karneichik.hello1c.pojo.OrderWithProducts
import by.karneichik.hello1c.pojo.Product
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val allProducts : LiveData<List<Product>>? = null
    private val compositeDisposable = CompositeDisposable()

    fun getProductsList(uid: String): LiveData<List<Product>> {
        allProducts?.let { return it }
        return db.orderProductsInfoDao().getOrderProducts(uid)
    }

    fun getOrderInfo(uid: String): LiveData<OrderWithProducts> {
        return db.orderInfoDao().getOrderInfo(uid)
    }

    fun update(product: Product) {
        db.orderProductsInfoDao().insertProduct(product)
    }

//    private fun updateData(product:Product) {
//        val disposable = db.orderProductsInfoDao().insertProduct(product)
//
//
//
//
////            //ApiFactory.apiService.getOrders(mapOf("AccessToken" to accessToken))
////            .map { it.orders.map { it } }
////            .subscribeOn(Schedulers.io())
////            .subscribe({ it ->
////
////                db.orderInfoDao().deleteAllOrders()
////                db.orderProductsInfoDao().deleteAllProducts()
////
////                db.orderInfoDao().insertOrders(it)
////                val allProducts = it.flatMap { it.products }
////                db.orderProductsInfoDao().insertProducts(allProducts)
////                Log.d("TEST_OF_LOADING_DATA", "Success: $it")
////            }, {
////                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
////            })
//        compositeDisposable.add(disposable)
//    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
