package com.minerva.app.presentation.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Entrada escalonada: el contenido sube y aparece con un ligero retardo.
 *
 * Pensada para encadenar elementos (`delayMillis` creciente) y conseguir una
 * sensación "juicy" pero sobria. `visible` se activa una vez al montar la pantalla.
 */
@Composable
fun Modifier.staggeredEnter(
    visible: Boolean,
    delayMillis: Int = 0,
    slide: Dp = 22.dp,
): Modifier {
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else slide,
        animationSpec = tween(durationMillis = 480, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label = "staggerOffset"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 360, delayMillis = delayMillis),
        label = "staggerAlpha"
    )
    return this
        .alpha(alpha)
        .graphicsLayer { translationY = offsetY.toPx() }
}

/**
 * Reduce ligeramente la escala mientras el elemento está pulsado, con un muelle
 * que recupera el tamaño al soltar. Aporta tacto físico a tarjetas y botones.
 */
fun Modifier.scaleOnPress(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.97f,
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "pressScale"
    )
    graphicsLayer { scaleX = scale; scaleY = scale }
}
