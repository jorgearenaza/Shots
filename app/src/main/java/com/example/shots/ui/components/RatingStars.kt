package com.example.shots.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme

@Composable
fun RatingStars(
    rating: Int,
    max: Int = 10,
    onRatingChange: (Int) -> Unit
) {
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
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "$rating / $max",
            style = MaterialTheme.typography.labelLarge
        )
    }
}
