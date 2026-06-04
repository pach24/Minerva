package com.minerva.app.presentation.util

/**
 * Helpers de presentación para el nombre del usuario.
 *
 * Cuando el perfil aún no tiene `nombre`, se deriva uno legible del correo como
 * *fallback*; las iniciales se calculan a partir del nombre que se muestre.
 */

private val SEPARATORS = charArrayOf('.', '_', '-')

fun emailToDisplayName(email: String): String {
    val local = email.substringBefore('@')
    val name = local.split(*SEPARATORS)
        .filter { it.isNotBlank() }
        .joinToString(" ") { part -> part.replaceFirstChar { it.uppercaseChar() } }
    return name.ifBlank { "Usuario" }
}

fun initialsFrom(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2)
        else -> "${parts[0].take(1)}${parts[1].take(1)}"
    }.uppercase()
}
