package com.example.myapp008

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapp008.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Počítadlo pro navazující úkol (nesouvisí s ukládáním, žije jen v paměti)
    private var hits = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfile()

        binding.btnSave.setOnClickListener { saveProfile() }

        setupTraining()
    }

    // ---------- NAČTENÍ DAT ----------
    // Čtení také běží v korutině – first() vezme aktuální hodnotu z Flow a skončí.
    private fun loadProfile() {
        viewLifecycleOwner.lifecycleScope.launch {
            val prefs = requireContext().dataStore.data.first()
            binding.etNickname.setText(prefs[ProfileKeys.NICKNAME] ?: "")
            binding.switchHardcoreMode.isChecked = prefs[ProfileKeys.HARDCORE] ?: false
        }
    }

    // ---------- ULOŽENÍ DAT (asynchronně) ----------
    private fun saveProfile() {
        val nickname = binding.etNickname.text.toString().trim()
        val hardcore = binding.switchHardcoreMode.isChecked

        // viewLifecycleOwner.lifecycleScope = korutina se sama zruší,
        // když uživatel mezitím přepne na jiný fragment (nespadne na binding == null)
        viewLifecycleOwner.lifecycleScope.launch {
            // Správné UX: zablokovat tlačítko proti vícenásobnému kliknutí a ukázat kolečko
            binding.btnSave.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE

            // Zapamatujeme si původní hodnoty kvůli tlačítku "Zpět" ve Snackbaru
            val oldPrefs = requireContext().dataStore.data.first()
            val oldNickname = oldPrefs[ProfileKeys.NICKNAME]
            val oldHardcore = oldPrefs[ProfileKeys.HARDCORE]

            // Simulace těžké práce – delay() NEBLOKUJE hlavní vlákno (na rozdíl od Thread.sleep)
            delay(3000)

            // Samotný zápis – edit() je suspend funkce, zápis na disk proběhne na pozadí
            requireContext().dataStore.edit { prefs ->
                prefs[ProfileKeys.NICKNAME] = nickname
                prefs[ProfileKeys.HARDCORE] = hardcore
                prefs[ProfileKeys.SAVE_COUNT] = (prefs[ProfileKeys.SAVE_COUNT] ?: 0) + 1
            }

            binding.progressBar.visibility = View.INVISIBLE
            binding.btnSave.isEnabled = true

            // Snackbar místo Toastu – umí i akci (tlačítko "Zpět")
            Snackbar.make(binding.root, getString(R.string.saved_ok, nickname), Snackbar.LENGTH_LONG)
                .setAction(R.string.undo) { undoSave(oldNickname, oldHardcore) }
                .show()
        }
    }

    // "Zpět" = vrátíme původní hodnoty do DataStore i do formuláře
    private fun undoSave(oldNickname: String?, oldHardcore: Boolean?) {
        viewLifecycleOwner.lifecycleScope.launch {
            requireContext().dataStore.edit { prefs ->
                if (oldNickname == null) prefs.remove(ProfileKeys.NICKNAME)
                else prefs[ProfileKeys.NICKNAME] = oldNickname

                if (oldHardcore == null) prefs.remove(ProfileKeys.HARDCORE)
                else prefs[ProfileKeys.HARDCORE] = oldHardcore

                val count = prefs[ProfileKeys.SAVE_COUNT] ?: 0
                if (count > 0) prefs[ProfileKeys.SAVE_COUNT] = count - 1
            }
            binding.etNickname.setText(oldNickname ?: "")
            binding.switchHardcoreMode.isChecked = oldHardcore ?: false

            Snackbar.make(binding.root, R.string.undo_done, Snackbar.LENGTH_SHORT).show()
        }
    }

    // ---------- NAVAZUJÍCÍ ÚKOL: tréninková střelnice ----------
    // Terč (počítadlo zásahů) + posuvník "míření". Nijak nesouvisí s ukládáním,
    // ale díky tomu, že hlavní vlákno není blokované, reaguje okamžitě
    // i během toho, co se 3 sekundy točí kolečko.
    private fun setupTraining() {
        binding.tvHits.text = getString(R.string.hits, hits)
        binding.tvAim.text = getString(R.string.aim, binding.seekAim.progress)

        binding.btnTarget.setOnClickListener {
            hits++
            binding.tvHits.text = getString(R.string.hits, hits)
        }

        binding.seekAim.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvAim.text = getString(R.string.aim, progress)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
