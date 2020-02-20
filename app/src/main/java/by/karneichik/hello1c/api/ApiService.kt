package by.karneichik.hello1c.api

import by.karneichik.hello1c.pojo.Orders
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST


interface ApiService {


    @GET("hs/delivery/get_orders")
    fun getOrders(@HeaderMap headers: Map<String, String>)
            : Single<Orders>

    @POST("hs/delivery/get_orders")
    fun syncOrders(@Body orders: Orders) : Call<String>
//    fun syncOrders(@Body orders: String) : Call<String>

    companion object {
//        private val HEADERS = mapOf("AccessToken" to "007")

    }

}