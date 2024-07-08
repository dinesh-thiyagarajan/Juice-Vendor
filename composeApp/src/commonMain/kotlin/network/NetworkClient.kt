package network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


val httpClient = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
    install(WebSockets)
    HttpResponseValidator {
        validateResponse { response ->
            val statusCode = response.status.value
            if (statusCode >= 300) {
                throw ResponseException(
                    response = response,
                    cachedResponseText = "HTTP ${response.status.value}"
                )
            }
        }
        handleResponseExceptionWithRequest { exception, request ->
            when (exception) {
                is ClientRequestException -> {
                    // 4xx errors
                    println("Client request error: ${exception.response.status.description}")
                }

                is ServerResponseException -> {
                    // 5xx errors
                    println("Server response error: ${exception.response.status.description}")
                }

                is ResponseException -> {
                    // Other errors
                    println("Response error: ${exception.response.status.description}")
                }

                else -> {
                    // Generic exception handling
                    println("Unhandled error: ${exception.localizedMessage}")
                }
            }
        }

    }
}