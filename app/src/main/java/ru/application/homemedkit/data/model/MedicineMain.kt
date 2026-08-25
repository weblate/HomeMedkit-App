package ru.application.homemedkit.data.model

import ru.application.homemedkit.utils.enums.DoseType

data class MedicineMain(
    val id: Long,
    val productName: String,
    val nameAlias: String,
    val prodAmount: Double,
    val doseType: DoseType,
    val expDate: Long,
    val prodFormNormName: String,
    val image: String?,
    val kitIdsString: String?
) {
    val kitIds: Set<Long> by lazy {
        if (kitIdsString.isNullOrBlank()) emptySet()
        else kitIdsString.splitToSequence(',')
            .mapNotNull(String::toLongOrNull)
            .toHashSet()
    }
}