package by.karneichik.hello1c.database


import androidx.lifecycle.LiveData
import androidx.room.*
import by.karneichik.hello1c.pojo.Order
import by.karneichik.hello1c.pojo.OrderWithProducts
import by.karneichik.hello1c.pojo.Product


@Dao
interface OrderInfoDao {
    @Query("SELECT * FROM orders ORDER BY number DESC")
    fun getOrdersList(): LiveData<List<Order>>

    @Transaction
    @Query("SELECT * FROM orders WHERE uid == :uid")
    fun getOrderInfo(uid: String): LiveData<OrderWithProducts>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orderList: List<Order>)
}

@Dao
interface OrderProductsInfoDao {

    @Query("SELECT * FROM orderProducts WHERE uid == :uid")
    fun getOrderProducts(uid: String): LiveData<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(productList: List<Product>)

    @Query("DELETE FROM orderProducts")
    fun deleteAllProducts()
}
