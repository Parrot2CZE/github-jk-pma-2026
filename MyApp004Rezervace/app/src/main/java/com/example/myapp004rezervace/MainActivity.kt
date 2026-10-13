package com.example.myapp004rezervace

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp004rezervace.databinding.ActivityMainBinding
import com.example.myapp004rezervace.databinding.DialogTimeRangeBinding
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (AppCompatDelegate.getApplicationLocales().isEmpty) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("cs"))
        }
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.root.post { updateImage() }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.radioGroup.setOnCheckedChangeListener { _, _ -> updateImage() }
        binding.checkRedDot.setOnCheckedChangeListener { _, _ -> updateImage() }
        binding.checkLongBarrel.setOnCheckedChangeListener { _, _ -> updateImage() }

        binding.buttonReserve.setOnClickListener { showTimePicker() }
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
        val image = pickImage(
            modelId,
            binding.checkRedDot.isChecked,
            binding.checkLongBarrel.isChecked
        )
        binding.imageWeapon.setImageResource(image)
    }

    private val prefs by lazy { getSharedPreferences("reservations", MODE_PRIVATE) }

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
                    showSummary(formatTime(start), formatTime(end))
                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private fun showSummary(start: String, end: String) {
        val model = when (binding.radioGroup.checkedRadioButtonId) {
            R.id.radioP10c -> getString(R.string.model_p10c)
            R.id.radioP09c -> getString(R.string.model_p09c)
            else -> getString(R.string.model_shadow2)
        }

        val extras = buildList {
            if (binding.checkRedDot.isChecked) add(getString(R.string.option_red_dot))
            if (binding.checkLongBarrel.isChecked) add(getString(R.string.option_long_barrel))
        }.ifEmpty { listOf(getString(R.string.summary_none)) }

        val license = getString(
            if (binding.checkLicense.isChecked) R.string.answer_yes else R.string.answer_no
        )

        binding.textSummary.text = getString(R.string.summary_title) + "\n" +
                getString(R.string.summary_weapon, model) + "\n" +
                getString(R.string.summary_extras, extras.joinToString(", ")) + "\n" +
                getString(R.string.summary_license, license) + "\n" +
                getString(R.string.summary_time, start, end)
    }
}