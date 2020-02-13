package by.karneichik.hello1c.api

import by.karneichik.hello1c.pojo.Orders
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.HeaderMap


interface ApiService {


    @GET("hs/delivery/get_orders")
    fun getOrders(@HeaderMap headers: Map<String, String> )
            : Single<Orders>

    companion object {
//        private val HEADERS = mapOf("AccessToken" to "007")

    }

}