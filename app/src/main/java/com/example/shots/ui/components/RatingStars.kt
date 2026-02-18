package com.example.shots.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun RatingStars(
    rating: Int,
    max: Int = 10,
    onRatingChange: (Int) -> Unit
) {
    val ratingColor = when {
        rating <= 3 -> Color(0xFFD32F2F)  // Rojo
        rating <= 7 -> Color(0xFFF57C00)  // Naranja/Amarillo
        else -> Color(0xFF388E3C)          // Verde
    }
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Slider(
            value = rating.toFloat(),
            onValueChange = { newValue -> onRatingChange(newValue.toInt()) },
            valueRange = 0f..max.toFloat(),
            steps = max - 1,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = ratingColor,
                activeTrackColor = ratingColor
            )
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Semáforo (círculo de color)
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(ratingColor, shape = CircleShape)
            )
            
            // Valor
            Text(
                text = "$rating / $max",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
