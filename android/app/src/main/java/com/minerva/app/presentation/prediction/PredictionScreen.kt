package com.minerva.app.presentation.prediction

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.Student
import com.minerva.app.presentation.prediction.components.StudentRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    onLogout: () -> Unit,
    viewModel: PredictionViewModel = hiltViewModel()
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

    LaunchedEffect(uiState) {
        if (uiState is PredictionUiState.Error) {
            snackbarHostState.showSnackbar(
                message = (uiState as PredictionUiState.Error).message,
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minerva") },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is PredictionUiState.Idle -> {
                    IdleContent(onPickCsv = { pickCsv.launch("*/*") })
                }

                is PredictionUiState.CsvLoaded -> {
                    CsvLoadedContent(
                        students = state.students,
                        onPickCsv = { pickCsv.launch("*/*") },
                        onPredict = { viewModel.predict(state.students) }
                    )
                }

                is PredictionUiState.Predicting -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text("Analizando estudiantes…")
                        }
                    }
                }

                is PredictionUiState.Results -> {
                    ResultsContent(
                        predictions = state.predictions,
                        onReset = { viewModel.reset() }
                    )
                }

                is PredictionUiState.Error -> {
                    val prev = state.prevStudents
                    if (prev != null) {
                        CsvLoadedContent(
                            students = prev,
                            onPickCsv = { pickCsv.launch("*/*") },
                            onPredict = { viewModel.predict(prev) }
                        )
                    } else {
                        IdleContent(onPickCsv = { pickCsv.launch("*/*") })
                    }
                }
            }
        }
    }
}

@Composable
private fun IdleContent(onPickCsv: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.UploadFile,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(16.dp))
            Text("Selecciona un CSV de alumnos", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(20.dp))
            Button(onClick = onPickCsv) {
                Text("Seleccionar CSV")
            }
        }
    }
}

@Composable
private fun CsvLoadedContent(
    students: List<Student>,
    onPickCsv: () -> Unit,
    onPredict: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "${students.size} alumno${if (students.size != 1) "s" else ""} cargados",
                style = MaterialTheme.typography.titleLarge
            )
            TextButton(onClick = onPickCsv) { Text("Cambiar CSV") }
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students) { student ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = student.nombre,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = onPredict,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Predecir riesgo")
        }
    }
}

@Composable
private fun ResultsContent(
    predictions: List<Prediction>,
    onReset: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Resultados (${predictions.size})", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onReset) { Text("Nueva predicción") }
        }
        Spacer(Modifier.height(8.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(predictions) { prediction ->
                StudentRow(prediction)
            }
        }
    }
}
