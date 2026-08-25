package ru.application.homemedkit.utils.extensions

import androidx.compose.material3.DividerDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Density
import ru.application.homemedkit.utils.SnackbarPaddingState

fun Modifier.drawHorizontalDivider(
    color: Color,
    start: (DrawScope.() -> Offset) = { Offset(0f, size.height) },
    end: (DrawScope.() -> Offset) = { Offset(size.width, size.height) }
) = drawWithContent {
    drawContent()
    drawLine(
        color = color,
        strokeWidth = DividerDefaults.Thickness.toPx(),
        start = start(),
        end = end()
    )
}

fun Modifier.snackbarPadding(state: SnackbarPaddingState, density: Density) = onGloballyPositioned {
    state.height = with(density) { it.size.height.toDp() }
}
