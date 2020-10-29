package com.octagon_technologies.covid19_statistics_app.ui.search_location.each_search_result_item

import com.octagon_technologies.covid19_statistics_app.IconType
import com.octagon_technologies.covid19_statistics_app.R
import com.octagon_technologies.covid19_statistics_app.databinding.EachSearchResultItemBinding
import com.octagon_technologies.covid19_statistics_app.getActualIconFromTheme
import com.octagon_technologies.covid19_statistics_app.ui.search_location.EachAdapterLocationItem
import com.xwray.groupie.databinding.BindableItem

class EachSearchResultItem(private val eachAdapterLocationItem: EachAdapterLocationItem) :
    BindableItem<EachSearchResultItemBinding>() {
    override fun bind(binding: EachSearchResultItemBinding, position: Int) {
        eachAdapterLocationItem.apply {
            binding.location = location
            binding.bindingCountryName = bindingCountryName
            binding.liveLocation = liveLocation
            binding.theme = theme

            binding.addToFavouritesBtn.setImageResource(
                if (isFavourite) getActualIconFromTheme(IconType.Checked, theme)
                else getActualIconFromTheme(IconType.AddItem, theme)
            )

            if (liveRecentAdapterPosition != null) binding.addToFavouritesBtn.removeFromRecent(location, theme, liveRecentAdapterPosition, position)
            else binding.addToFavouritesBtn.addToFavouriteIcon(location, theme)
        }
    }

    override fun getLayout(): Int = R.layout.each_search_result_item
}