package com.octagon_technologies.covid19_statistics_app.main_activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.octagon_technologies.covid19_statistics_app.*
import com.octagon_technologies.covid19_statistics_app.databinding.ActivityMainBinding
import com.octagon_technologies.covid19_statistics_app.databinding.FragmentSettingsFragmentBinding
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var settingsLayout: FragmentSettingsFragmentBinding
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mainViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(applicationContext)
        ).get(MainViewModel::class.java)

        binding.lifecycleOwner = this
        binding.fragmentSettingsFragment.viewModel = mainViewModel
        binding.theme = mainViewModel.theme

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment)

        binding.menuBtn.setOnClickListener {
            settingsLayout.changeMenuLayoutVisibility(binding)
        }

        mainViewModel.theme.observe(this, {
            val countryName = binding.countryName.text.toString()
            Timber.d("Country in theme.observe() is $countryName")
            if (countryName != "--" && countryName.isNotEmpty()) {
                binding.countryName.getImageIcon(it, IconType.ArrowDown)
            }
        })

        mainViewModel.location.observe(this, {
            binding.countryName.apply {
                if (it.isNullOrEmpty()) {
                    Timber.d("savedLocation is $it and isNullOrEmpty")
                    text = "--"
                    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else {
                    Timber.d("savedLocation is $it")
                    binding.countryName.text = it
                    getImageIcon(mainViewModel.theme.value, IconType.ArrowDown)
                }
            }
        })

        settingsLayout = binding.fragmentSettingsFragment
        settingsLayout.openOrCloseMenuItem()

        settingsLayout.basicLayout.setOnClickListener {
            Timber.d("Basic Layout clicked")
            if (settingsLayout.root.visibility == View.VISIBLE) {
                settingsLayout.root.visibility = View.GONE
                binding.navView.visibility = View.VISIBLE
            }
        }

        binding.countryName.setOnClickListener {
            navController.navigate(R.id.findLocationFragment)
        }

        settingsLayout.contactUsBtn.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$DEVELOPER_EMAIL")
            }
            startActivity(Intent.createChooser(emailIntent, "Send Feedback"))
        }

        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (settingsLayout.root.visibility == View.GONE) super.onBackPressed()
        else settingsLayout.root.visibility = View.GONE
    }
}