package by.karneichik.hello1c.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import by.karneichik.hello1c.pojo.Order
import by.karneichik.hello1c.pojo.Product

@Database(entities = [Order::class, Product::class], version = 9, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    companion object {

        private var db: AppDatabase? = null
        private const val DB_NAME = "main3.db"
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        DB_NAME
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                db = instance
                return instance
            }
        }
    }

    abstract fun orderInfoDao(): OrderInfoDao
    abstract fun orderProductsInfoDao(): OrderProductsInfoDao
}
