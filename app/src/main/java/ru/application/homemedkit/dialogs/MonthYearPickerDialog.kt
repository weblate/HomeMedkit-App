package ru.application.homemedkit.dialogs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.style.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ru.application.homemedkit.R
import ru.application.homemedkit.R.string.text_cancel
import ru.application.homemedkit.R.string.text_save
import ru.application.homemedkit.ui.elements.IconButton
import ru.application.homemedkit.ui.elements.VectorIcon
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun MonthYear(
    confirm: (Int, Int) -> Unit,
    cancel: () -> Unit,
    currentMonth: Int = LocalDate.now().monthValue,
    currentYear: Int = LocalDate.now().year
) {
    val locale = remember { Locale.current.platformLocale }

    val selectedColor = MaterialTheme.colorScheme.secondary

    var selectedMonth by remember { mutableIntStateOf(currentMonth) }
    var selectedYear by remember { mutableIntStateOf(currentYear) }

    Dialog(cancel) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
        ) {
            Column(Modifier.padding(24.dp), Arrangement.spacedBy(16.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.Center, Alignment.CenterVertically) {
                    IconButton(
                        onClick = { selectedYear-- },
                        content = {
                            VectorIcon(
                                icon = R.drawable.vector_keyboard_arrow_left,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    )

                    Text(
                        text = selectedYear.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { selectedYear++ },
                        content = {
                            VectorIcon(
                                icon = R.drawable.vector_keyboard_arrow_right,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(Month.entries, Month::name) { month ->
                        val interactionSource = remember(::MutableInteractionSource)
                        val styleState = rememberUpdatedStyleState(interactionSource) {
                            it.isSelected = selectedMonth == month.value
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .selectable(
                                    indication = null,
                                    selected = styleState.isSelected,
                                    role = Role.RadioButton,
                                    interactionSource = interactionSource,
                                    onClick = { selectedMonth = month.value }
                                )
                                .styleable(styleState) {
                                    size(60.dp)
                                    clip()
                                    shape(CircleShape)

                                    background(Color.Transparent)

                                    selected {
                                        background(selectedColor)
                                    }
                                }
                        ) {
                            Text(
                                fontWeight = FontWeight.Medium,
                                color = if (styleState.isSelected) MaterialTheme.colorScheme.onSecondary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                text = month.getDisplayName(TextStyle.SHORT, locale)
                                    .uppercase()
                                    .removeSuffix(".")
                            )
                        }
                    }
                }

                Row(Modifier.fillMaxWidth(), Arrangement.End) {
                    TextButton(
                        onClick = cancel,
                        content = { Text(stringResource(text_cancel)) }
                    )

                    TextButton(
                        onClick = { confirm(selectedMonth, selectedYear) },
                        content = { Text(stringResource(text_save)) }
                    )
                }
            }
        }
    }
}