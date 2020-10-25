package com.octagon_technologies.covid19moniterapp.ui.search_location.each_search_result_item

import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.databinding.EachSearchResultItemBinding
import com.octagon_technologies.covid19moniterapp.ui.search_location.EachAdapterLocationItem
import com.xwray.groupie.databinding.BindableItem

class EachSearchResultItem(private val eachAdapterLocationItem: EachAdapterLocationItem): BindableItem<EachSearchResultItemBinding>() {
    override fun bind(binding: EachSearchResultItemBinding, position: Int) {
        eachAdapterLocationItem.apply {
            binding.location = location
            binding.bindingCountryName = bindingCountryName
            binding.liveLocation = liveLocation
            binding.theme = theme
            binding.addToFavouritesBtn.setImageResource(if (isFavourite) R.drawable.ic_check_circle_white else R.drawable.ic_add_circle_outline_black)
        }
    }

    override fun getLayout(): Int = R.layout.each_search_result_item
}