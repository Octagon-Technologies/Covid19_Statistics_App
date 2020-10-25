package com.octagon_technologies.covid19moniterapp.ui.currentCountry.each_current_history_item

import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.Theme
import com.octagon_technologies.covid19moniterapp.databinding.EachCurrentHistoryItemBinding
import com.octagon_technologies.covid19moniterapp.network.currentCountry.CurrentCountry
import com.xwray.groupie.databinding.BindableItem
import timber.log.Timber

class EachCurrentHistoryItem(
    val currentCountry: CurrentCountry?,
    var theme: Theme?
) :
    BindableItem<EachCurrentHistoryItemBinding>() {
    override fun bind(binding: EachCurrentHistoryItemBinding, position: Int) {
        Timber.d("onBind called.")
        binding.currentCountry = currentCountry
        binding.theme = theme
    }

    override fun bind(
        binding: EachCurrentHistoryItemBinding,
        position: Int,
        payloads: MutableList<Any>
    ) {
        Timber.d("payloads is $payloads")
        if (payloads.isNotEmpty()) {
            payloads.forEach {
                Timber.d("payload[${payloads.indexOf(it)}] is $it")
            }
            if (payloads[0] is Theme) {
                binding.theme = payloads[0] as Theme?
            }
        } else {
            super.bind(binding, position, payloads)
        }
    }

    override fun getLayout(): Int = R.layout.each_current_history_item
}
