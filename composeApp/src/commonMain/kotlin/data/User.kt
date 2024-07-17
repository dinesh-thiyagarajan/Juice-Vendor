package data

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: Role = Role.VENDOR,
    val timeStamp: String = System.currentTimeMillis().toString()
) {
    constructor() : this(
        id = "",
        name = "",
        email = "",
        role = Role.VENDOR,
        timeStamp = ""
    )
}

enum class Role {
    VENDOR,
    ADMIN
}