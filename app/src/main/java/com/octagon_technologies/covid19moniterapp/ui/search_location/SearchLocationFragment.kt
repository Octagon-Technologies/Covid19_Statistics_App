package com.octagon_technologies.covid19moniterapp.ui.search_location

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.SettingsDataStore
import com.octagon_technologies.covid19moniterapp.Theme
import com.octagon_technologies.covid19moniterapp.changeStatusBarColor
import com.octagon_technologies.covid19moniterapp.databinding.SearchLocationFragmentBinding
import com.octagon_technologies.covid19moniterapp.main_activity.MainActivity
import com.octagon_technologies.covid19moniterapp.ui.find_location.FindLocationViewModel
import com.octagon_technologies.covid19moniterapp.ui.find_location.FindLocationViewModelFactory
import com.octagon_technologies.covid19moniterapp.ui.search_location.each_search_result_item.EachSearchResultItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class SearchLocationFragment : Fragment() {

    private lateinit var viewModel: SearchLocationViewModel
    private lateinit var findLocationViewModel: FindLocationViewModel
    private lateinit var binding: SearchLocationFragmentBinding

    val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(requireContext())
    }
    private val favouritesList = ArrayList<String>()
    private val bindingCountryName: TextView by lazy {
        (activity as MainActivity).binding.countryName
    }
    private val liveLocation: MutableLiveData<String> by lazy {
        (activity as MainActivity).mainViewModel.location
    }
    private val theme: LiveData<Theme> by lazy {
        (activity as MainActivity).mainViewModel.theme
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
        binding.fragmentLifecycleOwner = viewLifecycleOwner
        binding.theme = theme

        binding.cancelBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchLocationRecyclerview.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(context)
        }

        viewModel.uiScope.launch {
            favouritesList.addAll(settingsDataStore.getFavouriteLocation().first())
        }

        viewModel.locationSuggestions.observe(viewLifecycleOwner, { locationSuggestions ->
            Timber.d("List of location is ${locationSuggestions?.size}")
            groupAdapter.clear()

            locationSuggestions?.forEach {
                Timber.d("eachLocation is $it")
                val eachSearchResultItem = EachSearchResultItem(
                    EachAdapterLocationItem(
                        isFavourite = it in favouritesList,
                        location = it,
                        bindingCountryName = bindingCountryName,
                        liveLocation = liveLocation,
                        theme = theme
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

    val handler = object : CountDownTimer(700, 500) {
        override fun onTick(millisUntilFinished: Long) {}

        override fun onFinish() {
            viewModel.getLocationSuggestions(binding.searchQuery.text.toString())
        }
    }

    override fun onStart() {
        super.onStart()

        theme.observe(this, {
            changeStatusBarColor(if (it == Theme.LIGHT) R.color.line_grey else R.color.color_black)
        })
    }
}