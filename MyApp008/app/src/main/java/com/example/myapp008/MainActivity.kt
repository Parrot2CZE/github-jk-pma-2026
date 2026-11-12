package com.example.myapp008 // Zkontrolujte svůj balíček!

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapp008.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pokud se aplikace zapíná poprvé, načteme Dashboard
        if (savedInstanceState == null) {
            showFragment(DashboardFragment())
        }

        binding.btnDashboard.setOnClickListener { showFragment(DashboardFragment()) }
        binding.btnSettings.setOnClickListener { showFragment(SettingsFragment()) }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
