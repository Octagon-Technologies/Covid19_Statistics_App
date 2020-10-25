package com.octagon_technologies.covid19moniterapp.ui.allCountries.each_country_item

import androidx.navigation.findNavController
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.Theme
import com.octagon_technologies.covid19moniterapp.databinding.EachAllCountryItemBinding
import com.octagon_technologies.covid19moniterapp.network.allCountries.EachCountry
import com.octagon_technologies.covid19moniterapp.ui.allCountries.AllCountriesFragmentDirections
import com.xwray.groupie.databinding.BindableItem

class EachCountryItem(private val eachCountry: EachCountry, private val theme: Theme?): BindableItem<EachAllCountryItemBinding>() {
    override fun bind(binding: EachAllCountryItemBinding, position: Int) {
        binding.eachCountry = eachCountry
        binding.theme = theme

        binding.mainConstraint.setOnClickListener {
            if (eachCountry.CountryCode == "GLO") return@setOnClickListener
            it.findNavController().navigate(AllCountriesFragmentDirections.actionAllCountriesFragmentToCurrentFragment(eachCountry.Country))
        }
    }

    override fun getLayout(): Int = R.layout.each_all_country_item
}