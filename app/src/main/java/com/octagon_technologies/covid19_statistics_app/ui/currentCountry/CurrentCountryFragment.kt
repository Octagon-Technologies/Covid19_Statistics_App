package com.octagon_technologies.covid19_statistics_app.ui.currentCountry

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.octagon_technologies.covid19_statistics_app.*
import com.octagon_technologies.covid19_statistics_app.databinding.FragmentCurrentCountryBinding
import com.octagon_technologies.covid19_statistics_app.main_activity.MainActivity
import com.octagon_technologies.covid19_statistics_app.main_activity.MainViewModel
import com.octagon_technologies.covid19_statistics_app.ui.allCountries.loadCountryFlag
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import timber.log.Timber

@SuppressLint("NewApi")
class CurrentCountryFragment : Fragment() {
    lateinit var viewModel: CurrentCountryViewModel
    private lateinit var binding: FragmentCurrentCountryBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    lateinit var settingsViewModel: MainViewModel
    private lateinit var liveLocation: MutableLiveData<String>
    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val connectivityManager: ConnectivityManager by lazy {
        requireContext().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
    }

    private val mainActivity: MainActivity by lazy { (activity as MainActivity) }

    private val networkCallback: ConnectivityManager.NetworkCallback by lazy { getNetworkListener() }

    private val isConnected: Boolean by lazy {
        requireContext().isNetworkConnected()
    }

    private var eachCountryName: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("arguments in onCreateView is $arguments")
        arguments?.let {
            eachCountryName = CurrentCountryFragmentArgs.fromBundle(it).countryName
            Timber.d("eachCountryName is $eachCountryName")
        }

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_current_country, container, false)
        swipeRefreshLayout = binding.swipeRefreshLayout
        viewModel =
            ViewModelProvider(viewModelStore, CurrentCountryViewModelFactory(requireContext())).get(
                CurrentCountryViewModel::class.java
            )
        settingsViewModel = mainActivity.mainViewModel
        liveLocation = settingsViewModel.location

        binding.lifecycleOwner = viewLifecycleOwner
        binding.liveCountryCode = viewModel.liveCountryCode
        binding.theme = settingsViewModel.theme
        binding.status = viewModel.status
        binding.selectedCountryName = eachCountryName

        eachCountryName?.let {
            mainActivity.binding.topToolbar.visibility = View.GONE
            mainActivity.binding.toolbarBottomLine.visibility = View.GONE
            mainActivity.binding.navView.visibility = View.GONE

            binding.eachCountryBackBtn.setOnClickListener {
                findNavController().popBackStack()
            }
        }

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupAdapter
        }

        liveLocation.observe(viewLifecycleOwner, {
            Timber.d("Live location is being observed")
            viewModel.loadData(requireContext(), eachCountryName ?: it)
        })

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true

            viewModel.loadData(requireContext(), eachCountryName ?: liveLocation.value)
        }

        viewModel.arrayOfCurrentCountry.observe(viewLifecycleOwner, { arrayOfCurrentCountry ->
            Timber.d("arrayOfCurrentCountry changed")

            binding.historyRecyclerView.getCurrentCountryHistory(
                lifecycleOwner = viewLifecycleOwner,
                arrayOfCurrentCountry = arrayOfCurrentCountry,
                liveTheme = settingsViewModel.theme,
                groupAdapter = groupAdapter,
                nestedScrollView = binding.nestedScrollView
            )

            swipeRefreshLayout.isRefreshing = false
        })

        viewModel.liveCountryCode.observe(viewLifecycleOwner, {
            binding.getFullData(it, viewModel.arrayOfAllCountries.value)
            binding.countryFlagImage.loadCountryFlag(it, settingsViewModel.theme.value)
            swipeRefreshLayout.isRefreshing = false
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        settingsViewModel.theme.observe(this, {
            changeStatusBarColor(if (it == Theme.LIGHT) android.R.color.white else R.color.dark_black)
        })

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!isConnected) connectivityManager.registerNetworkCallback(
                NetworkRequest.Builder().build(),
                networkCallback
            )
        }

        if (eachCountryName == null) {
            addToolbarAndBottomNav()
            mainActivity.binding.navView.setOnNavigationItemReselectedListener {
                Timber.d("it.itemId is ${it.itemId} and this.id is $id")

                if (!liveLocation.value.isNullOrEmpty()) {
                    Timber.d("Location has not changed.")
                    return@setOnNavigationItemReselectedListener
                }

                viewModel.loadData(requireContext(), liveLocation.value)
            }
        }
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
