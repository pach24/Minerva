package com.minerva.app.presentation.navigation

object Routes {
    // Nivel raíz
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MAIN = "main"

    // Pestañas del shell con barra inferior
    const val HOME = "home"
    const val EVALUAR = "evaluar"
    const val OPCIONES = "opciones"

    // Detalle de alumno (sub-pantalla dentro de Evaluar)
    const val DETAIL_ARG_INDEX = "index"
    const val DETAIL = "detalle/{$DETAIL_ARG_INDEX}"
    fun detail(index: Int) = "detalle/$index"
}
