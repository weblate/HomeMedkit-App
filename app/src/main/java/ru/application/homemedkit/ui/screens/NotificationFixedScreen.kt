@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package ru.application.homemedkit.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.application.homemedkit.R
import ru.application.homemedkit.receivers.AlarmSetter
import ru.application.homemedkit.ui.elements.VectorIcon

@Composable
fun NotificationFixed() {
    val activity = LocalActivity.current ?: return

    val alarmManager = AlarmSetter.getInstance(activity)
    var isFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        alarmManager.resetAll()

        isFinished = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        content = {
            Crossfade(
                targetState = isFinished,
                animationSpec = tween(600),
                content = { finished ->
                    if (finished) AnimatedTick(activity::finishAndRemoveTask)
                    else LoadingIndicator()
                }
            )
        }
    )
}

@Composable
private fun AnimatedTick(onFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = keyframes {
                durationMillis = 1200

                0f at 0
                1.2f at 400 using FastOutSlowInEasing
                1f at 700 using LinearOutSlowInEasing
                1f at 1200
            }
        )

        onFinished()
    }

    VectorIcon(
        icon = R.drawable.vector_confirm,
        tint = Color(0xFF4CAF50),
        modifier = Modifier
            .size(128.dp)
            .scale(scale.value)
    )
}