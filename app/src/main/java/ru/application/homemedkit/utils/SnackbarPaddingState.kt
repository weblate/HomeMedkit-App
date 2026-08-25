package ru.application.homemedkit.utils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Stable
class SnackbarPaddingState(val hostState: SnackbarHostState) {
    internal var height by mutableStateOf(0.dp)

    @Composable
    fun getAnimatedPadding(): Dp {
        val isVisible = hostState.currentSnackbarData != null

        val animatedPadding by animateDpAsState(
            targetValue = if (isVisible) height else 0.dp,
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        )

        return animatedPadding
    }
}

@Composable
fun rememberSnackbarState(hostState: SnackbarHostState) = remember(hostState) {
    SnackbarPaddingState(hostState)
}