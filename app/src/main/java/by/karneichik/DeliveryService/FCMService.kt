package by.karneichik.DeliveryService

import android.content.Context
import android.util.Log
import by.karneichik.DeliveryService.viewModels.OrderViewModel
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


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

        OrderViewModel(application).updateToken()
    }


}
