package com.minerva.app.domain.model

/**
 * Perfil del usuario autenticado (fila 1:1 con `auth.users` en la tabla
 * `public.profiles`). El [nombre] es el único campo editable por el usuario;
 * el resto de la cuenta lo gestiona la institución.
 */
data class Profile(
    val id: String,
    val nombre: String?
)
