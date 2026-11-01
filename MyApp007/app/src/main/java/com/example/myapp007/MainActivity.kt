package com.example.myapp007

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.myapp007.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // View Binding pro activity_main.xml
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Odsazení obsahu od systémových lišt
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Při prvním spuštění zobrazíme výchozí fragment
        // (po otočení displeje už ho Android obnoví sám)
        if (savedInstanceState == null) {
            showFragment(FragmentModel())
        }

        // Každé tlačítko zobrazí svůj fragment
        binding.btnModel.setOnClickListener { showFragment(FragmentModel()) }
        binding.btnTime.setOnClickListener { showFragment(FragmentTime()) }
        binding.btnInfo.setOnClickListener { showFragment(FragmentInfo()) }
    }

    // Vymění obsah kontejneru za zadaný fragment
    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment) // nahradí aktuální obsah
            .commit()                                  // potvrdí a provede změnu
    }
}