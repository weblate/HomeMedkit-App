package ru.application.homemedkit.network.models

import kotlinx.serialization.Serializable

@Serializable
data class MainModel(
    val codeFounded: Boolean,
    val category: String?,
    val code: String,
    val productName: String?,
    val expireDate: Long?,
    val screen: ScreenData? = null
) {
    val imageUrls: List<String>
        get() {
            val cardImages = screen?.items
                ?.firstOrNull { it.itemType == "group_card" }
                ?.images

            if (!cardImages.isNullOrEmpty()) return cardImages

            val pharmacyImage = screen?.items
                ?.firstOrNull { it.itemType == "pharmacy_search" }
                ?.pharmacyData?.image

            return pharmacyImage?.let { listOf(it) } ?: emptyList()
        }

    val attributes: Map<String, String>
        get() = screen?.items
            ?.filter { it.attrList != null }
            ?.flatMap { it.attrList!! }
            ?.associate { (it.label.orEmpty()) to (it.value.orEmpty()) }
            ?: emptyMap()

    val pharmacyInfo: PharmacyData?
        get() = screen?.items?.firstOrNull { it.itemType == "pharmacy_search" }?.pharmacyData
}

@Serializable
data class ScreenData(
    val items: List<ScreenItem>? = null
)

@Serializable
data class ScreenItem(
    val itemType: String,
    val images: List<String>? = null,
    val pharmacyData: PharmacyData? = null,
    val attrList: List<AttrItem>? = null
)

@Serializable
data class PharmacyData(
    val title: String? = null,
    val activeSubstance: String? = null,
    val form: String? = null,
    val dosage: String? = null,
    val quantity: String? = null,
    val image: String? = null
)

@Serializable
data class AttrItem(
    val label: String? = null,
    val value: String? = null
)