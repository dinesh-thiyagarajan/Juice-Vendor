package repositories

import data.Drink
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import network.ApiConstants.DATABASE_URL
import network.httpClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuiceVendorRepository {

    suspend fun getDrinkOrders(): List<Drink> {
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return getDrinkOrders(formatter.format(Date()))
    }

    private suspend fun getDrinkOrders(collection: String): List<Drink> {
        val drinksList: ArrayList<Drink> = arrayListOf()
        try {
            val responseBody = httpClient
                .get("${DATABASE_URL}/${collection}.json") {
                    header("Content-Type", "application/json")
                }

            val jsonElement: JsonElement = Json.parseToJsonElement(responseBody.bodyAsText())
            val values = jsonElement.jsonObject.values.toList()
            values.forEach { item ->
                (item as JsonArray).toList().forEach { drinkItem ->
                    try {
                        val drink = Drink(
                            drinkId = drinkItem.jsonObject["drinkId"]?.jsonPrimitive?.contentOrNull
                                ?: throw (Exception("Missing drinkId")),
                            drinkName = drinkItem.jsonObject["drinkName"]?.jsonPrimitive?.contentOrNull
                                ?: throw Exception("Missing drinkName"),
                            drinkImage = drinkItem.jsonObject["drinkImage"]?.jsonPrimitive?.contentOrNull
                                ?: throw Exception("Missing drinkImage"),
                            orderCount = drinkItem.jsonObject["itemCount"]?.jsonPrimitive?.int
                                ?: throw (Exception("Missing itemCount")),
                        )
                        drinksList.add(drink)
                    } catch (e: Exception) {
                        // Handle or ignore exceptions
                        println("Error parsing drink item: ${e.message}")
                    }
                }
            }
        } catch (ex: Exception) {
            // handle http, socket exceptions
            // TODO remove this try catch and handle this via interceptors
        }
        return drinksList
    }

    suspend fun addNewDrink(drink: Drink, collection: String) {
        try {
            val response = httpClient
                .post("${DATABASE_URL}/${collection}.json") {
                    header("Content-Type", "application/json")
                    setBody(drink)
                }
            println(response.request)
        } catch (ex: Exception) {
            // handle http, socket exceptions
            // TODO remove this try catch and handle this via interceptors
        }
    }

}