package ru.application.homemedkit.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp

@Stable
interface NavigationBarVisibility {
    val isVisible: Boolean

    fun show()
    fun hide()
}

private class InitialNavigationBarVisibility : NavigationBarVisibility {
    private val _isVisible = mutableStateOf(true)
    override val isVisible by _isVisible

    override fun show() {
        _isVisible.value = true
    }

    override fun hide() {
        _isVisible.value = false
    }
}

val LocalBarVisibility = compositionLocalOf<NavigationBarVisibility> {
    InitialNavigationBarVisibility()
}

val LocalSnackbarPadding = compositionLocalOf { 0.dp }

@Composable
fun rememberNavigationBarVisibility(): NavigationBarVisibility {
    return remember(::InitialNavigationBarVisibility)
}