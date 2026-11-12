package com.example.myapp008

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapp008.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

// Dashboard = výpis uloženého profilu.
// Data čteme z DataStore jako Flow -> obrazovka se sama aktualizuje,
// kdykoliv se uložená data změní (žádné ruční "obnovit").
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            // Sbíráme data jen když je obrazovka vidět (šetří baterii)
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                requireContext().dataStore.data.collect { prefs ->
                    val nickname = prefs[ProfileKeys.NICKNAME] ?: getString(R.string.no_nickname)
                    val hardcore = prefs[ProfileKeys.HARDCORE] ?: false
                    val count = prefs[ProfileKeys.SAVE_COUNT] ?: 0

                    binding.tvNickname.text = getString(R.string.dashboard_nickname, nickname)
                    binding.tvHardcore.text = getString(
                        R.string.dashboard_hardcore,
                        getString(if (hardcore) R.string.yes else R.string.no)
                    )
                    binding.tvSaveCount.text = getString(R.string.dashboard_save_count, count)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
