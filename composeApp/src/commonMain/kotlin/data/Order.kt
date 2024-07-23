package data

import kotlinx.serialization.Serializable

@Serializable
data class Order(
    var drinkId: String,
    val drinkName: String,
    var orderCount: Int = 0,
    @field:JvmField // this annotation is needed if the boolean prefix starts with "is"
    val isAvailable: Boolean = true,
    val orderTimeStamp: Long = 0L
)