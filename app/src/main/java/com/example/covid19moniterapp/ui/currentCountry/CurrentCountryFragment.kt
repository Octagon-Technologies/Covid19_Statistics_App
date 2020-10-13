package com.example.covid19moniterapp.ui.currentCountry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.covid19moniterapp.MainActivity
import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.databinding.FragmentCurrentCountryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import timber.log.Timber

class CurrentCountryFragment: Fragment() {
    private lateinit var viewModel: CurrentCountryViewModel
    private lateinit var binding: FragmentCurrentCountryBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_current_country, container, false)
        viewModel = ViewModelProvider(viewModelStore, CurrentCountryViewModelFactory(requireContext())).get(CurrentCountryViewModel::class.java)

        val spinnerItem = binding.sortSpinner
        spinnerItem.adapter = viewModel.spinnerArrayAdapter

        initMainPage()
        viewModel.getCurrentCountry()
        viewModel.getAllCountries()

        spinnerItem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.spinnerOnItemClickListener(position)
            }
        }

        binding.currentRecyclerView.adapter = viewModel.adapter

        return binding.root
    }

    private fun initMainPage() {
        viewModel.countryTotal.observe(viewLifecycleOwner) {
            Timber.d("it is $it")
            (activity as MainActivity).supportActionBar?.title = it.countryName
            binding.newCasesDisplayText.text = it.newCases.toString()
            binding.newRecoveriesDisplayText.text = it.newRecovered.toString()
            binding.casesDisplayText.text = it.totalCases.toString()
            binding.totalRecoveredDisplayText.text = it.totalRecovered.toString()
            binding.totalDeathDisplayText.text = it.totalDeaths.toString()

            viewModel.loadImage(
                binding.countryFlagImage,
                "https://www.countryflags.io/${it.countryCode}/flat/64.png"
            )
        }

        viewModel.currentCountryData.observe(viewLifecycleOwner) {
            it.forEach { item ->
                val eachCurrentHistoryGroup = EachCurrentHistoryGroup(item)
                Timber.d("item is $item")
                viewModel.adapter.add(eachCurrentHistoryGroup)
            }

            viewModel.addProvincesToAdapter()
        }

    }

}
