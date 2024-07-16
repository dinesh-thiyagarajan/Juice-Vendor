package data

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    var drinkId: String,
    val drinkName: String,
    val drinkImage: String,
    var orderCount: Int = 0,
    val isAvailable: Boolean = false
)