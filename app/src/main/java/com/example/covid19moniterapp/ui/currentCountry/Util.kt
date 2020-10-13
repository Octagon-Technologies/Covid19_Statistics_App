package com.example.covid19moniterapp.ui.currentCountry

import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.databinding.EachCurrentItemBinding
import com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.each_all_country_item.view.*
import kotlinx.android.synthetic.main.each_current_item.view.*
//import com.xwray.groupie.databinding.BindableItem
import java.text.SimpleDateFormat
import java.util.*

class EachCurrentHistoryGroup(val currentCountryItem: CurrentCountryItem?): Item<GroupieViewHolder>() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val layout = viewHolder.itemView
//        binding.currentItem = currentCountryItem

        val date = currentCountryItem?.Date ?: ""
        var stringArray = ""
        for(char in date){
            if(char.toString() != "T"){
                stringArray += "$char"
            }
            else{
                break
            }
        }

        val weekDayString = SimpleDateFormat("yyyy-MM-dd",
            Locale.getDefault()).parse(stringArray)
        val longDate = weekDayString?.time
//      2020-03-18

        var newString = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(longDate)

        if(currentCountryItem?.Province != null && currentCountryItem.Province.isNotEmpty()){
            newString += " (${currentCountryItem.Province})"
        }

        layout.date_display_text.text = newString

        layout.recovered_display_text.text = currentCountryItem?.Recovered.toString()
        layout.death_display_text.text = currentCountryItem?.Deaths.toString()
        layout.active_display_text.text = currentCountryItem?.Active.toString()
        layout.confirmed_display_text.text = currentCountryItem?.Confirmed.toString()
    }

    override fun getLayout(): Int = R.layout.each_current_item
}