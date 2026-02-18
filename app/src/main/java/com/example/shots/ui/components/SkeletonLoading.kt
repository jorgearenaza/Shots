package com.example.shots.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SkeletonCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
    )
}

@Composable
fun SkeletonLine(
    modifier: Modifier = Modifier,
    width: Float = 1f,
    height: Float = 16f
) {
    Box(
        modifier = modifier
            .fillMaxWidth(width)
            .height(height.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
    )
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    width: Float = 1f,
    height: Float = 80f
) {
    val shimmerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    
    Box(
        modifier = modifier
            .fillMaxWidth(width)
            .height(height.dp)
            .background(
                color = shimmerColor,
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@Composable
fun SkeletonLoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp)
    ) {
        repeat(3) {
            SkeletonCard()
        }
    }
}
