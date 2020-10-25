package com.octagon_technologies.covid19moniterapp.ui.currentCountry

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.Theme
import com.octagon_technologies.covid19moniterapp.addToolbarAndBottomNav
import com.octagon_technologies.covid19moniterapp.changeStatusBarColor
import com.octagon_technologies.covid19moniterapp.databinding.EachCurrentHistoryItemBinding
import com.octagon_technologies.covid19moniterapp.databinding.FragmentCurrentCountryBinding
import com.octagon_technologies.covid19moniterapp.main_activity.MainActivity
import com.octagon_technologies.covid19moniterapp.main_activity.MainViewModel
import com.octagon_technologies.covid19moniterapp.ui.currentCountry.each_current_history_item.EachCurrentHistoryItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.databinding.BindableItem
import timber.log.Timber

class CurrentCountryFragment : Fragment() {
    private lateinit var viewModel: CurrentCountryViewModel
    private lateinit var binding: FragmentCurrentCountryBinding
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var settingsViewModel: MainViewModel
    private lateinit var liveLocation: MutableLiveData<String>
    val groupAdapter = GroupAdapter<GroupieViewHolder>()

    private val mainActivity: MainActivity by lazy {
        (activity as MainActivity)
    }

    var eachCountryName: String? = null
    private val mapOfGroupItems: LinkedHashMap<String, BindableItem<EachCurrentHistoryItemBinding>> =
        LinkedHashMap()

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
        liveLocation = mainActivity.mainViewModel.location
        viewModel =
            ViewModelProvider(viewModelStore, CurrentCountryViewModelFactory(requireContext())).get(
                CurrentCountryViewModel::class.java
            )
        settingsViewModel = mainActivity.mainViewModel

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
            viewModel.loadData(eachCountryName ?: liveLocation.value)
        })

        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true

            if (viewModel.arrayOfCurrentCountry.value?.get(0)?.Country == eachCountryName ?: settingsViewModel.location.value) {
                Timber.d("Location has not changed.")
                Handler().postDelayed({ swipeRefreshLayout.isRefreshing = false }, 1000)
                return@setOnRefreshListener
            }

            viewModel.loadData(eachCountryName ?: liveLocation.value)
        }


        settingsViewModel.theme.observe(viewLifecycleOwner, { theme ->
            if (viewModel.arrayOfCurrentCountry.value?.get(0)?.Country == eachCountryName ?: settingsViewModel.location.value) {
                Timber.d("Theme has changed but Location hasn't")
                swipeRefreshLayout.isRefreshing = true

                mainActivity.binding.countryName.text =
                    viewModel.arrayOfCurrentCountry.value?.get(0)?.Country


                mapOfGroupItems.values.forEach {
                    val position = mapOfGroupItems.values.indexOf(it)
                    groupAdapter.notifyItemChanged(position, theme)
                }

                swipeRefreshLayout.isRefreshing = false
                return@observe
            }
        })

        viewModel.arrayOfCurrentCountry.observe(viewLifecycleOwner, { arrayOfCurrentCountry ->
            Timber.d("arrayOfCurrentCountry changed")

            mainActivity.binding.countryName.text =
                viewModel.arrayOfCurrentCountry.value?.get(0)?.Country
            arrayOfCurrentCountry?.forEach {
                val eachCurrentHistoryItem =
                    EachCurrentHistoryItem(it, settingsViewModel.theme.value)
                mapOfGroupItems[it.Date] = eachCurrentHistoryItem
            }

            groupAdapter.updateAsync(mapOfGroupItems.values.toList())
            swipeRefreshLayout.isRefreshing = false
        })

        viewModel.liveCountryCode.observe(viewLifecycleOwner, {
            binding.getFullData(it, viewModel.arrayOfAllCountries.value)
            binding.countryFlagImage.loadCountryFlag(it)
            swipeRefreshLayout.isRefreshing = false
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        settingsViewModel.theme.observe(this, {
            changeStatusBarColor(if (it == Theme.LIGHT) android.R.color.white else R.color.dark_black)
        })

        if (eachCountryName == null) {
            addToolbarAndBottomNav()
            mainActivity.binding.navView.setOnNavigationItemReselectedListener {
                Timber.d("it.itemId is ${it.itemId} and this.id is $id")

                if (viewModel.arrayOfCurrentCountry.value?.get(0)?.Country == settingsViewModel.location.value) {
                    Timber.d("Location has not changed.")
                    swipeRefreshLayout.isRefreshing = true
                    Handler().postDelayed({ swipeRefreshLayout.isRefreshing = false }, 1000)
                    return@setOnNavigationItemReselectedListener
                }

                viewModel.loadData(eachCountryName ?: liveLocation.value)
            }
        }
    }

    private fun ImageView.loadCountryFlag(countryCode: String?) {
        val url = resources.getString(R.string.country_flag_url, countryCode)
        Timber.d("Url is $url")
        Glide.with(context)
            .load(if (countryCode == "GLO") R.drawable.ic_public else url)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
            )
            .centerCrop()
            .into(this)
    }
}
