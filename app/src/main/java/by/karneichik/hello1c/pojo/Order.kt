package by.karneichik.hello1c.pojo

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Constructor
import java.util.Arrays.asList


@Entity(tableName = "orders")
data class Order (

    @PrimaryKey @SerializedName("uid") @Expose var uid : String,
    @SerializedName("number") @Expose var number : String,
    @SerializedName("client_fio") @Expose var client_fio : String,
    @SerializedName("client_phone") @Expose var client_phone : String,
    @SerializedName("address") @Expose var address : String,
    @SerializedName("totalsum") @Expose var totalsum : Double,
    @SerializedName("payform") @Expose var payform : String,
    @SerializedName("time") @Expose var time : String,
    @SerializedName("isDelivered") @Expose var isDelivered : Boolean,
    @SerializedName("isCancelled") @Expose var isCancelled : Boolean,
    @Ignore @SerializedName("products") @Expose var products : List<Product>

)
{constructor(): this("", "","","","",0.0,"","", false, false,listOf())}

@Entity(tableName = "orderProducts")
//@Entity(tableName = "orderProducts",
//    foreignKeys = [
//        ForeignKey(entity = Product::class,
//            parentColumns = ["uid"],
//            childColumns = ["uid"],
//            onDelete = ForeignKey.CASCADE)])
data class Product (

    @PrimaryKey(autoGenerate = true) val id : Int,
    @SerializedName("uid") @Expose val uid : String,
    @SerializedName("goods")@Expose val goods : String,
    @SerializedName("serial")@Expose val serial : String,
    @SerializedName("delivered")@Expose var delivered : Boolean,
    @SerializedName("price")@Expose val price : Double,
    @SerializedName("sum")@Expose val sum : Double,
    @SerializedName("count")@Expose val count : Int
)

data class OrderWithProducts (
    @Embedded val order: Order,
    @Relation(
        parentColumn = "uid",
        entity = Product::class,
        entityColumn = "uid")
    val productsList: List<Product>


)