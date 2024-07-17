package data

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val timeStamp: String = System.currentTimeMillis().toString()
) {
    constructor() : this(
        id = "",
        name = "",
        email = "",
        role = "",
        timeStamp = ""
    )
}