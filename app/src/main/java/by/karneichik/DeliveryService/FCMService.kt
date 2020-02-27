package by.karneichik.DeliveryService

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import by.karneichik.DeliveryService.api.ApiFactory
import by.karneichik.DeliveryService.viewModels.OrderViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FCMService : FirebaseMessagingService() {

    private val TAG = "TEST_OF_LOADING_DATA"

    override fun onMessageReceived(remoteMessage: RemoteMessage) { // ...

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.from)
        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            remoteMessage.data["uid"]?.let { OrderViewModel(application).cancelOrder(it) }
        }
        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body)
        }
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        getSharedPreferences("_", Context.MODE_PRIVATE).edit().putString("fb", token)
            .apply()

        Log.d(TAG, "New token $token")

        val accessToken = PreferenceManager.getDefaultSharedPreferences(this).getString("accessToken", null) ?: return

        ApiFactory.apiService.updateToken(mapOf("AccessToken" to accessToken,"FCMToken" to token)).enqueue(
            object:Callback<String>{
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, "Message data payload: ${t.message}")
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    Log.d(TAG, "Message data payload: ${response.body()}")
                }
            }
        )

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token)


    }


}
