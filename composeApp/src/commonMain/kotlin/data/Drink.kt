package data

import kotlinx.serialization.Serializable

@Serializable
data class Drink(
    val drinkId: String,
    val drinkName: String,
    val drinkImage: String,
    var orderCount: Int = 0,
    @field:JvmField // this annotation is needed if the boolean prefix starts with "is"
    val isAvailable: Boolean = false,
    val nonAvailabilityReason: String? = null,
    val description: String? = null
) {
    constructor() : this(
        drinkId = "",
        drinkName = "",
        drinkImage = "",
        orderCount = 0,
        isAvailable = false,
        nonAvailabilityReason = "",
        description = "",
    )
}