package com.example.shots.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ValidationWarningsPanel(
    warnings: List<ValidationWarning>,
    modifier: Modifier = Modifier
) {
    if (warnings.isEmpty()) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "⚠️ Validaciones",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            warnings.forEach { warning ->
                ValidationWarningItem(warning)
            }
        }
    }
}

@Composable
private fun ValidationWarningItem(warning: ValidationWarning) {
    val backgroundColor = when (warning.severity) {
        WarningSeverity.ERROR -> MaterialTheme.colorScheme.errorContainer
        WarningSeverity.WARNING -> MaterialTheme.colorScheme.secondaryContainer
        WarningSeverity.INFO -> MaterialTheme.colorScheme.tertiaryContainer
    }
    
    val foregroundColor = when (warning.severity) {
        WarningSeverity.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        WarningSeverity.WARNING -> MaterialTheme.colorScheme.onSecondaryContainer
        WarningSeverity.INFO -> MaterialTheme.colorScheme.onTertiaryContainer
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = warning.emoji,
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = warning.message,
            style = MaterialTheme.typography.labelSmall,
            color = foregroundColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ValidationBadge(severity: WarningSeverity, count: Int) {
    if (count == 0) return
    
    val backgroundColor = when (severity) {
        WarningSeverity.ERROR -> MaterialTheme.colorScheme.error
        WarningSeverity.WARNING -> MaterialTheme.colorScheme.secondary
        WarningSeverity.INFO -> MaterialTheme.colorScheme.tertiary
    }

    Row(
        modifier = Modifier
            .background(
                color = backgroundColor.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = backgroundColor
        )
    }
}
