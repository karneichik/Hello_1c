package by.karneichik.DeliveryService.api


import by.karneichik.DeliveryService.helpers.PrefHelper
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ApiFactory {

    private val BASE_URL = PrefHelper.preferences.getString("API_URL",null) ?:""

    private fun getClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10,TimeUnit.SECONDS)
            .readTimeout(20,TimeUnit.SECONDS)
            .build()
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(getClient())
        .baseUrl(if (BASE_URL.endsWith("/")) { BASE_URL } else {
            "$BASE_URL/"
        } )
        .build()

    val apiService = retrofit.create(ApiService::class.java)

}