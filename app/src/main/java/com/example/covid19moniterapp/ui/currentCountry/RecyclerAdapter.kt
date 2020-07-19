package com.example.covid19moniterapp.ui.currentCountry

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.covid19moniterapp.databinding.EachCurrentItemBinding
import com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem
import java.text.SimpleDateFormat
import java.util.*


class RecyclerAdapter : RecyclerView.Adapter<MyViewHolder>() {

    var data = arrayListOf<CurrentCountryItem>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(EachCurrentItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentCountryItem = data[position]

        holder.bind(currentCountryItem)
    }
}

class MyViewHolder(private val binding: EachCurrentItemBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(item: CurrentCountryItem?) {
        binding.currentItem = item

        val date = item?.Date ?: ""
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

        if(item?.Province != null && item.Province.isNotEmpty()){
            newString += " (${item.Province})"
        }

        binding.dateDisplayText.text = newString

        binding.executePendingBindings()
    }
}