package com.minerva.app.presentation.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.verticalGradientBorder(
    cornerRadius: Dp,
    topColor: Color,
    bottomColor: Color,
    strokeWidth: Dp = 1.dp
): Modifier = this.drawWithContent {
    drawContent()
    drawRoundRect(
        brush = Brush.verticalGradient(listOf(topColor, bottomColor)),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = Stroke(width = strokeWidth.toPx())
    )
}

/**
 * Draws a glow border only at the four corners (no bleed on straight edges).
 * Each corner arc uses a local sweep gradient so it fades in/out within the arc.
 *
 * sweepGradient stops: 0.0 = 0° (east/right), 0.25 = 90° (south), 0.5 = 180° (west), 0.75 = 270° (north)
 * Arc angle convention: 0° = east, clockwise.
 */
fun Modifier.cornerGlowBorder(
    cornerRadius: Dp,
    color: Color = Color.White,
    peakAlpha: Float = 0.35f,
    strokeWidth: Dp = 1.dp
): Modifier = this.drawWithContent {
    drawContent()
    val r = cornerRadius.toPx()
    val sw = strokeWidth.toPx()
    val half = sw / 2f
    val arcSize = Size(r * 2 - sw, r * 2 - sw)
    val W = size.width
    val H = size.height
    val z = color.copy(alpha = 0f)
    val p = color.copy(alpha = peakAlpha)

    // Top-left: arc 180°→270°, peak at 225° (stop 0.625)
    drawArc(
        brush = Brush.sweepGradient(
            0.0f to z, 0.499f to z, 0.625f to p, 0.751f to z, 1.0f to z,
            center = Offset(r, r)
        ),
        startAngle = 180f, sweepAngle = 90f, useCenter = false,
        topLeft = Offset(half, half), size = arcSize, style = Stroke(sw)
    )

    // Top-right: arc 270°→360°, peak at 315° (stop 0.875)
    drawArc(
        brush = Brush.sweepGradient(
            0.0f to z, 0.749f to z, 0.875f to p, 1.0f to z,
            center = Offset(W - r, r)
        ),
        startAngle = 270f, sweepAngle = 90f, useCenter = false,
        topLeft = Offset(W - r * 2 + half, half), size = arcSize, style = Stroke(sw)
    )

    // Bottom-right: arc 0°→90°, peak at 45° (stop 0.125)
    drawArc(
        brush = Brush.sweepGradient(
            0.0f to z, 0.125f to p, 0.251f to z, 1.0f to z,
            center = Offset(W - r, H - r)
        ),
        startAngle = 0f, sweepAngle = 90f, useCenter = false,
        topLeft = Offset(W - r * 2 + half, H - r * 2 + half), size = arcSize, style = Stroke(sw)
    )

    // Bottom-left: arc 90°→180°, peak at 135° (stop 0.375)
    drawArc(
        brush = Brush.sweepGradient(
            0.0f to z, 0.249f to z, 0.375f to p, 0.501f to z, 1.0f to z,
            center = Offset(r, H - r)
        ),
        startAngle = 90f, sweepAngle = 90f, useCenter = false,
        topLeft = Offset(half, H - r * 2 + half), size = arcSize, style = Stroke(sw)
    )
}
