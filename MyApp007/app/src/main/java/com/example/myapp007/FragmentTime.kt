package com.example.myapp007

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.myapp007.databinding.FragmentTimeBinding

class FragmentTime : Fragment() {

    private lateinit var binding: FragmentTimeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeBinding.inflate(inflater, container, false)

        // Začátek 10:00-19:30, konec 10:30-20:00, krok 30 minut
        val startTimes = buildSlots(10 * 60, 19 * 60 + 30)
        val endTimes = buildSlots(10 * 60 + 30, 20 * 60)

        binding.spStart.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, startTimes
        )
        binding.spEnd.adapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, endTimes
        )

        binding.btnReserve.setOnClickListener {
            val startPos = binding.spStart.selectedItemPosition
            val endPos = binding.spEnd.selectedItemPosition

            // Konec na pozici i je o 30 minut později než začátek na pozici i,
            // takže konec je po začátku, pokud endPos >= startPos
            if (endPos < startPos) {
                binding.tvResult.setText(R.string.error_time)
            } else {
                binding.tvResult.text =
                    getString(R.string.time_result, startTimes[startPos], endTimes[endPos])
            }
        }

        return binding.root
    }

    // Vytvoří seznam časů ve formátu HH:mm po 30 minutách
    private fun buildSlots(fromMinutes: Int, toMinutes: Int): List<String> =
        (fromMinutes..toMinutes step 30).map { "%02d:%02d".format(it / 60, it % 60) }
}