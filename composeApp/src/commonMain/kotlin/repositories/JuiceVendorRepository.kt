package repositories

import data.Drink
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import network.ApiConstants.DATABASE_URL
import network.httpClient

class JuiceVendorRepository {

    suspend fun getDrinkOrders(): List<Drink> {
        return getDataFromFirebase("04-06-24")
    }

    private suspend fun getDataFromFirebase(collection: String): List<Drink> {
        val responseBody = httpClient
            .get("${DATABASE_URL}/${collection}.json") {
                header("Content-Type", "application/json")
            }

        val drinksList: ArrayList<Drink> = arrayListOf()
        val jsonElement: JsonElement = Json.parseToJsonElement(responseBody.bodyAsText())
        val values = jsonElement.jsonObject.values.toList()
        values.forEach { item ->
            (item as JsonArray).toList().forEach { drinkItem ->
                try {
                    val drink = Drink(
                        drinkId = drinkItem.jsonObject["drinkId"]?.jsonPrimitive?.int
                            ?: throw (Exception("Missing drinkId")),
                        drinkName = drinkItem.jsonObject["drinkName"]?.jsonPrimitive?.contentOrNull
                            ?: throw Exception("Missing drinkName"),
                        drinkImage = drinkItem.jsonObject["drinkImage"]?.jsonPrimitive?.contentOrNull
                            ?: throw Exception("Missing drinkImage"),
                        itemCount = drinkItem.jsonObject["itemCount"]?.jsonPrimitive?.int
                            ?: throw (Exception("Missing itemCount")),
                    )
                    drinksList.add(drink)
                } catch (e: Exception) {
                    // Handle or ignore exceptions
                    println("Error parsing drink item: ${e.message}")
                }
            }
        }
        return drinksList
    }

}