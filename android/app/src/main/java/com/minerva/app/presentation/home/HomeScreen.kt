package com.minerva.app.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minerva.app.R
import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.RiskLevel
import com.minerva.app.presentation.prediction.PredictionUiState
import com.minerva.app.presentation.theme.MinervaAccent
import com.minerva.app.presentation.theme.MinervaBlueDark
import com.minerva.app.presentation.theme.RiskAlto
import com.minerva.app.presentation.theme.RiskBajo
import com.minerva.app.presentation.theme.RiskCritico
import com.minerva.app.presentation.theme.scaleOnPress
import com.minerva.app.presentation.theme.staggeredEnter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    userName: String,
    predictionState: PredictionUiState,
    onEvaluarClick: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))

        // Cabecera con saludo
        Column(modifier = Modifier.staggeredEnter(visible, 0)) {
            Text(
                text = todayLabel(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Bienvenido, ${userName.ifBlank { "de nuevo" }}",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(24.dp))

        HeroCard(
            onClick = onEvaluarClick,
            modifier = Modifier.staggeredEnter(visible, 90)
        )

        Spacer(Modifier.height(20.dp))

        when (val state = predictionState) {
            is PredictionUiState.Results -> LastEvaluationCard(
                predictions = state.predictions,
                onClick = onEvaluarClick,
                modifier = Modifier.staggeredEnter(visible, 170)
            )
            else -> HowItWorksCard(
                modifier = Modifier.staggeredEnter(visible, 170)
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun HeroCard(onClick: () -> Unit, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .scaleOnPress(interaction)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(MinervaAccent, MinervaBlueDark)))
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
            .padding(24.dp)
    ) {
        // Marca de agua sutil
        Icon(
            painter = painterResource(R.drawable.ic_sparkles),
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.12f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(96.dp)
        )
        Column {
            Text(
                text = "Evaluar alumnos",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Sube un CSV y detecta el riesgo académico de tu clase con IA.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f)
            )
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Comenzar",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LastEvaluationCard(
    predictions: List<Prediction>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val criticos = predictions.count { it.nivelRiesgo == RiskLevel.CRITICO }
    val altos = predictions.count { it.nivelRiesgo == RiskLevel.ALTO }
    val seguros = predictions.count { it.nivelRiesgo == RiskLevel.BAJO }

    SectionCard(
        modifier = modifier
            .scaleOnPress(interaction)
            .clickable(interactionSource = interaction, indication = null, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Última evaluación",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Ver resultados",
                style = MaterialTheme.typography.bodyMedium,
                color = MinervaAccent,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = "${predictions.size} alumno${if (predictions.size != 1) "s" else ""} analizado${if (predictions.size != 1) "s" else ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RiskStat("Crítico", criticos, RiskCritico, Modifier.weight(1f))
            RiskStat("Riesgo alto", altos, RiskAlto, Modifier.weight(1f))
            RiskStat("Sin riesgo", seguros, RiskBajo, Modifier.weight(1f))
        }
    }
}

@Composable
private fun RiskStat(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HowItWorksCard(modifier: Modifier = Modifier) {
    SectionCard(modifier = modifier) {
        Text(
            text = "¿Cómo funciona?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(18.dp))
        StepRow(1, painterResource(R.drawable.ic_document), "Prepara tu CSV", "Una fila por alumno con sus notas y asistencia.")
        Spacer(Modifier.height(16.dp))
        StepRow(2, painterResource(R.drawable.ic_upload), "Súbelo en Evaluar", "Lo procesamos al instante, sin salir de la app.")
        Spacer(Modifier.height(16.dp))
        StepRow(3, painterResource(R.drawable.ic_check_badge), "Revisa el riesgo", "Cada alumno con su probabilidad de aprobar.")
    }
}

@Composable
private fun StepRow(number: Int, icon: Painter, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(MinervaAccent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(painter = icon, contentDescription = null, tint = MinervaAccent, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) { content() }
    }
}

private fun todayLabel(): String {
    val locale = Locale("es", "ES")
    val text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", locale))
    return text.replaceFirstChar { it.uppercaseChar() }
}
