package by.karneichik.DeliveryService.database


import androidx.lifecycle.LiveData
import androidx.room.*
import by.karneichik.DeliveryService.pojo.Order
import by.karneichik.DeliveryService.pojo.OrderWithProducts
import by.karneichik.DeliveryService.pojo.Product


@Dao
interface OrderInfoDao {
    @Query("SELECT * FROM orders ORDER BY number DESC")
    fun getOrdersList(): LiveData<List<Order>>

    @Transaction
    @Query("SELECT * FROM orders WHERE uid == :uid")
    fun getOrderWithProductsInfo(uid: String): LiveData<OrderWithProducts>

    @Query("SELECT * FROM orders WHERE uid == :uid")
    fun getOrderInfoLiveDate(uid: String): LiveData<Order>

    @Query("SELECT * FROM orders WHERE uid == :uid")
    fun getOrderInfo(uid: String): Order

    @Transaction
    @Query("SELECT * FROM orders")
    fun getAllOrders(): List<OrderWithProducts>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orderList: List<Order>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(orderList: Order)

    @Query("DELETE FROM orders")
    fun deleteAllOrders()
}

@Dao
interface OrderProductsInfoDao {

    @Query("SELECT * FROM orderProducts WHERE uid == :uid")
    fun getOrderProductsLiveData(uid: String): LiveData<List<Product>>

    @Query("SELECT * FROM orderProducts WHERE uid == :uid")
    fun getOrderProductsList(uid: String): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(productList: List<Product>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(productList: Product)

    @Query("DELETE FROM orderProducts")
    fun deleteAllProducts()
}
