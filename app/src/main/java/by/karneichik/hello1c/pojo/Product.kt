package by.karneichik.hello1c.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "orderProducts")
data class Product (

    @PrimaryKey @SerializedName("uid") @Expose val uid : String,
    @SerializedName("goods")@Expose val goods : String,
    @SerializedName("serial")@Expose val serial : String,
    @SerializedName("delivered")@Expose val delivered : Boolean,
    @SerializedName("price")@Expose val price : Double,
    @SerializedName("sum")@Expose val sum : Double,
    @SerializedName("count")@Expose val count : Int
)