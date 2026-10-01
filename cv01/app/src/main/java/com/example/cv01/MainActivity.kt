package com.example.cv01

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var tajneCislo = 0
    private var pocetPokusu = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etTip = findViewById<EditText>(R.id.etTip)
        val btnHadej = findViewById<Button>(R.id.btnHadej)
        val tvVysledek = findViewById<TextView>(R.id.tvVysledek)
        val tvPocetPokusu = findViewById<TextView>(R.id.tvPocetPokusu)

        // Vygenerování náhodného čísla 1-100
        tajneCislo = Random.nextInt(1, 101)

        btnHadej.setOnClickListener {
            val vstup = etTip.text.toString()

            if (vstup.isEmpty()) {
                tvVysledek.text = "Zadej nějaké číslo!"
                return@setOnClickListener
            }

            // Přetypování textu na číslo
            val tip = vstup.toIntOrNull()

            if (tip == null) {
                tvVysledek.text = "Zadej platné číslo!"
                return@setOnClickListener
            }

            pocetPokusu++
            tvPocetPokusu.text = "Počet pokusů: $pocetPokusu"

            // Větvení podle porovnání
            when {
                tip < tajneCislo -> tvVysledek.text = "Zkus to výš ⬆️"
                tip > tajneCislo -> tvVysledek.text = "Zkus to níž ⬇️"
                else -> {
                    tvVysledek.text = "Uhodl jsi! Číslo bylo $tajneCislo 🎉"
                    tajneCislo = Random.nextInt(1, 101) // nová hra
                    pocetPokusu = 0
                }
            }
        }
    }
}