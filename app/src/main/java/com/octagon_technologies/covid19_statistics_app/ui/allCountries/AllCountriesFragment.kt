package com.octagon_technologies.covid19_statistics_app.ui.allCountries

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.octagon_technologies.covid19_statistics_app.*
import com.octagon_technologies.covid19_statistics_app.databinding.ActivityMainBinding
import com.octagon_technologies.covid19_statistics_app.databinding.FragmentAllCountriesBinding
import com.octagon_technologies.covid19_statistics_app.main_activity.MainActivity
import com.octagon_technologies.covid19_statistics_app.main_activity.MainViewModel
import com.octagon_technologies.covid19_statistics_app.ui.search_location.CustomTextWatcher
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import timber.log.Timber

@SuppressLint("NewApi")
class AllCountriesFragment : Fragment() {
    val viewModel: AllCountriesViewModel by lazy {
        ViewModelProvider(viewModelStore, AllCountriesViewModelFactory(requireContext())).get(AllCountriesViewModel::class.java)
    }
    private val connectivityManager: ConnectivityManager by lazy {
        requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val binding: FragmentAllCountriesBinding by lazy { FragmentAllCountriesBinding.inflate(layoutInflater) }
    private val networkCallback: ConnectivityManager.NetworkCallback by lazy { getNetworkListener() }
    private val mainViewModel: MainViewModel by lazy { (activity as MainActivity).mainViewModel }
    private val mainActivityBinding: ActivityMainBinding by lazy { (activity as MainActivity).binding }
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()
    private val isConnected: Boolean by lazy { requireContext().isNetworkConnected() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navView = (activity as MainActivity).binding.navView
        navView.visibility = View.VISIBLE

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.mainViewModel = mainViewModel
        binding.groupAdapter = groupAdapter

        binding.searchQuery.addTextChangedListener(object: CustomTextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterAllCountries(binding.searchQuery, viewModel.liveGlobal.value, mainViewModel.theme.value, groupAdapter)
            }
        })

        binding.allCountriesRecyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
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

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart called")

        mainActivityBinding.topToolbar.visibility = View.GONE
        mainActivityBinding.toolbarBottomLine.visibility = View.GONE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isConnected) connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                networkCallback
            )
        }

        filterAllCountries(binding.searchQuery, viewModel.liveGlobal.value, mainViewModel.theme.value, groupAdapter)

        mainViewModel.theme.observe(this, {
            changeStatusBarColor(if (it == Theme.LIGHT) R.color.line_grey else R.color.color_black)
        })
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !isConnected) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

}