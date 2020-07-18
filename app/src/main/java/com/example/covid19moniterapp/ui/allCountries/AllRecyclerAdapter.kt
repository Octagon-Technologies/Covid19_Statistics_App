package com.example.covid19moniterapp.ui.allCountries

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.databinding.EachAllCountryItemBinding
import com.example.covid19moniterapp.databinding.EachCurrentItemBinding
import com.example.covid19moniterapp.network.allCountries.AllCountries
import com.example.covid19moniterapp.network.allCountries.Country


class AllRecyclerAdapter(): RecyclerView.Adapter<AllViewHolder>() {

    var data: AllCountries? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllViewHolder {
        return AllViewHolder(EachAllCountryItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount() = data?.Countries?.size ?: 0

    override fun onBindViewHolder(holder: AllViewHolder, position: Int) {
        val currentCountryItem = data?.Countries?.get(position)

        holder.bind(currentCountryItem)
    }
}

class AllViewHolder(private val binding: EachAllCountryItemBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(item: Country?) {
        binding.eachCountry = item
        val imgView = binding.allCountryFlagImage
        val imgUri = "https://www.countryflags.io/${item?.CountryCode}/flat/64.png".toUri().buildUpon().scheme("https").build()

        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image))
            .into(imgView)

        binding.executePendingBindings()
    }
}

/*
const val COUNTRY_IMAGE_BASE_URL = "https://www.countryflags.io/"

// extra = be/flat/64.png
 */