package com.minerva.app.presentation.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import com.minerva.app.presentation.theme.MinervaAccent

data class BottomTab(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int,
)

/**
 * Barra de navegación inferior con un indicador en forma de píldora que se
 * despliega con un muelle bajo el icono activo. El icono cambia entre relleno y
 * contorno, escala suavemente y el color transiciona hacia el azul de marca.
 */
@Composable
fun MinervaBottomBar(
    tabs: List<BottomTab>,
    currentRoute: String?,
    onSelect: (BottomTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 14.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(66.dp)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                BottomBarItem(
                    tab = tab,
                    selected = currentRoute == tab.route,
                    onClick = { onSelect(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant

    val contentColor by animateColorAsState(
        targetValue = if (selected) MinervaAccent else unselectedColor,
        animationSpec = tween(220),
        label = "tabColor"
    )
    val pillWidth by animateDpAsState(
        targetValue = if (selected) 56.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessLow),
        label = "tabPill"
    )
    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.08f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "tabIconScale"
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = interaction,
                indication = null
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.height(32.dp)) {
            Box(
                modifier = Modifier
                    .width(pillWidth)
                    .height(32.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MinervaAccent.copy(alpha = 0.12f))
            )
            Icon(
                painter = painterResource(tab.icon),
                contentDescription = tab.label,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .graphicsLayer { scaleX = iconScale; scaleY = iconScale }
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}
