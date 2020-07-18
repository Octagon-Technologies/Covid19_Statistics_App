package com.example.covid19moniterapp.ui.allCountries

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.covid19moniterapp.MainActivity
import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.databinding.FragmentAllCountriesBinding
import com.example.covid19moniterapp.viewmodel.SharedViewModel
import com.example.covid19moniterapp.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class AllCountriesFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentAllCountriesBinding

    val actionBarJob = Job()
    val actionBarScope = CoroutineScope(Dispatchers.Main + actionBarJob)

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val database = DataBase.getInstance(application)

        viewModel = ViewModelProvider(this, SharedViewModelFactory(database!!)).get(SharedViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_countries, container, false)

        val adapter = AllRecyclerAdapter()
        viewModel.setAllCountries()
        try{
            viewModel.setAllCountries()
            adapter.data = viewModel.allCountriesData.value
        }
        catch (t: Throwable){
            Timber.e(t)
        }
        finally {
            viewModel.setAllCountries()
            adapter.data = viewModel.allCountriesData.value
        }

        val timer = object : CountDownTimer(5000, 500){
            override fun onFinish() {
                try{
                    viewModel.setAllCountries()
                    adapter.data = viewModel.allCountriesData.value
                }
                catch (t: Throwable){
                    Timber.e(t)
                }
                finally {
                    viewModel.setAllCountries()
                    adapter.data = viewModel.allCountriesData.value
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                actionBarScope.launch {
                    (activity as MainActivity).supportActionBar?.title = "Summary"
                }
                try{
                    viewModel.setAllCountries()
                    adapter.data = viewModel.allCountriesData.value
                }
                catch (t: Throwable){
                    Timber.e(t)
                }
                finally {
                    viewModel.setAllCountries()
                    adapter.data = viewModel.allCountriesData.value
                }
            }
        }

        timer.start()

        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionBarJob.cancel()
    }
}