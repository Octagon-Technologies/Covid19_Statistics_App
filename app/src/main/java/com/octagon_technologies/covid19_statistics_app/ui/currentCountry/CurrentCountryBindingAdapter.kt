package com.octagon_technologies.covid19_statistics_app.ui.currentCountry

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.octagon_technologies.covid19_statistics_app.R
import com.octagon_technologies.covid19_statistics_app.Theme
import com.octagon_technologies.covid19_statistics_app.databinding.FragmentCurrentCountryBinding
import com.octagon_technologies.covid19_statistics_app.network.allCountries.EachCountry
import com.octagon_technologies.covid19_statistics_app.network.currentCountry.CurrentCountry
import com.octagon_technologies.covid19_statistics_app.ui.currentCountry.each_current_history_item.EachCurrentHistoryItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


fun RecyclerView.getCurrentCountryHistory(
    lifecycleOwner: LifecycleOwner,
    arrayOfCurrentCountry: ArrayList<CurrentCountry>?,
    liveTheme: LiveData<Theme>,
    groupAdapter: GroupAdapter<GroupieViewHolder>,
    nestedScrollView: NestedScrollView
) {
    val ioScope = CoroutineScope(Dispatchers.IO)
    val uiScope = CoroutineScope(Dispatchers.Main)
    val customLayoutManager = (layoutManager as LinearLayoutManager)

    var isJobActive = false
    var startPosition = 0
    Timber.d("getCurrentCountryHistory called with arrayOfCurrentCountry.size as ${arrayOfCurrentCountry?.size}")

    groupAdapter.clear()

    suspend fun addHistoryItems() {
        withContext(Dispatchers.IO) {
            Timber.d("start of addAllItems() with groupAdapter.itemCount as ${groupAdapter.itemCount}")
            val subList = try {
                arrayOfCurrentCountry?.subList(startPosition, startPosition + 30)
            } catch (indexException: IndexOutOfBoundsException) {
                arrayOfCurrentCountry?.subList(startPosition, arrayOfCurrentCountry.size)
            }

            uiScope.launch {
                subList?.forEach {
                    val eachCurrentHistoryItem =
                        EachCurrentHistoryItem(it, liveTheme.value)
                    groupAdapter.add(eachCurrentHistoryItem)
                }
            }
            Timber.d("end of addAllItems() with groupAdapter.itemCount as ${groupAdapter.itemCount}")
            startPosition += 30

            isJobActive = false
        }
    }

    fun doJob() {
        ioScope.launch {
            val lastVisibleItem = customLayoutManager.findLastCompletelyVisibleItemPosition()
            Timber.d("lastVisibleItem is $lastVisibleItem and groupAdapter.itemCount is ${groupAdapter.itemCount}")

            if (lastVisibleItem >= groupAdapter.itemCount - 10 && startPosition < arrayOfCurrentCountry?.size ?: 0) {
                Timber.d("Will load new items")
                addHistoryItems()
            } else isJobActive = false
        }
    }

    ioScope.launch {
        addHistoryItems()
        withContext(Dispatchers.Main) {
            nestedScrollView.scrollTo(0, 0)
        }
    }

    liveTheme.observe(lifecycleOwner, { theme ->
        Timber.d("liveTheme changed")
        (0..startPosition).forEach {
            groupAdapter.notifyItemChanged(it, theme)
        }
    })

    nestedScrollView.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { scrollView, _, scrollY, _, oldScrollY ->
        val nestedRecyclerView =
            (scrollView[0] as ConstraintLayout?)?.getViewById(R.id.history_recycler_view) as RecyclerView?
        Timber.d("nestedRecyclerView == null is ${nestedRecyclerView == null}")
        Timber.d("oldScrollY is $oldScrollY and scrollY is $scrollY")
        Timber.d("nestedRecyclerView.measuredHeight is ${nestedRecyclerView?.measuredHeight} and scrollView.measuredHeight is ${scrollView.measuredHeight}")

        nestedRecyclerView?.let {
            if (scrollY > oldScrollY && scrollY >= (nestedRecyclerView.measuredHeight - scrollView.measuredHeight)) {
                Timber.d("nestedScrollView scroll has increased")

                Timber.d("job.isActive is $isJobActive")
                if (!isJobActive) {
                    isJobActive = true
                    doJob()
                }
            }
        }
    })


}


fun FragmentCurrentCountryBinding.getFullData(
    currentCountryCode: String?,
    arrayOfAllCountries: ArrayList<EachCountry>?
) {
    val infectionRankingList = ArrayList<String>()
    val recoveryRankingList = ArrayList<String>()
    val deathRankingList = ArrayList<String>()

    arrayOfAllCountries?.sortedByDescending { it.NewConfirmed }
        ?.forEach { infectionRankingList.add(it.CountryCode) }
    arrayOfAllCountries?.sortedByDescending { it.NewRecovered }
        ?.forEach { recoveryRankingList.add(it.CountryCode) }
    arrayOfAllCountries?.sortedByDescending { it.NewDeaths }
        ?.forEach { deathRankingList.add(it.CountryCode) }

    val infectionPosition = infectionRankingList.indexOf(currentCountryCode) + 1
    val recoveryPosition = recoveryRankingList.indexOf(currentCountryCode) + 1
    val deathPosition = deathRankingList.indexOf(currentCountryCode) + 1

    Timber.d("infectionRanking is $infectionPosition")
    Timber.d("recoveryRanking is $recoveryPosition")
    Timber.d("deathRanking is $deathPosition")
    infectionRanking.text = root.resources.getString(
        R.string.out_of_format,
        infectionPosition,
        190
    )
    recoveryRanking.text = root.resources.getString(
        R.string.out_of_format,
        recoveryPosition,
        190
    )
    deathRanking.text = root.resources.getString(
        R.string.out_of_format,
        deathPosition,
        190
    )

    getOverallStatistics(currentCountryCode, arrayOfAllCountries)
}


private fun FragmentCurrentCountryBinding.getOverallStatistics(
    countryCode: String?,
    arrayOfAllCountries: ArrayList<EachCountry>?
) {
    arrayOfAllCountries?.forEach {
        if (it.CountryCode == countryCode) {
            Timber.d("getOverallStatistics.CountryCode == $countryCode")
            it.apply {
                newCasesDisplayText.text = NewConfirmed.toString()
                newRecoveriesDisplayText.text = NewRecovered.toString()
                newDeathDisplayText.text = NewDeaths.toString()

                totalCasesDisplayText.text = TotalConfirmed.toString()
                totalRecoveredDisplayText.text = TotalRecovered.toString()
                totalDeathDisplayText.text = TotalDeaths.toString()
            }
        }
    }
}