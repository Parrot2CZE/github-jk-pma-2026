package com.example.dicecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicecompose.ui.theme.DiceComposeTheme // upravte podle názvu projektu
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DiceComposeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DiceScreen()
                }
            }
        }
    }
}

private val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

@Composable
fun DiceScreen() {
    var diceIndex by remember { mutableIntStateOf(0) }
    var isRolling by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Hoď kostkou",
            fontSize = 32.sp,
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = diceSymbols[diceIndex],
            fontSize = 160.sp,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            enabled = !isRolling,
            onClick = {
                scope.launch {
                    isRolling = true

                    repeat(10) {
                        delay(250)
                        diceIndex = Random.nextInt(diceSymbols.size)
                    }

                    delay(250)
                    diceIndex = Random.nextInt(diceSymbols.size)

                    isRolling = false
                }
            }
        ) {
            Text("Hodit")
        }
    }
}