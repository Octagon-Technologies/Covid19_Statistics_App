package com.octagon_technologies.covid19_statistics_app.ui.allCountries.each_country_item

import android.widget.Toast
import androidx.core.view.updateLayoutParams
import androidx.navigation.findNavController
import com.octagon_technologies.covid19_statistics_app.R
import com.octagon_technologies.covid19_statistics_app.Theme
import com.octagon_technologies.covid19_statistics_app.databinding.EachAllCountryItemBinding
import com.octagon_technologies.covid19_statistics_app.isNetworkConnected
import com.octagon_technologies.covid19_statistics_app.network.allCountries.EachCountry
import com.octagon_technologies.covid19_statistics_app.ui.allCountries.AllCountriesFragmentDirections
import com.xwray.groupie.databinding.BindableItem

class EachCountryItem(private val eachCountry: EachCountry, private val theme: Theme?) :
    BindableItem<EachAllCountryItemBinding>() {
    override fun bind(binding: EachAllCountryItemBinding, position: Int) {
        binding.eachCountry = eachCountry
        binding.theme = theme

        if (eachCountry.CountryCode == "GLO") {
            binding.allCountryFlagImage.updateLayoutParams {
                height = binding.root.resources.getDimensionPixelSize(R.dimen._80sdp)
                width = binding.root.resources.getDimensionPixelSize(R.dimen._80sdp)
            }
//            binding.allCountryFlagImage.setColorFilter(android.R.color.holo_red_dark)//(if (theme == Theme.LIGHT) R.color.dark_black else android.R.color.white)
//            Timber.d("allCountryFlagImage.tint is ${binding.allCountryFlagImage.}")
        }

        binding.mainConstraint.setOnClickListener {
            if (eachCountry.CountryCode == "GLO") return@setOnClickListener
            val context = binding.root.context

            if (!context.isNetworkConnected()) {
                Toast.makeText(
                    context,
                    context.getString(R.string.no_network_available_plain_text),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            it.findNavController().navigate(
                AllCountriesFragmentDirections.actionAllCountriesFragmentToCurrentFragment(
                    eachCountry.Country
                )
            )
        }
    }

    override fun getLayout(): Int = R.layout.each_all_country_item
}