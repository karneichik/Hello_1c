package by.karneichik.hello1c.pojo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class Orders (
    @SerializedName("orders") @Expose val orders : List<Order>
)
