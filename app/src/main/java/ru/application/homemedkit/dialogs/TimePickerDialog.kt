package ru.application.homemedkit.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VibrantTimePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.application.homemedkit.R

@Composable
fun TimePickerDialog(onCancel: () -> Unit, onConfirm: () -> Unit, content: @Composable () -> Unit) =
    VibrantTimePickerDialog(
        onDismissRequest = onCancel,
        confirmButton = { TextButton(onConfirm) { Text(stringResource(R.string.text_save)) } },
        dismissButton = { TextButton(onCancel) { Text(stringResource(R.string.text_cancel)) } },
        content = {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.text_select_time),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                content()
            }
        }
    )