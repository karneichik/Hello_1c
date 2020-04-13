package by.karneichik.DeliveryService.api

import by.karneichik.DeliveryService.pojo.Orders
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST


interface ApiService {

    @GET("hs/delivery/get_orders")
    fun getOrders(@HeaderMap headers: Map<String, String>)
            : Single<Orders>

    @POST("hs/delivery/get_orders")
    fun syncOrders(
        @Body orders: Orders,
        @HeaderMap headers: Map<String, String>
    ) : Single<Orders>

    @POST("hs/delivery/update_token")
    fun updateToken(@HeaderMap headers: Map<String, String>): Single<String>

}