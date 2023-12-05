package com.example.vinonovi2.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun LoadingCirlce(text : String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = text
        , style = MaterialTheme.typography.bodyLarge)
        LinearProgressIndicator(
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.Red,
                            Color.Yellow,
                            Color.Green,
                            Color.Blue,
                            Color(0xFF4B0082), // Indigo
                            Color(0xFF9400D3)  // Violet
                        )
                    )
                ),
//                progress = 0.5f, // 0에서 1 사이의 값으로 설정
        )
    }
}