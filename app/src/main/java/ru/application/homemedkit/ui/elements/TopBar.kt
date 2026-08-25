package ru.application.homemedkit.ui.elements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.application.homemedkit.R

@Composable
fun TopBarActions(
    isDefault: Boolean,
    setModifiable: () -> Unit,
    onSave: () -> Unit,
    onShowDialog: () -> Unit,
    onReloadImages: (() -> Unit)? = null,
    onDuplicate: (() -> Unit)? = null,
    onNavigate: (() -> Unit)? = null
) {
    @Composable
    fun LocalDropDownItem(@StringRes text: Int, @DrawableRes icon: Int, onClick: () -> Unit) =
        DropdownMenuItem(
            onClick = onClick,
            trailingIcon = { VectorIcon(icon) },
            text = {
                Text(
                    text = stringResource(text),
                    modifier = Modifier.widthIn(112.dp, 280.dp)
                )
            }
        )

    if (isDefault) {
        var expanded by remember { mutableStateOf(false) }

        if (onNavigate != null) {
            FilledIconButton(
                onClick = onNavigate,
                shapes = IconButtonDefaults.shapes(),
                content = { VectorIcon(R.drawable.vector_notification) }
            )
        }

        IconButton(
            onClick = { expanded = !expanded },
            content = { VectorIcon(R.drawable.vector_dropdown_more) }
        )

        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuGroup(MenuDefaults.groupShape(0, 1)) {
                if (onDuplicate != null) {
                    LocalDropDownItem(
                        text = R.string.text_to_duplicate,
                        icon = R.drawable.vector_duplicate,
                        onClick = {
                            onDuplicate()
                            expanded = false
                        }
                    )
                }

                LocalDropDownItem(
                    text = R.string.text_edit,
                    icon = R.drawable.vector_edit,
                    onClick = setModifiable
                )

                if (onReloadImages != null) {
                    LocalDropDownItem(
                        text = R.string.text_download_photos,
                        icon = R.drawable.vector_download,
                        onClick = {
                            onReloadImages()
                            expanded = false
                        }
                    )
                }

                LocalDropDownItem(
                    text = R.string.text_delete,
                    icon = R.drawable.vector_delete,
                    onClick = {
                        onShowDialog()
                        expanded = false
                    }
                )
            }
        }
    } else {
        OutlinedIconButton(onSave) { VectorIcon(R.drawable.vector_confirm) }
    }
}