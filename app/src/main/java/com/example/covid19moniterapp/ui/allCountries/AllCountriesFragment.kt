package com.example.covid19moniterapp.ui.allCountries

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.databinding.FragmentAllCountriesBinding
import com.example.covid19moniterapp.ui.currentCountry.CurrentCountryViewModel
import com.example.covid19moniterapp.ui.currentCountry.CurrentCountryViewModelFactory
import timber.log.Timber

class AllCountriesFragment : Fragment() {

    private val database = lazy {
        DataBase.getInstance(requireActivity().application)!!
    }
    private lateinit var viewModel: AllCountriesViewModel
    private lateinit var binding: FragmentAllCountriesBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_countries, container, false)
        viewModel = ViewModelProvider(viewModelStore, AllCountriesViewModelFactory(database.value, requireContext())).get(AllCountriesViewModel::class.java)

        setHasOptionsMenu(true)
        binding.recyclerView.adapter = viewModel.adapter

        viewModel.mainGlobalDataData.observe(viewLifecycleOwner) { mainGlobalData ->
            viewModel.adapter.clear()
            val mapOfEachCountryGroup = LinkedHashMap<String, EachCountryGroup>()
            mainGlobalData.Countries?.forEach {country ->
                Timber.d("viewModel countryName is ${country.Country}")
                val eachCountryGroup = EachCountryGroup(country)
                mapOfEachCountryGroup[country.CountryCode] = eachCountryGroup
            }

            mapOfEachCountryGroup.values.forEach {
                Timber.d("map countryName is ${it.country?.Country}")
            }

            val arrayOfEachCountryGroup = mapOfEachCountryGroup.values.toList() as ArrayList<EachCountryGroup>
            arrayOfEachCountryGroup.sortBy { eachCountryGroup ->
                eachCountryGroup.country?.Country
            }

            Timber.d("viewModel size is ${mainGlobalData.Countries?.size} and map size is ${mapOfEachCountryGroup.size} and arrayOfEachCountryGroup size is ${arrayOfEachCountryGroup.size}")

            viewModel.adapter.updateAsync(arrayOfEachCountryGroup)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search_icon)

        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.hint = "Search Here"

            searchView.setOnQueryTextListener(viewModel.searchViewQueryListener(searchView))
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }


}