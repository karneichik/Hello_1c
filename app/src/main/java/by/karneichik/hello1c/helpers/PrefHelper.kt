package by.karneichik.hello1c.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PrefHelper {

    companion object {

        lateinit var preferences : SharedPreferences
//        private const val MODE = Context.MODE_PRIVATE

        fun init(context: Context) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context)
        }
    }
}