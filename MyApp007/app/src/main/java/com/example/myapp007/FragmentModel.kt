package com.example.myapp007

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp007.databinding.FragmentModelBinding

class FragmentModel : Fragment() {

    // View Binding pro layout fragment_model.xml
    private lateinit var binding: FragmentModelBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentModelBinding.inflate(inflater, container, false)

        // Při každé změně výběru se přepne fotka
        binding.rgModel.setOnCheckedChangeListener { _, _ -> updatePhoto() }
        binding.cbRedDot.setOnCheckedChangeListener { _, _ -> updatePhoto() }
        binding.cbBarrel.setOnCheckedChangeListener { _, _ -> updatePhoto() }
        updatePhoto()

        return binding.root
    }

    // Vybere fotku podle modelu a vybraných doplňků
    private fun updatePhoto() {
        val redDot = binding.cbRedDot.isChecked
        val barrel = binding.cbBarrel.isChecked

        val photo = when {
            binding.rbP10.isChecked -> when {
                barrel -> R.drawable.cz_p10c_reddot_barrel
                redDot -> R.drawable.cz_p10c_reddot
                else -> R.drawable.cz_p10c
            }
            binding.rbP09.isChecked ->
                if (barrel) R.drawable.cz_p09c_barrel else R.drawable.cz_p09c
            else -> R.drawable.cz_shadow2_compact
        }
        binding.ivPistol.setImageResource(photo)
    }
}