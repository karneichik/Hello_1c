package by.karneichik.DeliveryService.api


import by.karneichik.DeliveryService.helpers.PrefHelper
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private val BASE_URL = PrefHelper.preferences.getString("API_URL",null) ?:""

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(if (BASE_URL.endsWith("/")) { BASE_URL } else {
            "$BASE_URL/"
        } )
        .build()

    val apiService = retrofit.create(ApiService::class.java)



}