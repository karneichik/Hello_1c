package by.karneichik.hello1c.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import by.karneichik.hello1c.database.AppDatabase
import by.karneichik.hello1c.pojo.OrderWithProducts
import by.karneichik.hello1c.pojo.Product

class ProductsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val allProducts : LiveData<List<Product>>? = null

    fun getProductsList(uid: String): LiveData<List<Product>> {
        allProducts?.let { return it }
        return db.orderProductsInfoDao().getOrderProducts(uid)
    }

    fun getOrderInfo(uid: String): LiveData<OrderWithProducts> {
        return db.orderInfoDao().getOrderInfo(uid)
    }

}
