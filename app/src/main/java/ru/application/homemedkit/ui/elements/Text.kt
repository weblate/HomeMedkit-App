@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalFoundationStyleApi::class)

package ru.application.homemedkit.ui.elements

import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.contentPadding
import androidx.compose.foundation.style.fillWidth
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TextDate(date: String) = Text(
    text = date,
    style = MaterialTheme.typography.titleMediumEmphasized,
    modifier = Modifier.styleable {
        fillWidth()
        contentPadding(16.dp, 12.dp)
    }
)