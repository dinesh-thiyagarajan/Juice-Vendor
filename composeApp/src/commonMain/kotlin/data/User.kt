package data

data class User(val id: String, val email: String, val role: String) {
    constructor() : this(
        id = "",
        email = "",
        role = ""
    )
}