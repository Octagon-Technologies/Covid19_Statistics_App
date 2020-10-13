package com.example.covid19moniterapp.ui.allCountries

import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.network.allCountries.Country
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
//import com.xwray.groupie.databinding.BindableItem
import kotlinx.android.synthetic.main.each_all_country_item.view.*
import timber.log.Timber

class EachCountryGroup(val country: Country?): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        try {
            val layout = viewHolder.itemView
//            binding.eachCountry = country
            country?.let {
                layout.all_cases_display_text.text = it.TotalConfirmed.toString()
                layout.all_death_display_text.text = it.TotalDeaths.toString()
                layout.all_recovered_display_text.text = it.TotalRecovered.toString()
                layout.all_country_name.text = it.Country
            }

            val imgView = layout.all_country_flag_image
            val imgUri = "https://www.countryflags.io/${country?.CountryCode}/flat/64.png".toUri()
                .buildUpon().scheme("https").build()

            Glide.with(imgView.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image)
                )
                .into(imgView)

        } catch (t: Throwable) {
            Timber.e(t)
        }
    }

    override fun getLayout(): Int = R.layout.each_all_country_item
}