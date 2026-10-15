package com.example.myapplication005

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication005.databinding.ActivitySummaryBinding

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Příjem dat z Intentu
        val model = intent.getStringExtra(EXTRA_MODEL).orEmpty()
        val redDot = intent.getBooleanExtra(EXTRA_RED_DOT, false)
        val longBarrel = intent.getBooleanExtra(EXTRA_LONG_BARREL, false)
        val license = intent.getBooleanExtra(EXTRA_LICENSE, false)
        val price = intent.getIntExtra(EXTRA_PRICE, 0)
        val start = intent.getStringExtra(EXTRA_START).orEmpty()
        val end = intent.getStringExtra(EXTRA_END).orEmpty()

        // Sestavení textového přehledu
        val extras = buildList {
            if (redDot) add(getString(R.string.option_red_dot))
            if (longBarrel) add(getString(R.string.option_long_barrel))
        }.ifEmpty { listOf(getString(R.string.extra_none)) }

        binding.tvSelectedBike.text = getString(R.string.summary_selected_pistol, model)
        binding.tvTime.text = getString(R.string.summary_time, start, end)
        binding.tvExtras.text = getString(R.string.summary_extras_title) + "\n" +
                extras.joinToString("\n") { "- $it" }
        binding.tvLicense.text = getString(
            R.string.summary_license,
            getString(if (license) R.string.answer_yes else R.string.answer_no)
        )
        binding.tvFinalPrice.text = getString(R.string.total_price, price)

        binding.btnBack.setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_MODEL = "extra_model"
        const val EXTRA_RED_DOT = "extra_red_dot"
        const val EXTRA_LONG_BARREL = "extra_long_barrel"
        const val EXTRA_LICENSE = "extra_license"
        const val EXTRA_PRICE = "extra_price"
        const val EXTRA_START = "extra_start"
        const val EXTRA_END = "extra_end"
    }
}