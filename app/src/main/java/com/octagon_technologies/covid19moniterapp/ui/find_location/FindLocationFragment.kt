package com.octagon_technologies.covid19moniterapp.ui.find_location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.Theme
import com.octagon_technologies.covid19moniterapp.changeStatusBarColor
import com.octagon_technologies.covid19moniterapp.databinding.FindLocationFragmentBinding
import com.octagon_technologies.covid19moniterapp.main_activity.MainActivity
import com.octagon_technologies.covid19moniterapp.removeToolbarAndBottomNav

class FindLocationFragment : Fragment() {

    private lateinit var viewModel: FindLocationViewModel
    private lateinit var binding: FindLocationFragmentBinding
    private val theme: LiveData<Theme> by lazy {
        (activity as MainActivity).mainViewModel.theme
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
        binding.favouriteLocationsList = viewModel.favouriteLocationsList

        setOnClickListeners()
        viewModel.getFavouriteLocations()

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.turnOnLocation()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        this.removeToolbarAndBottomNav()

        theme.observe(this, {
            changeStatusBarColor(if (it == Theme.LIGHT) R.color.line_grey else R.color.color_black)
        })
    }

    private fun setOnClickListeners() {
        binding.closeBtn.setOnClickListener { findNavController().popBackStack() }
        binding.searchQuery.setOnClickListener { findNavController().navigate(R.id.action_findLocationFragment_to_searchLocationFragment) }
        binding.enableLocationLayout.setOnClickListener { viewModel.checkIfPermissionIsGranted() }

        binding.gpsLocationLayout.setOnClickListener {
            viewModel.saveLocationInDataStore()
            (activity as MainActivity).mainViewModel.location.value =
                viewModel.reversedGeoCodingLocation.value?.reverseGeoCodingAddress?.country
            findNavController().popBackStack()
        }
    }

}