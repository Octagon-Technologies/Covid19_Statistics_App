package com.octagon_technologies.covid19_statistics_app.ui.search_location

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.octagon_technologies.covid19_statistics_app.R
import com.octagon_technologies.covid19_statistics_app.Theme
import com.octagon_technologies.covid19_statistics_app.databinding.SearchLocationFragmentBinding
import com.octagon_technologies.covid19_statistics_app.main_activity.MainActivity
import com.octagon_technologies.covid19_statistics_app.ui.find_location.FindLocationViewModel
import com.octagon_technologies.covid19_statistics_app.ui.find_location.FindLocationViewModelFactory
import com.octagon_technologies.covid19_statistics_app.ui.search_location.each_search_result_item.EachSearchResultItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import timber.log.Timber

class SearchLocationFragment : Fragment() {

    private lateinit var viewModel: SearchLocationViewModel
    private lateinit var findLocationViewModel: FindLocationViewModel
    private lateinit var binding: SearchLocationFragmentBinding

    val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val bindingCountryName: TextView by lazy {
        (activity as MainActivity).binding.countryName
    }
    private val liveLocation: MutableLiveData<String> by lazy {
        (activity as MainActivity).mainViewModel.location
    }
    private val theme: Theme by lazy {
        (activity as MainActivity).mainViewModel.theme.value ?: Theme.LIGHT
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SearchLocationFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, SearchLocationViewModelFactory(requireContext())).get(
            SearchLocationViewModel::class.java
        )
        findLocationViewModel =
            ViewModelProvider(this, FindLocationViewModelFactory(requireContext())).get(
                FindLocationViewModel::class.java
            )
        binding.lifecycleOwner = this
        binding.findLocationViewModel = findLocationViewModel
        binding.theme = theme
        binding.navController = findNavController()

        binding.searchLocationRecyclerview.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(context)
        }

        findLocationViewModel.reversedGeoCodingLocation.observe(viewLifecycleOwner, {
            findLocationViewModel.saveLocationInDataStore()
            liveLocation.value = it.reverseGeoCodingAddress?.country ?: return@observe
            findNavController().popBackStack(R.id.currentFragment, false)
        })

        viewModel.locationSuggestions.observe(viewLifecycleOwner, { locationSuggestions ->
            groupAdapter.clear()
            locationSuggestions?.forEach {
                Timber.d("eachLocation is $it")
                val eachSearchResultItem = EachSearchResultItem(
                    EachAdapterLocationItem(
                        isFavourite = it in viewModel.favouritesList,
                        location = it,
                        bindingCountryName = bindingCountryName,
                        liveLocation = liveLocation,
                        theme = theme,
                        liveRecentAdapterPosition = null
                    )
                )
                groupAdapter.add(eachSearchResultItem)
            }
        })

        binding.searchQuery.addTextChangedListener(object : CustomTextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                super.onTextChanged(s, start, before, count)
                handler.cancel()
                handler.start()
            }
        })

        return binding.root
    }

    val handler = object : CountDownTimer(200, 100) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            viewModel.getLocationSuggestions(binding.searchQuery.text.toString())
        }
    }

}