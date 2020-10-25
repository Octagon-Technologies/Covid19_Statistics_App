package com.octagon_technologies.covid19moniterapp.ui.allCountries

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.Theme
import com.octagon_technologies.covid19moniterapp.network.allCountries.EachCountry
import com.octagon_technologies.covid19moniterapp.network.allCountries.Global
import com.octagon_technologies.covid19moniterapp.network.allCountries.Premium
import com.octagon_technologies.covid19moniterapp.ui.allCountries.each_country_item.EachCountryItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

@BindingAdapter("loadCountryFlag")
fun ImageView.loadCountryFlag(countryCode: String?) {
    val url = resources.getString(R.string.country_flag_url, countryCode)
    Glide.with(context)
        .load(if (countryCode == "GLO") R.drawable.ic_public else url)
        .apply(
            RequestOptions()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
        )
        .centerCrop()
        .into(this)
}

@BindingAdapter("addCustomTextListener", "addTheme", "addGroupAdapter")
fun EditText.addCustomTextListener(
    liveGlobal: Global?,
    theme: Theme?,
    groupAdapter: GroupAdapter<GroupieViewHolder>
) {
    addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable) {
            val searchInput = text.toString().toLowerCase(Locale.getDefault())
            val formerList = ((liveGlobal?.Countries as ArrayList?) ?: ArrayList()).sortedBy {
                it.Country
            }
            groupAdapter.clear()
            Timber.d("searchInput is $searchInput")

            if (searchInput.isNotEmpty()) {
                val newList = formerList.filter {
                    it.Country.toLowerCase(Locale.getDefault()).contains(searchInput)
                }
                groupAdapter.addGlobalItems(newList, theme)
            } else {
                liveGlobal?.let {
                    groupAdapter.add(
                        0,
                        EachCountryItem(getGlobalCountry(it), theme)
                    )
                }
                groupAdapter.addGlobalItems(formerList, theme)
            }
        }
    })
}

@BindingAdapter("getAllCountries", "addTheme", "addGroupAdapter")
fun RecyclerView.getAllCountries(
    global: Global?,
    theme: Theme?,
    groupAdapter: GroupAdapter<GroupieViewHolder>
) {
    layoutManager = LinearLayoutManager(context)
    adapter = groupAdapter

    global?.let {
        groupAdapter.add(
            EachCountryItem(
                getGlobalCountry(it),
                theme
            )
        )
        groupAdapter.addGlobalItems(it.Countries, theme)
    }
}

fun getGlobalCountry(global: Global): EachCountry {
    return with(global.summaryData) {
        EachCountry(
            Country = "Global",
            CountryCode = "GLO",
            Date = "",
            NewConfirmed = NewConfirmed,
            NewDeaths = NewDeaths,
            NewRecovered = NewRecovered,
            Premium = Premium(),
            Slug = "",
            TotalConfirmed = TotalConfirmed,
            TotalDeaths = TotalDeaths,
            TotalRecovered = TotalRecovered
        )
    }
}

fun GroupAdapter<GroupieViewHolder>.addGlobalItems(
    arrayOfEachCountry: List<EachCountry>?,
    theme: Theme?
) {
    arrayOfEachCountry?.forEach {
        add(EachCountryItem(it, theme))
    }
}