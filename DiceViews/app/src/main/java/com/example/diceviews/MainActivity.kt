package com.example.diceviews

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

    private val handler = Handler(Looper.getMainLooper())

    private val animationSteps = 10
    private val delayMs = 250L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.llMain)
        ) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        val tvDice = findViewById<TextView>(R.id.tvDice)
        val btnRoll = findViewById<Button>(R.id.btnRoll)

        btnRoll.setOnClickListener {
            rollDice(tvDice, btnRoll)
        }
    }

    private fun rollDice(tvDice: TextView, btnRoll: Button) {
        btnRoll.isEnabled = false

        for (step in 1..animationSteps) {
            handler.postDelayed({
                tvDice.text = diceSymbols[Random.nextInt(diceSymbols.size)]
            }, step * delayMs)
        }

        handler.postDelayed({
            tvDice.text = diceSymbols[Random.nextInt(diceSymbols.size)]
            btnRoll.isEnabled = true
        }, (animationSteps + 1) * delayMs)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}