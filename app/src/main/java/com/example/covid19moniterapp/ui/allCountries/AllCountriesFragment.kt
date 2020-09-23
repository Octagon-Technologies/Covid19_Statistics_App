package com.example.covid19moniterapp.ui.allCountries

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.covid19moniterapp.MainActivity
import com.example.covid19moniterapp.R
import com.example.covid19moniterapp.database.DataBase
import com.example.covid19moniterapp.databinding.FragmentAllCountriesBinding
import com.example.covid19moniterapp.network.allCountries.Country
import com.example.covid19moniterapp.ui.currentCountry.RecyclerAdapter
import com.example.covid19moniterapp.viewmodel.SharedViewModel
import com.example.covid19moniterapp.viewmodel.SharedViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class AllCountriesFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    private lateinit var binding: FragmentAllCountriesBinding
    lateinit var adapter: AllRecyclerAdapter

    private val actionBarJob = Job()
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

        setHasOptionsMenu(true)

        adapter = AllRecyclerAdapter()
        viewModel.setAllCountries()
        try{
            viewModel.setAllCountries()
            adapter.data = viewModel.allCountriesData.value?.Countries as ArrayList<Country>?
        }
        catch (t: Throwable){
            Timber.e(t)
        }
        finally {
            viewModel.setAllCountries()
            adapter.data = viewModel.allCountriesData.value?.Countries as ArrayList<Country>?
        }

        val timer = object : CountDownTimer(5000, 500){
            override fun onFinish() {
                try{
                    viewModel.setAllCountries()
                    adapter.data = viewModel.allCountriesData.value?.Countries as ArrayList<Country>?
                }
                catch (t: Throwable){
                    Timber.e(t)
                }
                finally {
                    viewModel.setAllCountries()
                    adapter.data = viewModel.allCountriesData.value?.Countries as ArrayList<Country>?
                }
            }

            override fun onTick(millisUntilFinished: Long) {
                actionBarScope.launch {
                    (activity as MainActivity).supportActionBar?.title = "Summary"
                }
                try{
                    viewModel.setAllCountries()
                    adapter.data = viewModel.allCountriesData.value?.Countries as ArrayList<Country>?
                }
                catch (t: Throwable){
                    Timber.e(t)
                }
                finally {
                    viewModel.setAllCountries()
                    adapter.data = viewModel.allCountriesData.value?.Countries as ArrayList<Country>?
                }
            }
        }

        timer.start()

        binding.recyclerView.adapter = adapter

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)

        val searchItem = menu.findItem(R.id.search_icon)

        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.hint = "Search Here"

            searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    
                    val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchView.windowToken, 0)
                    
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    adapter.data?.clear()
                    viewModel.setAllCountries()
                    if (newText != null && newText.isNotEmpty()) {
                        viewModel.allCountriesData.value?.let {
                            for (country in it.Countries) {
                                if (country.Country.toLowerCase(Locale.ROOT).contains(newText.toLowerCase(Locale.ROOT))){
                                    adapter.data?.add(country)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }
                    }
                    else{
                     adapter.data?.clear()
                        adapter.data = viewModel.allCountriesData.value?.Countries as ArrayList<Country>?
                    }

                    return true
                }
            })
        }

        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        actionBarJob.cancel()
    }
}