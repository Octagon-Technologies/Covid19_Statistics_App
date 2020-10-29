package com.octagon_technologies.covid19_statistics_app

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.octagon_technologies.covid19_statistics_app.main_activity.MainViewModel
import org.joda.time.DateTime
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@BindingAdapter("getStatusIcon", "addTheme")
fun ImageView.getStatusIcon(status: Status?, theme: Theme?) {
    visibility = if (status == Status.DONE) View.GONE else View.VISIBLE
    setBackgroundColor(
        ResourcesCompat.getColor(
            resources,
            if (theme == Theme.LIGHT) android.R.color.white else R.color.dark_black,
            null
        )
    )
    Timber.d("status changed to $status")

    setImageResource(
        when (status) {
            Status.NETWORK_ERROR -> {
                if (theme == Theme.LIGHT) R.drawable.ic_connection_error_black else R.drawable.ic_connection_error_white
            }
            else -> R.drawable.loading_animation
        }
    )
}


@BindingAdapter("setDate")
fun TextView.setDate(date: String?) {
    val jodaDate = DateTime(date).toLocalDate().toDate()
    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(jodaDate)
}

@BindingAdapter("changeLightBackground", "addLightViewModel")
fun Button.changeLightBackground(userTheme: Theme?, viewModel: MainViewModel) {
    val isSelected = Theme.LIGHT == userTheme
    setTextColor(ResourcesCompat.getColor(resources, android.R.color.white, null))

    if (isSelected) {
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.color_black, null))
    } else {
        setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.transparent, null))
    }

    setOnClickListener {
        viewModel.changeThemeToWhite()
        setTextColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.color_black, null))
    }
}


@BindingAdapter("changeDarkBackground", "addDarkViewModel")
fun Button.changeDarkBackground(userTheme: Theme?, viewModel: MainViewModel) {
    val isSelected = Theme.DARK == userTheme

    if (isSelected) {
        setTextColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.color_black, null))
    } else {
        setTextColor(ResourcesCompat.getColor(resources, R.color.dark_black, null))
        setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.transparent, null))
    }

    setOnClickListener {
        viewModel.changeThemeToDark()
        setTextColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
        setBackgroundColor(ResourcesCompat.getColor(resources, R.color.color_black, null))
    }
}


@BindingAdapter("getImageIcon", "iconType")
fun View.getImageIcon(theme: Theme?, iconType: IconType) {
    if (this is ImageView) {
        Timber.d("This is a imageView with type as $iconType")
        setImageResource(
            getActualIconFromTheme(iconType, theme)
        )
    } else if (this is Button) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val drawableLeft = ResourcesCompat.getDrawable(
                resources,
                getActualIconFromTheme(iconType, theme),
                null
            )
            Timber.d("This is a button with type as $iconType")
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawableLeft,
                null,
                null,
                null
            )
        } else setCompoundDrawablesWithIntrinsicBounds(
            getActualIconFromTheme(iconType, theme),
            0,
            0,
            0
        )
    } else if (this is TextView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val drawableRight = ResourcesCompat.getDrawable(
                resources,
                getActualIconFromTheme(iconType, theme),
                null
            )
            Timber.d("This is a button with type as $iconType")
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                drawableRight,
                null
            )
        } else setCompoundDrawablesWithIntrinsicBounds(
            0,
            0,
            getActualIconFromTheme(iconType, theme),
            0
        )
    }
}

@SuppressLint("ResourceType")
@BindingAdapter("changeBottomNavTint")
fun BottomNavigationView.changeBottomNavTint(theme: Theme?) {

    when (theme) {
        Theme.LIGHT -> {
            setBackgroundColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
            itemIconTintList =
                ResourcesCompat.getColorStateList(resources, R.drawable.bottom_nav_item_tint, null)
            itemTextColor =
                ResourcesCompat.getColorStateList(resources, R.drawable.bottom_nav_item_tint, null)
        }
        Theme.DARK -> {
            setBackgroundColor(ResourcesCompat.getColor(resources, R.color.color_black, null))
            itemIconTintList = ResourcesCompat.getColorStateList(
                resources,
                R.drawable.dark_bottom_nav_item_tint,
                null
            )
            itemTextColor = ResourcesCompat.getColorStateList(
                resources,
                R.drawable.dark_bottom_nav_item_tint,
                null
            )
        }
    }

    Timber.d("itemIconTintList is $itemIconTintList")
}