package by.karneichik.hello1c.database


import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.karneichik.hello1c.pojo.Order
import by.karneichik.hello1c.pojo.Product


@Dao
interface OrderInfoDao {
    @Query("SELECT * FROM orders ORDER BY number DESC")
    fun getOrdersList(): LiveData<List<Order>>

    @Query("SELECT * FROM orders WHERE uid == :uid LIMIT 1")
    fun getOrderInfo(uid: String): LiveData<Order>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orderList: List<Order>)
}

//@Dao
//interface OrderProductsInfoDao {
//
//    @Query("SELECT * FROM orderProducts WHERE uid == :uid")
//    fun getOrderProducts(uid: String): LiveData<List<Product>>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertProducts(orderList: List<Order>)
//}
