package by.karneichik.hello1c.pojo

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "orders")
data class Order (

    @PrimaryKey @SerializedName("uid") @Expose val uid : String,
    @SerializedName("number") @Expose val number : String,
    @SerializedName("client_fio") @Expose val client_fio : String,
    @SerializedName("client_phone") @Expose val client_phone : String,
    @SerializedName("address") @Expose val address : String,
    @SerializedName("totalsum") @Expose val totalsum : Double,
    @SerializedName("payform") @Expose val payform : String,
    @SerializedName("time") @Expose val time : String
//    @SerializedName("products") @Expose val products : List<Product>
)