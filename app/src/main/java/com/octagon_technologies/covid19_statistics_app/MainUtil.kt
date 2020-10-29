package com.octagon_technologies.covid19_statistics_app

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.octagon_technologies.covid19_statistics_app.databinding.ActivityMainBinding
import com.octagon_technologies.covid19_statistics_app.databinding.FragmentSettingsFragmentBinding
import com.octagon_technologies.covid19_statistics_app.main_activity.MainActivity
import com.octagon_technologies.covid19_statistics_app.ui.allCountries.AllCountriesFragment
import com.octagon_technologies.covid19_statistics_app.ui.currentCountry.CurrentCountryFragment
import timber.log.Timber

const val DEVELOPER_EMAIL = "tech.octagontechnologies@gmail.com"

enum class Status { LOADING, NETWORK_ERROR, DONE }
enum class Theme { LIGHT, DARK }
enum class IconType { Navigation, ArrowDown, Menu, Settings, BackBtn, Checked, AddItem }

fun getActualIconFromTheme(iconType: IconType, theme: Theme?): Int {
    return when (iconType) {
        IconType.Navigation -> if (theme == Theme.LIGHT) R.drawable.ic_near_me_black else R.drawable.ic_near_me_white
        IconType.Settings -> if (theme == Theme.LIGHT) R.drawable.ic_settings_black else R.drawable.ic_settings
        IconType.ArrowDown -> if (theme == Theme.LIGHT) R.drawable.ic_arrow_downward_black else R.drawable.ic_arrow_downward_white
        IconType.Menu -> if (theme == Theme.LIGHT) R.drawable.ic_menu_black else R.drawable.ic_menu_white
        IconType.BackBtn -> if (theme == Theme.LIGHT) R.drawable.ic_arrow_back_dark else R.drawable.ic_arrow_back_light
        IconType.Checked -> if (theme == Theme.LIGHT) R.drawable.ic_check_circle_black else R.drawable.ic_check_circle_white
        IconType.AddItem -> if (theme == Theme.LIGHT) R.drawable.ic_add_circle_outline_black else R.drawable.ic_add_circle_outline_white
    }
}

fun Context.isNetworkConnected(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val info = connectivityManager.activeNetworkInfo
    val isConnected = info != null && info.isConnectedOrConnecting
    Timber.d("isConnected is $isConnected")

    return isConnected
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun Fragment.getNetworkListener(): ConnectivityManager.NetworkCallback {
            return object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    Timber.d("network is available as $network")

                    when (this@getNetworkListener) {
                        is CurrentCountryFragment -> {
                                viewModel.loadData(requireContext(), settingsViewModel.location.value)
                        }
                        is AllCountriesFragment -> {
                                viewModel.getAllCountries()
                        }
                    }

                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    Timber.d("maxMsToLive is $maxMsToLive in onLosing")
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    Timber.d("onLost called")
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    Timber.d("onUnavailable called")
                }
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