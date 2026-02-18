package com.example.shots.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RatingStars(
    rating: Int,
    max: Int = 10,
    onRatingChange: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        for (i in 1..max) {
            val filled = i <= rating
            IconButton(
                onClick = { onRatingChange(i) },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
