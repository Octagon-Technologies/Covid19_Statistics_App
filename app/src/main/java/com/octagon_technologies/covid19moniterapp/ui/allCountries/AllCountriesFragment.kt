package com.octagon_technologies.covid19moniterapp.ui.allCountries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.Theme
import com.octagon_technologies.covid19moniterapp.changeStatusBarColor
import com.octagon_technologies.covid19moniterapp.databinding.ActivityMainBinding
import com.octagon_technologies.covid19moniterapp.databinding.FragmentAllCountriesBinding
import com.octagon_technologies.covid19moniterapp.main_activity.MainActivity
import com.octagon_technologies.covid19moniterapp.main_activity.MainViewModel
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import timber.log.Timber

class AllCountriesFragment : Fragment() {

    private lateinit var viewModel: AllCountriesViewModel
    private lateinit var binding: FragmentAllCountriesBinding
    private lateinit var navView: BottomNavigationView

    private val mainViewModel: MainViewModel by lazy {
        (activity as MainActivity).mainViewModel
    }
    private val mainActivityBinding: ActivityMainBinding by lazy {
        (activity as MainActivity).binding
    }
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_all_countries, container, false)
        viewModel =
            ViewModelProvider(viewModelStore, AllCountriesViewModelFactory(requireContext())).get(
                AllCountriesViewModel::class.java
            )
        navView = (activity as MainActivity).binding.navView
        navView.visibility = View.VISIBLE

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.mainViewModel = mainViewModel
        binding.groupAdapter = groupAdapter

        binding.allCountriesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                when {
                    dy < 0 -> {
                        navView.visibility = View.VISIBLE
                    }
                    dy > 0 -> {
                        navView.visibility = View.GONE
                    }
                    else -> {
                        Timber.d("user is no longer scrolling")
                    }
                }
            }
        })

        viewModel.getAllCountries()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mainActivityBinding.topToolbar.visibility = View.GONE
        mainActivityBinding.toolbarBottomLine.visibility = View.GONE

        mainViewModel.theme.observe(this, {
            changeStatusBarColor(if (it == Theme.LIGHT) R.color.line_grey else R.color.color_black)
        })
    }

}