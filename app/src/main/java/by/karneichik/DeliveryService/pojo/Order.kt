package by.karneichik.DeliveryService.pojo

import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


@Entity(tableName = "orders")
data class Order (

    @PrimaryKey @SerializedName("uid") @Expose var uid : String,
    @SerializedName("number") var number : String,
    @SerializedName("client_fio") var client_fio : String,
    @SerializedName("client_phone") var client_phone : String,
    @SerializedName("address") var address : String,
    @SerializedName("totalsum") var totalsum : Double,
    @SerializedName("payform") var payform : String,
    @SerializedName("time") var time : String,
    @SerializedName("comment") var comment : String,
    @SerializedName("isDelivered") @Expose var isDelivered : Boolean,
    @SerializedName("isCancelled") @Expose var isCancelled : Boolean,
    @Ignore @SerializedName("products") @Expose var products : List<Product>

)
{constructor(): this("", "","","","",0.0,"","", "",false, false,listOf())}

@Entity(tableName = "orderProducts")
//@Entity(tableName = "orderProducts",
//    foreignKeys = [
//        ForeignKey(entity = Product::class,
//            parentColumns = ["uid"],
//            childColumns = ["uid"],
//            onDelete = ForeignKey.CASCADE)])
data class Product (

    @PrimaryKey(autoGenerate = true) var id : Int?,
    @SerializedName("uid") @Expose val uid : String,
    @SerializedName("good_uid") @Expose val good_uid : String,
    @SerializedName("goods")@Expose val goods : String,
    @SerializedName("serial")@Expose val serial : String,
    @SerializedName("delivered")@Expose var delivered : Boolean,
    @SerializedName("price")@Expose val price : Double,
    @SerializedName("sum")@Expose var sum : Double,
    @SerializedName("count")@Expose var count : Int
)

data class OrderWithProducts (
    @Embedded val order: Order,
    @Relation(
        parentColumn = "uid",
        entity = Product::class,
        entityColumn = "uid")
    val productsList: List<Product>


)