package com.zoomore.reelapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.zoomore.reelapp.MainActivity
import com.zoomore.reelapp.R

object BottomNavViewUtils {

    fun showBottomNavBar(activity: Activity?) = changeVisibility(activity, shouldShow = true)
    fun hideBottomNavBar(activity: Activity?) = changeVisibility(activity, shouldShow = false)

    fun changeVisibility(activity: Activity?, shouldShow: Boolean) {
        val bottomNavigationView = (activity as? MainActivity)?.binding?.navView
        bottomNavigationView?.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    /**
     * Changes bottom nav color. When useWhiteBar is true, the background should be white,
     * the icons dark and pink(selected). Otherwise, the background should be dark,
     * the icons white and pink(selected)
     *
     * @param systemBarColors whether the navView background color is white or not
     */
    @SuppressLint("ResourceType")
    fun changeNavBarColor(activity: Activity?, systemBarColors: SystemBarColors) {
        val navView = (activity as MainActivity).binding.navView

        val backgroundRes =
            if (systemBarColors == SystemBarColors.WHITE) android.R.color.white
            else R.color.dark_black

        val iconRes =
            if (systemBarColors == SystemBarColors.WHITE) R.drawable.dark_nav_icon_tint
            else R.drawable.white_nav_icon_tint

        val iconTint = ResourcesCompat.getColorStateList(activity.resources, iconRes, null)

        navView.setBackgroundColor(backgroundRes)
        navView.itemIconTintList = iconTint
        navView.itemTextColor = iconTint
    }
}