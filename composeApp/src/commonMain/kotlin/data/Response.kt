package data

import kotlinx.serialization.Serializable

@Serializable
data class Response<T>(
    val status: Status,
    val data: T? = null,
    val message: String? = null
)

enum class Status {
    Success,
    Error,
}