package com.octagon_technologies.covid19_statistics_app.ui.currentCountry.each_current_history_item

import com.octagon_technologies.covid19_statistics_app.R
import com.octagon_technologies.covid19_statistics_app.Theme
import com.octagon_technologies.covid19_statistics_app.databinding.EachCurrentHistoryItemBinding
import com.octagon_technologies.covid19_statistics_app.network.currentCountry.CurrentCountry
import com.xwray.groupie.databinding.BindableItem

class EachCurrentHistoryItem(
    val currentCountry: CurrentCountry?,
    var theme: Theme?
) :
    BindableItem<EachCurrentHistoryItemBinding>() {
    override fun bind(binding: EachCurrentHistoryItemBinding, position: Int) {
        binding.currentCountry = currentCountry
        binding.theme = theme
    }

    override fun bind(
        binding: EachCurrentHistoryItemBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            if (payloads[0] is Theme) {
                binding.theme = payloads[0] as Theme?
            }
        } else {
            super.bind(binding, position, payloads)
        }
    }

    override fun getLayout(): Int = R.layout.each_current_history_item
}
