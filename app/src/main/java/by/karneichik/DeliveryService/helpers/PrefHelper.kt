package by.karneichik.DeliveryService.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.Preference
import androidx.preference.PreferenceManager

class PrefHelper {

    companion object {

        lateinit var preferences : SharedPreferences
        lateinit var myPrefs : Preference
//        private const val MODE = Context.MODE_PRIVATE

        fun init(context: Context) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context)
            myPrefs = Preference(context)
        }
    }
}