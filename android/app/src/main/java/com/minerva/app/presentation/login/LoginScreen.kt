package com.minerva.app.presentation.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minerva.app.R
import com.minerva.app.presentation.components.MinervaDialog
import com.minerva.app.presentation.components.registerDescription
import com.minerva.app.presentation.components.unavailableDescription
import com.minerva.app.presentation.theme.MinervaBlue
import com.minerva.app.presentation.theme.MinervaTheme

private val ButtonBlue = Color(0xFF1A73E8)

private enum class LoginDialog { ForgotPassword, Google, Apple, Register }

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) onLoginSuccess()
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    LoginScreenContent(
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        isLoading = uiState is LoginUiState.Loading,
        errorMessage = (uiState as? LoginUiState.Error)?.message,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onPasswordVisibleToggle = { passwordVisible = !passwordVisible },
        onLoginClick = { viewModel.login(email.trim(), password) }
    )
}

@Composable
private fun LoginScreenContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleToggle: () -> Unit,
    onLoginClick: () -> Unit,
) {
    val fieldShape = RoundedCornerShape(12.dp)
    val outline = MaterialTheme.colorScheme.outline
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val hint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val fieldSurface = MaterialTheme.colorScheme.surface
    val labelColor = MaterialTheme.colorScheme.onSurface
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = outline,
        focusedBorderColor = ButtonBlue,
        unfocusedLeadingIconColor = muted,
        focusedLeadingIconColor = ButtonBlue,
        unfocusedContainerColor = fieldSurface,
        focusedContainerColor = fieldSurface,
        unfocusedTrailingIconColor = muted,
        focusedTrailingIconColor = muted,
    )

    var activeDialog by remember { mutableStateOf<LoginDialog?>(null) }

    when (activeDialog) {
        LoginDialog.ForgotPassword, LoginDialog.Google, LoginDialog.Apple -> {
            MinervaDialog(
                title = "Funcionalidad no disponible",
                description = unavailableDescription(),
                onDismiss = { activeDialog = null }
            )
        }
        LoginDialog.Register -> {
            MinervaDialog(
                title = "¿Quieres registrarte?",
                description = registerDescription(),
                onDismiss = { activeDialog = null }
            )
        }
        null -> Unit
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(56.dp))

            // Logo
            Icon(
                painter = painterResource(R.drawable.mi_logo),
                contentDescription = "Minerva",
                modifier = Modifier.size(186.dp),
                tint = Color.Unspecified
            )

            Spacer(Modifier.height(0.dp))

            Text(
                text = "Bienvenido",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Inicia sesión para continuar",
                fontSize = 18.sp,
                color = muted
            )

            Spacer(Modifier.height(36.dp))

            // Correo electrónico
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Correo electrónico",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = labelColor,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = { Text("tu@email.com", color = hint, fontSize = 15.sp) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Email, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = fieldShape,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(16.dp))

            // Contraseña
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Contraseña",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = labelColor
                    )
                    Text(
                        text = "¿Olvidaste tu contraseña?",
                        fontSize = 12.sp,
                        color = ButtonBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { activeDialog = LoginDialog.ForgotPassword }
                    )
                }
                Spacer(Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = { Text("••••••••", color = hint, fontSize = 15.sp) },
                    leadingIcon = {
                        Icon(Icons.Outlined.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    enabled = !isLoading,
                    shape = fieldShape,
                    colors = fieldColors,
                    trailingIcon = {
                        IconButton(onClick = onPasswordVisibleToggle) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(24.dp))

            // Botón iniciar sesión
            Button(
                onClick = onLoginClick,
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBlue,
                    disabledContainerColor = ButtonBlue.copy(alpha = 0.45f)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Divisor "o continúa con"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = outline)
                Text(
                    text = "  o continúa con  ",
                    fontSize = 13.sp,
                    color = muted
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = outline)
            }

            Spacer(Modifier.height(16.dp))

            // Botón Google
            OutlinedButton(
                onClick = { activeDialog = LoginDialog.Google },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, outline),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = fieldSurface)
            ) {
                Icon(
                    painter = painterResource(R.drawable.google_logo),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(10.dp))
                Text(text = "Continuar con Google", fontSize = 15.sp, color = labelColor, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(10.dp))

            // Botón Apple
            OutlinedButton(
                onClick = { activeDialog = LoginDialog.Apple },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, outline),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = fieldSurface)
            ) {
                Icon(
                    painter = painterResource(R.drawable.apple_logo),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
                Spacer(Modifier.width(10.dp))
                Text(text = "Continuar con Apple", fontSize = 15.sp, color = labelColor, fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(28.dp))

            // Link registro
            Text(
                text = buildAnnotatedString {
                    append("¿No tienes una cuenta? ")
                    withStyle(SpanStyle(color = MinervaBlue, fontWeight = FontWeight.SemiBold)) {
                        append("Regístrate")
                    }
                },
                fontSize = 14.sp,
                color = muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable { activeDialog = LoginDialog.Register }
            )

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    MinervaTheme {
        LoginScreenContent(
            email = "",
            password = "",
            passwordVisible = false,
            isLoading = false,
            errorMessage = null,
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibleToggle = {},
            onLoginClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, name = "Login – con error")
@Composable
private fun LoginScreenErrorPreview() {
    MinervaTheme {
        LoginScreenContent(
            email = "usuario@ejemplo.com",
            password = "123456",
            passwordVisible = false,
            isLoading = false,
            errorMessage = "Credenciales incorrectas",
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibleToggle = {},
            onLoginClick = {}
        )
    }
}
