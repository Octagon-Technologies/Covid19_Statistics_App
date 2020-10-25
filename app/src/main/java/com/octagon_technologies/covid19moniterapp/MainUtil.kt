package com.octagon_technologies.covid19moniterapp

import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.octagon_technologies.covid19moniterapp.databinding.ActivityMainBinding
import com.octagon_technologies.covid19moniterapp.databinding.FragmentSettingsFragmentBinding
import com.octagon_technologies.covid19moniterapp.main_activity.MainActivity

const val DEVELOPER_EMAIL = "tech.octagontechnologies@gmail.com"

enum class Status { LOADING, LOCATION_ERROR, NETWORK_ERROR, DONE }
enum class Theme { LIGHT, DARK }
enum class IconType {Navigation, Search, ArrowDown, Menu, Settings, BackBtn, Checked}

fun getActualIconFromTheme(iconType: IconType, theme: Theme?): Int {
    return when (iconType) {
        IconType.Navigation -> if (theme == Theme.LIGHT) R.drawable.ic_near_me_black else R.drawable.ic_near_me_white
        IconType.Settings -> if (theme == Theme.LIGHT) R.drawable.ic_settings_black else R.drawable.ic_settings
        IconType.Search -> if (theme == Theme.LIGHT) R.drawable.ic_search_black else R.drawable.ic_search_white
        IconType.ArrowDown -> if (theme == Theme.LIGHT) R.drawable.ic_arrow_downward_black else R.drawable.ic_arrow_downward_white
        IconType.Menu -> if (theme == Theme.LIGHT) R.drawable.ic_menu_black else R.drawable.ic_menu_white
        IconType.BackBtn -> if (theme == Theme.LIGHT) R.drawable.ic_arrow_back_dark else R.drawable.ic_arrow_back_light
        IconType.Checked -> if (theme == Theme.LIGHT) R.drawable.ic_check_circle_black else R.drawable.ic_check_circle_white
    }
}

fun FragmentSettingsFragmentBinding.openOrCloseMenuItem() {
    changeThemeBtn.setOnClickListener {
        if (themeLayout.visibility == View.VISIBLE) {
            themeLayout.visibility = View.GONE
        } else {
            themeLayout.visibility = View.VISIBLE
        }
    }
}

fun FragmentSettingsFragmentBinding.changeMenuLayoutVisibility(binding: ActivityMainBinding) {
    root.apply {
        visibility = if (visibility == View.VISIBLE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.navView.visibility = View.VISIBLE
            }
            View.GONE
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.navView.visibility = View.GONE
            }
            View.VISIBLE
        }
    }
}

fun Fragment.removeToolbarAndBottomNav() {
    val mainActivity = (this.activity as MainActivity)
    val gone = View.GONE

    mainActivity.binding.apply {
        topToolbar.visibility = gone
        navView.visibility = gone
        toolbarBottomLine.visibility = gone
    }
}

fun Fragment.addToolbarAndBottomNav() {
    val mainActivity = (this.activity as MainActivity)
    val visible = View.VISIBLE

    mainActivity.binding.apply {
        topToolbar.visibility = visible
        navView.visibility = visible
        toolbarBottomLine.visibility = visible
    }
}

fun Fragment.changeStatusBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        (activity as MainActivity).apply {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

            window.statusBarColor =
                ResourcesCompat.getColor(resources, color, null)

            // 0 == white icons
            // View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR == black icons

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.decorView.systemUiVisibility = when (color) {
                    R.color.dark_black -> 0
                    R.color.color_black -> 0
                    R.color.line_grey -> View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    android.R.color.white -> View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    else -> throw RuntimeException("Unexpected color: $color")
                }
            }
        }
    }
}