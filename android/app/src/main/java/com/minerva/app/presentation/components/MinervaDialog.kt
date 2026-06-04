package com.minerva.app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

private val DialogBlue = Color(0xFF1A73E8)

/**
 * Diálogo de marca reutilizable: icono en círculo + título + descripción.
 *
 * - Sin [onConfirm] → un único botón ([dismissText]) que cierra el diálogo.
 * - Con [onConfirm] → confirmación de dos acciones (botón principal + "Cancelar"),
 *   con estilo destructivo opcional para acciones como cerrar sesión.
 */
@Composable
fun MinervaDialog(
    title: String,
    description: AnnotatedString,
    onDismiss: () -> Unit,
    icon: ImageVector = Icons.Outlined.Lock,
    iconPainter: Painter? = null,
    confirmText: String? = null,
    onConfirm: (() -> Unit)? = null,
    confirmIsDestructive: Boolean = false,
    dismissText: String = "Entendido",
) {
    val accent = if (confirmIsDestructive) MaterialTheme.colorScheme.error else DialogBlue
    val iconCircle = accent.copy(alpha = 0.12f)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(iconCircle, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconPainter != null) {
                        Icon(
                            painter = iconPainter,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )

                Spacer(Modifier.height(24.dp))

                if (onConfirm == null) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DialogBlue)
                    ) {
                        Text(dismissText, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                } else {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = accent)
                    ) {
                        Text(
                            confirmText ?: "Aceptar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(
                            "Cancelar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

fun unavailableDescription(): AnnotatedString = AnnotatedString(
    "Lo sentimos, esta funcionalidad no está disponible por el momento. " +
    "Te invitamos a intentarlo más tarde."
)

fun registerDescription(): AnnotatedString = buildAnnotatedString {
    append("El acceso a Minerva está gestionado por tu institución educativa.\n\n")
    append("Contacta con tu centro o escríbenos a:\n\n")
    withStyle(SpanStyle(color = Color(0xFF1A73E8), fontWeight = FontWeight.Medium)) {
        append("soporte@minerva.app")
    }
}

fun supportDescription(): AnnotatedString = buildAnnotatedString {
    append("¿Necesitas ayuda con Minerva? Escríbenos y te responderemos lo antes posible:\n\n")
    withStyle(SpanStyle(color = Color(0xFF1A73E8), fontWeight = FontWeight.Medium)) {
        append("soporte@minerva.app")
    }
}
