package com.example.covid19moniterapp.ui.currentCountry

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.covid19moniterapp.MainActivity
import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.databinding.FragmentCurrentCountryBinding
import com.example.covid19moniterapp.network.currentCountry.CurrentCountryItem
import com.example.covid19moniterapp.viewmodel.SharedViewModel
import com.example.covid19moniterapp.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class CurrentFragment: Fragment() {
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var binding: FragmentCurrentCountryBinding

    val actionBarJob = Job()
    val actionBarScope = CoroutineScope(Dispatchers.Main + actionBarJob)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModelJob = Job()
        val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

        val application = requireNotNull(this.activity).application
        val database = DataBase.getInstance(application)
        sharedViewModel = ViewModelProvider(this, SharedViewModelFactory(database!!)).get(
            SharedViewModel::class.java
        )
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_country, container, false)


        val spinnerItem = binding.sortSpinner
        val provinceList = ArrayList<String>()

        provinceList.add("Total")

        val spinnerArrayAdapter = ArrayAdapter<String>(this.requireContext(), R.layout.simple_spinner_item, provinceList)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_item)
        spinnerItem.adapter = spinnerArrayAdapter

        val adapter = RecyclerAdapter()
        var currentCountryData = sharedViewModel.currentCountryData.value?.reversed()

        fun loadImage(imgView: ImageView, imgUrl: String){
            val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()


            Glide.with(imgView.context)
                .load(imgUri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.loading_animation)
                        .error(R.drawable.ic_broken_image))
                .into(imgView)
        }

        var latestCountryItem: CurrentCountryItem?
        var totalCountryItem = Total(0, 0, 0, 0, 0)

        fun updateTotal(){
            latestCountryItem = currentCountryData?.get(0)
            sharedViewModel.setAllCountries()
            val allList = sharedViewModel.allCountriesData

            actionBarScope.launch {
                (activity as MainActivity).supportActionBar?.title = latestCountryItem?.Country
            }

            allList.value?.Countries?.let {
                for (country in it) {
                    Timber.i("country.Country is ${country.Country} and latestCountryItem?.Country is ${latestCountryItem?.Country}")
                    if (country.Country == latestCountryItem?.Country){
                        totalCountryItem = Total(country.TotalConfirmed , country.TotalDeaths, country.TotalRecovered, country.NewConfirmed, country.NewRecovered)
                    }
                }
            }

            var provinceIsEmpty: Boolean

            sharedViewModel.currentCountryData.value?.let {
                for (country in it){
                    country.Province?.let {province ->

                        provinceIsEmpty = province.isEmpty()
                        Timber.i("provinceIsEmpty is $provinceIsEmpty")
                        val newProvince = if (provinceIsEmpty){
                            "Total"
                        }else {
                            province
                        }

                        if (!provinceList.contains(newProvince)){

                            provinceList.add(newProvince)
                            spinnerArrayAdapter.notifyDataSetChanged()

                        }
                    }
                }

                 if (!provinceList.contains("Total")){
                     provinceList.add(0, "Total")
                 }
            }

            binding.newCasesDisplayText.text = totalCountryItem.newCases.toString()
            binding.newRecoveriesDisplayText.text = totalCountryItem.newRecovered.toString()
            binding.casesDisplayText.text = totalCountryItem.totalCases.toString()
            binding.totalRecoveredDisplayText.text = totalCountryItem.totalRecovered.toString()
            binding.totalDeathDisplayText.text = totalCountryItem.totalDeaths.toString()

            actionBarScope.launch {
                loadImage(
                    binding.countryFlagImage,
                    "https://www.countryflags.io/${latestCountryItem?.CountryCode}/flat/64.png"
                )
            }
        }

        updateTotal()

        val timer = object : CountDownTimer(9000, 500) {
            override fun onFinish() {
                uiScope.launch {
                    sharedViewModel.repoInstance.refreshData()
                }
                sharedViewModel.setCurrentCountry()
            }

            override fun onTick(millisUntilFinished: Long) {
                uiScope.launch {
                    sharedViewModel.repoInstance.refreshData()
                }
                sharedViewModel.setCurrentCountry()
                currentCountryData = sharedViewModel.currentCountryData.value?.reversed()

                updateTotal()

                try {
                    adapter.data = sharedViewModel.currentCountryData.value?.reversed() as ArrayList<CurrentCountryItem>? ?: arrayListOf()
                }
                catch(t: Throwable) {
                    Timber.e(t)
                }
            }
        }

        actionBarScope.launch {
            timer.start()
        }

        spinnerItem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                adapter.data.clear()
                Timber.i("provinceList[position] is ${provinceList[position]}")
                val itemTimer = object : CountDownTimer(9000, 500) {
                    override fun onFinish() {
                        uiScope.launch {
                            sharedViewModel.repoInstance.refreshData()
                        }
                        sharedViewModel.setCurrentCountry()
                    }

                    override fun onTick(millisUntilFinished: Long) {
                        uiScope.launch {
                            sharedViewModel.repoInstance.refreshData()
                        }
                        sharedViewModel.setCurrentCountry()

                        try {
                            sharedViewModel.currentCountryData.value?.reversed()?.let { list ->
                                for (eachItem in list){
                                    eachItem.Province?.let {
                                        Timber.i("provinceList[position] is ${provinceList[position]} and province is $it")
                                        if (it == provinceList[position]){
                                            adapter.data.add(eachItem)
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged()
                            }
                        }
                        catch(t: Throwable) {
                            Timber.e(t)
                        }
                    }
                }

                if (provinceList[position] == "Total"){
                    timer.start()
                }
                else {
                    timer.cancel()
                    itemTimer.start()
                }
            }
        }

        binding.newCasesDisplayText.text = totalCountryItem.newCases.toString()
        binding.newRecoveriesDisplayText.text = totalCountryItem.newRecovered.toString()
        binding.casesDisplayText.text = totalCountryItem.totalCases.toString()
        binding.totalRecoveredDisplayText.text = totalCountryItem.totalRecovered.toString()
        binding.totalDeathDisplayText.text = totalCountryItem.totalDeaths.toString()

        adapter.data = sharedViewModel.currentCountryData.value?.reversed() as ArrayList<CurrentCountryItem>? ?: arrayListOf()

        binding.currentRecyclerView.adapter = adapter
//        loadImage(binding.countryFlagImage, "https://www.countryflags.io/${latestCountryItem?.CountryCode}/flat/64.png")

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionBarJob.cancel()
    }
}



data class Total(val totalCases: Int, val totalDeaths: Int, val totalRecovered: Int, val newCases: Int, val newRecovered: Int)