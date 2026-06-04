package com.minerva.app.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minerva.app.BuildConfig
import com.minerva.app.R
import com.minerva.app.presentation.components.MinervaDialog
import com.minerva.app.presentation.components.supportDescription
import com.minerva.app.presentation.theme.MinervaAccent
import com.minerva.app.presentation.theme.staggeredEnter

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val editState by viewModel.editState.collectAsStateWithLifecycle()

    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showSupport by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showAccountInfo by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    // Cierra el diálogo de edición cuando el guardado termina con éxito.
    LaunchedEffect(Unit) {
        viewModel.editSucceeded.collect { editingName = false }
    }

    if (editingName) {
        EditNameDialog(
            initial = state.rawName,
            isSaving = editState.isSaving,
            error = editState.error,
            onSave = { viewModel.updateName(it) },
            onDismiss = { editingName = false }
        )
    }
    if (showLogoutConfirm) {
        MinervaDialog(
            title = "¿Cerrar sesión?",
            description = AnnotatedString(
                "Tendrás que volver a iniciar sesión para acceder a tus evaluaciones."
            ),
            iconPainter = painterResource(R.drawable.ic_logout),
            confirmText = "Cerrar sesión",
            confirmIsDestructive = true,
            onConfirm = {
                showLogoutConfirm = false
                viewModel.logout()
            },
            onDismiss = { showLogoutConfirm = false }
        )
    }
    if (showSupport) {
        MinervaDialog(
            title = "Soporte",
            description = supportDescription(),
            iconPainter = painterResource(R.drawable.ic_envelope),
            onDismiss = { showSupport = false }
        )
    }
    if (showAccountInfo) {
        MinervaDialog(
            title = "Datos de la cuenta",
            description = androidx.compose.ui.text.buildAnnotatedString {
                append("Esta funcionalidad todavía no está disponible.\n\nPróximamente podrás consultar y gestionar los datos asociados a tu cuenta desde aquí.")
            },
            iconPainter = painterResource(R.drawable.ic_user),
            onDismiss = { showAccountInfo = false }
        )
    }
    if (showAbout) {
        MinervaDialog(
            title = "Minerva ${BuildConfig.VERSION_NAME}",
            description = androidx.compose.ui.text.buildAnnotatedString {
                append("Plataforma de predicción académica basada en inteligencia artificial.\n\n")
                append("Desarrollada para ayudar a docentes a identificar alumnos en riesgo de forma temprana.\n\n")
                append("© 2025 Minerva · Primera release alpha")
            },
            iconPainter = painterResource(R.drawable.ic_info),
            onDismiss = { showAbout = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Opciones",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.staggeredEnter(visible, 0)
        )

        Spacer(Modifier.height(20.dp))
        ProfileHeaderCard(
            displayName = state.displayName,
            email = state.email,
            initials = state.initials,
            onEdit = {
                viewModel.startEditing()
                editingName = true
            },
            modifier = Modifier.staggeredEnter(visible, 80)
        )

        Spacer(Modifier.height(24.dp))
        SectionLabel("Cuenta", Modifier.staggeredEnter(visible, 140))
        Spacer(Modifier.height(10.dp))
        OptionRow(
            icon = painterResource(R.drawable.ic_user),
            title = "Datos de la cuenta",
            subtitle = "Email gestionado por tu institución · solo lectura",
            onClick = { showAccountInfo = true },
            modifier = Modifier.staggeredEnter(visible, 170)
        )

        Spacer(Modifier.height(24.dp))
        SectionLabel("Aplicación", Modifier.staggeredEnter(visible, 220))
        Spacer(Modifier.height(10.dp))
        OptionRow(
            icon = painterResource(R.drawable.ic_envelope),
            title = "Soporte",
            subtitle = "Contacta con el equipo de Minerva",
            onClick = { showSupport = true },
            modifier = Modifier.staggeredEnter(visible, 250)
        )
        Spacer(Modifier.height(10.dp))
        OptionRow(
            icon = painterResource(R.drawable.ic_info),
            title = "Versión",
            trailingText = BuildConfig.VERSION_NAME,
            onClick = { showAbout = true },
            modifier = Modifier.staggeredEnter(visible, 280)
        )

        Spacer(Modifier.height(28.dp))
        OutlinedButton(
            onClick = { showLogoutConfirm = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .staggeredEnter(visible, 320),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Cerrar sesión", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun ProfileHeaderCard(
    displayName: String,
    email: String,
    initials: String,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MinervaAccent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    painter = painterResource(R.drawable.ic_pencil),
                    contentDescription = "Editar nombre",
                    tint = MinervaAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun EditNameDialog(
    initial: String,
    isSaving: Boolean,
    error: String?,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(initial) }

    Dialog(onDismissRequest = { if (!isSaving) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Tu nombre",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Así aparecerás en Minerva.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(18.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    enabled = !isSaving,
                    isError = error != null,
                    placeholder = { Text("Nombre y apellidos") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss, enabled = !isSaving) {
                        Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(text) },
                        enabled = text.isNotBlank() && !isSaving,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MinervaAccent)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Guardar", fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier.padding(start = 4.dp)
    )
}

@Composable
private fun OptionRow(
    icon: Painter,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailingText: String? = null,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MinervaAccent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = icon, contentDescription = null, tint = MinervaAccent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
