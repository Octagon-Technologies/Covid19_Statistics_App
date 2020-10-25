package com.octagon_technologies.covid19moniterapp.main_activity

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
import com.octagon_technologies.covid19moniterapp.DEVELOPER_EMAIL
import com.octagon_technologies.covid19moniterapp.R
import com.octagon_technologies.covid19moniterapp.changeMenuLayoutVisibility
import com.octagon_technologies.covid19moniterapp.databinding.ActivityMainBinding
import com.octagon_technologies.covid19moniterapp.databinding.FragmentSettingsFragmentBinding
import com.octagon_technologies.covid19moniterapp.openOrCloseMenuItem
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