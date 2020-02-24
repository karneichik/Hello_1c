package by.karneichik.DeliveryService.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import by.karneichik.DeliveryService.R

class TabsPagerAdapter (private val context: Context, fm: FragmentManager): FragmentPagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val TAB_TITLES = arrayOf(R.string.order_on_delivery, R.string.order_to_return, R.string.order_delivered)

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position]);
    }

    override fun getItem(position: Int): Fragment {
        return OrdersFragment(position)
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }

}