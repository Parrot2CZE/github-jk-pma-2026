package com.example.myapplication005

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication005.databinding.ActivityMainBinding
import com.example.myapplication005.databinding.DialogTimeRangeBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val prefs by lazy { getSharedPreferences("reservations", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Při prvním spuštění nastav češtinu (výchozí zdroje zůstávají anglicky)
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("cs"))
        }

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Dynamický přepočet ceny a fotky při každé změně výběru
        binding.radioGroup.setOnCheckedChangeListener { _, _ -> refresh() }
        binding.checkRedDot.setOnCheckedChangeListener { _, _ -> refresh() }
        binding.checkLongBarrel.setOnCheckedChangeListener { _, _ -> refresh() }

        binding.buttonReserve.setOnClickListener { showTimePicker() }

        refresh()
        // Po změně jazyka se aktivita vytvoří znovu a stav se obnoví až po onCreate
        binding.root.post { refresh() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear) {
            prefs.edit().remove("items").apply()
            Toast.makeText(this, R.string.reservations_cleared, Toast.LENGTH_SHORT).show()
            return true
        }

        val languageTag = when (item.itemId) {
            R.id.action_cs -> "cs"
            R.id.action_en -> "en"
            R.id.action_de -> "de"
            else -> return super.onOptionsItemSelected(item)
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(languageTag))
        return true
    }


    private fun refresh() {
        updateImage()
        binding.textPrice.text = getString(R.string.total_price, calculatePrice())
    }

    private fun calculatePrice(): Int {
        var total = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.radioP10c -> PRICE_P10C
            R.id.radioP09c -> PRICE_P09C
            R.id.radioShadow2 -> PRICE_SHADOW2
            else -> 0
        }
        if (binding.checkRedDot.isChecked) total += PRICE_RED_DOT
        if (binding.checkLongBarrel.isChecked) total += PRICE_LONG_BARREL
        return total
    }

    private fun pickImage(modelId: Int, redDot: Boolean, barrel: Boolean): Int =
        when (modelId) {
            R.id.radioP10c -> when {
                barrel -> R.drawable.cz_p10c_reddot_barrel
                redDot -> R.drawable.cz_p10c_reddot
                else -> R.drawable.cz_p10c
            }
            R.id.radioP09c ->
                if (barrel) R.drawable.cz_p09c_barrel else R.drawable.cz_p09c
            else -> R.drawable.cz_shadow2_compact
        }

    private fun updateImage() {
        val modelId = binding.radioGroup.checkedRadioButtonId
        if (modelId == -1) return
        binding.imageWeapon.setImageResource(
            pickImage(modelId, binding.checkRedDot.isChecked, binding.checkLongBarrel.isChecked)
        )
    }


    private fun modelKey(modelId: Int): String = when (modelId) {
        R.id.radioP10c -> "p10c"
        R.id.radioP09c -> "p09c"
        else -> "shadow2"
    }

    private fun loadReservations(): Set<String> =
        prefs.getStringSet("items", emptySet()) ?: emptySet()

    private fun isReserved(model: String, start: Int, end: Int): Boolean =
        loadReservations().any { entry ->
            val (m, s, e) = entry.split(";")
            m == model && start < e.toInt() && s.toInt() < end
        }

    private fun saveReservation(model: String, start: Int, end: Int) {
        val updated = loadReservations().toMutableSet()
        updated.add("$model;$start;$end")
        prefs.edit().putStringSet("items", updated).apply()
    }

    private fun formatTime(minutes: Int): String =
        "%02d:%02d".format(minutes / 60, minutes % 60)


    private fun showTimePicker() {
        if (binding.radioGroup.checkedRadioButtonId == -1) {
            Toast.makeText(this, R.string.error_no_model, Toast.LENGTH_SHORT).show()
            return
        }

        val model = modelKey(binding.radioGroup.checkedRadioButtonId)
        val dialogBinding = DialogTimeRangeBinding.inflate(layoutInflater)

        // 10:00, 10:30, ... 20:00 v minutách od půlnoci
        val slots = (0..20).map { 10 * 60 + it * 30 }
        var endSlots = slots.drop(1)

        dialogBinding.spinnerStart.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            slots.dropLast(1).map { formatTime(it) }
        )

        // Konec je vždy až po začátku, takže nejde vybrat neplatný rozsah.
        fun updateEndTimes(startIndex: Int) {
            endSlots = slots.drop(startIndex + 1)
            dialogBinding.spinnerEnd.adapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_dropdown_item,
                endSlots.map { formatTime(it) }
            )
        }
        updateEndTimes(0)

        dialogBinding.spinnerStart.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    updateEndTimes(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.time_dialog_title)
            .setView(dialogBinding.root)
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val start = slots[dialogBinding.spinnerStart.selectedItemPosition]
                val end = endSlots[dialogBinding.spinnerEnd.selectedItemPosition]

                if (isReserved(model, start, end)) {
                    Toast.makeText(this, R.string.error_already_reserved, Toast.LENGTH_SHORT).show()
                } else {
                    saveReservation(model, start, end)
                    openSummary(formatTime(start), formatTime(end))
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun openSummary(start: String, end: String) {
        val modelName = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.radioP10c -> getString(R.string.model_p10c)
            R.id.radioP09c -> getString(R.string.model_p09c)
            else -> getString(R.string.model_shadow2)
        }

        val intent = Intent(this, SummaryActivity::class.java).apply {
            putExtra(SummaryActivity.EXTRA_MODEL, modelName)
            putExtra(SummaryActivity.EXTRA_RED_DOT, binding.checkRedDot.isChecked)
            putExtra(SummaryActivity.EXTRA_LONG_BARREL, binding.checkLongBarrel.isChecked)
            putExtra(SummaryActivity.EXTRA_LICENSE, binding.checkLicense.isChecked)
            putExtra(SummaryActivity.EXTRA_PRICE, calculatePrice())
            putExtra(SummaryActivity.EXTRA_START, start)
            putExtra(SummaryActivity.EXTRA_END, end)
        }
        startActivity(intent)
    }

    companion object {
        private const val PRICE_P10C = 300
        private const val PRICE_P09C = 300
        private const val PRICE_SHADOW2 = 400
        private const val PRICE_RED_DOT = 100
        private const val PRICE_LONG_BARREL = 150
    }
}