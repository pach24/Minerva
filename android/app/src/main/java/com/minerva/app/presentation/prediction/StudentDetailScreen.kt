package com.minerva.app.presentation.prediction

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.RiskLevel
import com.minerva.app.domain.model.Student
import com.minerva.app.presentation.prediction.components.RiskBadge
import com.minerva.app.presentation.theme.RiskAlto
import com.minerva.app.presentation.theme.RiskBajo
import com.minerva.app.presentation.theme.RiskCritico
import com.minerva.app.presentation.theme.RiskMedio
import com.minerva.app.presentation.theme.staggeredEnter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDetailScreen(
    index: Int,
    viewModel: PredictionViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val prediction = (uiState as? PredictionUiState.Results)?.predictions?.getOrNull(index)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = prediction?.student?.nombre ?: "Detalle del alumno",
                        maxLines = 1,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (prediction == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "No hay datos de este alumno.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onBack) { Text("Volver") }
                }
            }
            return@Scaffold
        }

        StudentDetailContent(
            prediction = prediction,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        )
    }
}

@Composable
private fun StudentDetailContent(prediction: Prediction, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val student = prediction.student
    val riskColor = prediction.nivelRiesgo.color()

    val ras = listOf(
        "RA1" to student.notaRa1, "RA2" to student.notaRa2, "RA3" to student.notaRa3,
        "RA4" to student.notaRa4, "RA5" to student.notaRa5, "RA6" to student.notaRa6,
        "RA7" to student.notaRa7, "RA8" to student.notaRa8, "RA9" to student.notaRa9,
    ).mapNotNull { (label, value) -> value?.let { label to it } }

    val context = listOf(
        "Confianza" to student.confianza,
        "Motivación escolar" to student.motivacionEscolar,
        "Recursos en casa" to student.recursosCasa,
        "Nivel educativo familiar" to student.nivelEducativoFamilia,
    ).mapNotNull { (label, value) -> value?.let { label to it } }

    Column(modifier = modifier) {
        Spacer(Modifier.height(8.dp))

        RiskHeroCard(
            prediction = prediction,
            riskColor = riskColor,
            modifier = Modifier.staggeredEnter(visible, 0)
        )

        Spacer(Modifier.height(20.dp))

        SectionTitle("Resumen académico", Modifier.staggeredEnter(visible, 90))
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.staggeredEnter(visible, 110)) {
            val notaMedia = student.notaMediaShown()
            StatCard(
                label = "Nota media",
                value = notaMedia?.let { formatScore(it) } ?: "—",
                unit = if (notaMedia != null) "/ 10" else "",
                modifier = Modifier.weight(1f)
            )
            StatCard("Asistencia", attendanceLabel(student), "", Modifier.weight(1f))
            StatCard("Participación", student.participacion, "", Modifier.weight(1f))
        }

        if (student.notaPracticas != null) {
            Spacer(Modifier.height(12.dp))
            DetailCard(modifier = Modifier.staggeredEnter(visible, 150)) {
                LabeledValueRow("Nota de prácticas", "${formatScore(student.notaPracticas)} / 10")
            }
        }

        if (ras.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            SectionTitle("Resultados de aprendizaje", Modifier.staggeredEnter(visible, 180))
            Spacer(Modifier.height(12.dp))
            DetailCard(modifier = Modifier.staggeredEnter(visible, 200)) {
                ras.forEachIndexed { i, (label, value) ->
                    if (i > 0) Spacer(Modifier.height(14.dp))
                    RaRow(label = label, value = value, color = riskColor)
                }
            }
        }

        if (context.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))
            SectionTitle("Contexto del alumno", Modifier.staggeredEnter(visible, 230))
            Spacer(Modifier.height(12.dp))
            DetailCard(modifier = Modifier.staggeredEnter(visible, 250)) {
                context.forEachIndexed { i, (label, value) ->
                    if (i > 0) Spacer(Modifier.height(12.dp))
                    LabeledValueRow(label, value.toString())
                }
            }
        }

        if (student.feedbackProfesor.isNotBlank()) {
            Spacer(Modifier.height(20.dp))
            SectionTitle("Observaciones del profesor", Modifier.staggeredEnter(visible, 280))
            Spacer(Modifier.height(12.dp))
            DetailCard(modifier = Modifier.staggeredEnter(visible, 300)) {
                Text(
                    text = student.feedbackProfesor,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 21.sp
                )
            }
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun RiskHeroCard(prediction: Prediction, riskColor: Color, modifier: Modifier = Modifier) {
    DetailCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProbabilityRing(prob = prediction.probAprobado, color = riskColor)
            Spacer(Modifier.width(20.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Nivel de riesgo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                RiskBadge(prediction.nivelRiesgo)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = riskHint(prediction.nivelRiesgo),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun ProbabilityRing(prob: Double, color: Color, size: androidx.compose.ui.unit.Dp = 132.dp) {
    var start by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { start = true }
    val animated by animateFloatAsState(
        targetValue = if (start) (prob / 100.0).toFloat().coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "probRing"
    )

    Box(modifier = Modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = 13.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(this.size.width - stroke, this.size.height - stroke)
            val topLeft = Offset(inset, inset)
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f, sweepAngle = animated * 360f, useCenter = false,
                topLeft = topLeft, size = arcSize, style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${prob.roundToInt()}%",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "aprobado",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    DetailCard(modifier = modifier, padding = 14.dp) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (unit.isNotEmpty()) {
                Spacer(Modifier.width(2.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun RaRow(label: String, value: Double, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(44.dp)
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth((value / 10.0).toFloat().coerceIn(0f, 1f))
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(color)
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = formatScore(value),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.width(32.dp)
        )
    }
}

@Composable
private fun LabeledValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier
    )
}

@Composable
private fun DetailCard(
    modifier: Modifier = Modifier,
    padding: androidx.compose.ui.unit.Dp = 18.dp,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 3.dp
    ) {
        Column(modifier = Modifier.padding(padding)) { content() }
    }
}

private fun RiskLevel.color(): Color = when (this) {
    RiskLevel.BAJO -> RiskBajo
    RiskLevel.MEDIO -> RiskMedio
    RiskLevel.ALTO -> RiskAlto
    RiskLevel.CRITICO -> RiskCritico
}

private fun riskHint(level: RiskLevel): String = when (level) {
    RiskLevel.BAJO -> "El alumno presenta indicadores sólidos. Sin necesidad de intervención."
    RiskLevel.MEDIO -> "Algún indicador a vigilar. Conviene un seguimiento puntual."
    RiskLevel.ALTO -> "Varios indicadores en alerta. Se recomienda intervención."
    RiskLevel.CRITICO -> "Riesgo elevado de no superar el curso. Intervención prioritaria."
}

/**
 * Nota media a mostrar. Si el CSV no traía columna `nota_media` (llega 0), la
 * derivamos como la media de los RAs presentes — igual que hace el backend
 * (`ml/predict.py`) para el modelo. Devuelve null si no hay ningún dato.
 */
private fun Student.notaMediaShown(): Double? {
    if (notaMedia > 0.0) return notaMedia
    val ras = listOfNotNull(
        notaRa1, notaRa2, notaRa3, notaRa4, notaRa5,
        notaRa6, notaRa7, notaRa8, notaRa9
    )
    return ras.takeIf { it.isNotEmpty() }?.average()
}

private fun formatScore(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString()
    else String.format("%.1f", value)

private fun attendanceLabel(student: Student): String {
    val ratio = student.ratioAsistencia
    return if (ratio > 0.0) "${(ratio * 100).roundToInt()}%" else "—"
}
