package ru.application.homemedkit.utils

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

class ResourceTextBuilder {
    private val elements = mutableListOf<ResourceText>()

    fun append(value: String) {
        elements.add(ResourceText.StaticString(value))
    }

    fun append(text: ResourceText) {
        if (text is ResourceText.MultiString) {
            elements.addAll(text.value)
        } else {
            elements.add(text)
        }
    }

    fun append(@StringRes resourceId: Int, vararg args: Any) {
        elements.add(ResourceText.StringResource(resourceId, *args))
    }

    fun appendPlural(@PluralsRes resourceId: Int, count: Int, vararg args: Any) {
        elements.add(ResourceText.PluralStringResource(resourceId, count, *args))
    }

    fun build() = when (elements.size) {
        0 -> ResourceText.StaticString(BLANK)
        1 -> elements.first()
        else -> ResourceText.MultiString(elements)
    }
}

inline fun buildResourceText(builderAction: ResourceTextBuilder.() -> Unit): ResourceText {
    return ResourceTextBuilder().apply(builderAction).build()
}