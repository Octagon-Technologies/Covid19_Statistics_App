package com.octagon_technologies.covid19moniterapp.ui.currentCountry

import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.databinding.FragmentCurrentCountryBinding
import com.octagon_technologies.covid19moniterapp.network.allCountries.EachCountry
import timber.log.Timber

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
        infectionRankingList.size + 1
    )
    recoveryRanking.text = root.resources.getString(
        R.string.out_of_format,
        recoveryPosition,
        recoveryRankingList.size + 1
    )
    deathRanking.text = root.resources.getString(
        R.string.out_of_format,
        deathPosition,
        deathRankingList.size + 1
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