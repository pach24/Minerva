package com.minerva.app.presentation.prediction

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minerva.app.R
import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.RiskLevel
import com.minerva.app.domain.model.Student
import com.minerva.app.presentation.prediction.components.StudentRow
import com.minerva.app.presentation.theme.MinervaAccent

@Composable
fun PredictionScreen(
    viewModel: PredictionViewModel,
    onOpenDetail: (Int) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val pickCsv = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        val text = context.contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
        if (text != null) viewModel.loadCsv(text)
    }
    val launchPicker = { pickCsv.launch("*/*") }

    LaunchedEffect(uiState) {
        if (uiState is PredictionUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (uiState as PredictionUiState.Error).message,
                duration = SnackbarDuration.Long
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Evaluar",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "Sube un CSV y analiza el riesgo de tu clase.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            AnimatedContent(
                targetState = uiState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentKey = { it::class },
                transitionSpec = {
                    (fadeIn(tween(260)) + slideInVertically(tween(260)) { it / 10 })
                        .togetherWith(fadeOut(tween(140)))
                },
                label = "predictionState"
            ) { state ->
                when (state) {
                    is PredictionUiState.Idle ->
                        IdleContent(onPickCsv = launchPicker)

                    is PredictionUiState.CsvLoaded ->
                        CsvLoadedContent(state.students, launchPicker) { viewModel.predict(state.students) }

                    is PredictionUiState.Predicting ->
                        PredictingContent()

                    is PredictionUiState.Results ->
                        ResultsContent(state.predictions, onReset = { viewModel.reset() }, onOpenDetail = onOpenDetail)

                    is PredictionUiState.Error -> {
                        val prev = state.prevStudents
                        if (prev != null)
                            CsvLoadedContent(prev, launchPicker) { viewModel.predict(prev) }
                        else
                            IdleContent(onPickCsv = launchPicker)
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun IdleContent(onPickCsv: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MinervaAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_cloud_upload),
                    contentDescription = null,
                    tint = MinervaAccent,
                    modifier = Modifier.size(44.dp)
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Sin alumnos cargados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Selecciona un archivo CSV con tus alumnos para empezar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            PrimaryButton(text = "Seleccionar CSV", onClick = onPickCsv, modifier = Modifier.fillMaxWidth(0.7f))
        }
    }
}

@Composable
private fun CsvLoadedContent(
    students: List<Student>,
    onPickCsv: () -> Unit,
    onPredict: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ListHeader(
            title = "${students.size} alumno${if (students.size != 1) "s" else ""}",
            actionText = "Cambiar CSV",
            onAction = onPickCsv
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            items(students) { student ->
                StudentCard(student = student, modifier = Modifier.animateItem())
            }
        }
        Spacer(Modifier.height(12.dp))
        PrimaryButton(text = "Predecir riesgo", onClick = onPredict, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun PredictingContent() {
    // Logo "respirando": escala y resplandor pulsan en bucle, como el overlay
    // de carga de la web (intelligence-glow).
    val breath = rememberInfiniteTransition(label = "breathing")
    val scale by breath.animateFloat(
        initialValue = 1f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )
    val glowAlpha by breath.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathGlow"
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = MinervaAccent.copy(alpha = glowAlpha),
                        spotColor = MinervaAccent.copy(alpha = glowAlpha)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.mi_logo),
                    contentDescription = null,
                    modifier = Modifier.size(56.dp)
                )
            }
            Spacer(Modifier.height(28.dp))
            Text(
                "Analizando estudiantes…",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Esto puede tardar unos segundos.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ResultsContent(
    predictions: List<Prediction>,
    onReset: () -> Unit,
    onOpenDetail: (Int) -> Unit,
) {
    val enRiesgo = predictions.count {
        it.nivelRiesgo == RiskLevel.ALTO || it.nivelRiesgo == RiskLevel.CRITICO
    }
    Column(modifier = Modifier.fillMaxSize()) {
        ListHeader(
            title = "Resultados",
            actionText = "Nueva",
            onAction = onReset
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = "${predictions.size} alumnos · $enRiesgo en riesgo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            itemsIndexed(predictions) { index, prediction ->
                StudentRow(
                    prediction = prediction,
                    onClick = { onOpenDetail(index) },
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
private fun StudentCard(student: Student, modifier: Modifier = Modifier) {
    val initials = student.nombre
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }
        .ifEmpty { "?" }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MinervaAccent.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MinervaAccent
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = student.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Alumno",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun ListHeader(title: String, actionText: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = actionText,
            style = MaterialTheme.typography.bodyMedium,
            color = MinervaAccent,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable(onClick = onAction)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = MinervaAccent)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}
