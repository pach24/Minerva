package com.minerva.app.presentation.prediction.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.minerva.app.domain.model.RiskLevel
import com.minerva.app.presentation.theme.RiskAlto
import com.minerva.app.presentation.theme.RiskBajo
import com.minerva.app.presentation.theme.RiskCritico
import com.minerva.app.presentation.theme.RiskMedio

@Composable
fun RiskBadge(level: RiskLevel, modifier: Modifier = Modifier) {
    val (bg, text) = when (level) {
        RiskLevel.BAJO -> RiskBajo to Color.White
        RiskLevel.MEDIO -> RiskMedio to Color.White
        RiskLevel.ALTO -> RiskAlto to Color.White
        RiskLevel.CRITICO -> RiskCritico to Color.White
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = level.label,
            style = MaterialTheme.typography.labelSmall,
            color = text
        )
    }
}
