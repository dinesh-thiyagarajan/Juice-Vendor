package data

data class Report(
    val drinkId: String,
    val drinkName: String,
    val orderCount: Int,
    val orderTimeStamp: Long,
    var orderDate: String? = null
)