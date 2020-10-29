package com.octagon_technologies.covid19_statistics_app.ui.find_location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.octagon_technologies.covid19_statistics_app.R
import com.octagon_technologies.covid19_statistics_app.Theme
import com.octagon_technologies.covid19_statistics_app.changeStatusBarColor
import com.octagon_technologies.covid19_statistics_app.databinding.FindLocationFragmentBinding
import com.octagon_technologies.covid19_statistics_app.main_activity.MainActivity
import com.octagon_technologies.covid19_statistics_app.removeToolbarAndBottomNav
import com.octagon_technologies.covid19_statistics_app.ui.search_location.EachAdapterLocationItem
import com.octagon_technologies.covid19_statistics_app.ui.search_location.each_search_result_item.EachSearchResultItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import timber.log.Timber

class FindLocationFragment : Fragment() {


    private lateinit var viewModel: FindLocationViewModel
    private lateinit var binding: FindLocationFragmentBinding
    private val theme: Theme by lazy {
        (activity as MainActivity).mainViewModel.theme.value ?: Theme.LIGHT
    }
    private val favouritesGroupAdapter = GroupAdapter<GroupieViewHolder>()
    private val recentGroupAdapter = GroupAdapter<GroupieViewHolder>()

    private val topCountryName: TextView by lazy {
        (activity as MainActivity).binding.countryName
    }

    private val liveLocation: MutableLiveData<String> by lazy {
        (activity as MainActivity).mainViewModel.location
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FindLocationFragmentBinding.inflate(inflater)
        viewModel = ViewModelProvider(this, FindLocationViewModelFactory(requireContext())).get(
            FindLocationViewModel::class.java
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.reversedGeoCodingLocation = viewModel.reversedGeoCodingLocation
        binding.isLoading = viewModel.isLoading
        binding.theme = theme
        binding.settingsDataStore = viewModel.settingsDataStore

        binding.favouriteRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favouritesGroupAdapter
        }

        binding.recentRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recentGroupAdapter
        }

        viewModel.favouriteLocationsList.observe(viewLifecycleOwner, { favouriteLocationsList ->
            if(!favouriteLocationsList.isNullOrEmpty()) {
                binding.emptyFavouriteListText.visibility = View.GONE
                val recentParams = binding.recentPlainText.layoutParams as ViewGroup.MarginLayoutParams
                recentParams.topMargin = binding.root.resources.getDimensionPixelSize(R.dimen._40sdp)
            }
            favouritesGroupAdapter.clear()
            favouriteLocationsList?.forEach {
                Timber.d("favouriteLocationItem is $it")
                favouritesGroupAdapter.add(
                    EachSearchResultItem(
                        EachAdapterLocationItem(true, it, topCountryName, liveLocation, theme, null)
                    )
                )
            }
        })

        viewModel.liveRecentAdapterPosition.observe(viewLifecycleOwner, {
            if (it == null || it < 0) return@observe

            Timber.d("liveRecentAdapterPosition is $it")
            recentGroupAdapter.remove(recentGroupAdapter.getItem(it - 1))
        })

        viewModel.recentLocationsList.observe(viewLifecycleOwner, { recentLocationsList ->
            recentLocationsList?.let { if(it.isNotEmpty()) binding.emptyRecentListText.visibility = View.GONE }
            recentGroupAdapter.clear()
            recentLocationsList?.forEach {
                Timber.d("recentLocationItem is $it")
                recentGroupAdapter.add(
                    EachSearchResultItem(
                        EachAdapterLocationItem(true, it, topCountryName, liveLocation, theme, viewModel.liveRecentAdapterPosition)
                    )
                )
            }
        })

        setOnClickListeners()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.turnOnLocation()
        }

        return binding.root
    }

    private fun setOnClickListeners() {
        binding.closeBtn.setOnClickListener { findNavController().popBackStack() }
        binding.searchQuery.setOnClickListener { findNavController().navigate(R.id.action_findLocationFragment_to_searchLocationFragment) }
        binding.enableLocationLayout.setOnClickListener { viewModel.checkIfPermissionIsGranted() }

        binding.clearFavouritesBtn.setOnClickListener {
            viewModel.settingsDataStore.clearHistoryLocation(false)
            favouritesGroupAdapter.clear()
            binding.emptyFavouriteListText.visibility = View.VISIBLE

            val recentParams = binding.recentPlainText.layoutParams as ViewGroup.MarginLayoutParams
            recentParams.topMargin = binding.root.resources.getDimensionPixelSize(R.dimen._70sdp)
        }
        binding.clearRecentHistoryBtn.setOnClickListener {
            viewModel.settingsDataStore.clearHistoryLocation(true)
            recentGroupAdapter.clear()
            binding.emptyRecentListText.visibility = View.VISIBLE
        }

        binding.gpsLocationLayout.setOnClickListener {
            viewModel.saveLocationInDataStore()
            (activity as MainActivity).mainViewModel.location.value =
                viewModel.reversedGeoCodingLocation.value?.reverseGeoCodingAddress?.country
            findNavController().popBackStack()
        }
    }

    override fun onStart() {
        super.onStart()
        this.removeToolbarAndBottomNav()
        viewModel.getHistoryLocations()
        viewModel.getHistoryLocations(true)

        changeStatusBarColor(if (theme == Theme.LIGHT) R.color.line_grey else R.color.color_black)
    }
}