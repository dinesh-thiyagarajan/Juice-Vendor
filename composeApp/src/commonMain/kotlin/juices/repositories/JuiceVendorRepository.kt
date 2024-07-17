package juices.repositories

import com.google.android.gms.tasks.Tasks
import data.Drink
import data.Order
import data.Response
import data.Status
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuiceVendorRepository(private val firebaseDatabase: FirebaseDatabase = Firebase.database) {

    suspend fun getDrinkOrders(): Response<List<Order>> {
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return getDrinkOrders(formatter.format(Date()))
    }

    private fun getDrinkOrders(collection: String): Response<List<Order>> {
        val ordersList: MutableList<Order> = mutableListOf()
        try {
            val ref = firebaseDatabase.reference("/$collection")
            val dataSnapshot = Tasks.await(ref.android.get())
            for (snapshot in dataSnapshot.children) {
                (snapshot.value as Map<*, *>).entries.forEach { entry ->
                    val drinkId = (entry.value as HashMap<*, *>)["drinkId"]
                    val drinkName = (entry.value as HashMap<*, *>)["drinkName"]
                    val orderCount = (entry.value as HashMap<*, *>)["orderCount"]
                    val drinkImage = (entry.value as HashMap<*, *>)["drinkImage"]
                    val order = Order(
                        drinkId = drinkId.toString(),
                        drinkImage = drinkImage.toString(),
                        drinkName = drinkName.toString(),
                        orderCount = orderCount.toString().toInt()
                    )
                    ordersList.add(order)
                }
            }
            return Response(status = Status.Success, data = ordersList)
        } catch (ex: Exception) {
            return Response(status = Status.Error, message = ex.message)
        }
    }

    suspend fun addNewDrink(drink: Drink, collection: String) {
        try {
            val drinkMap = mapOf<String, Any>(
                "drinkId" to drink.drinkId,
                "drinkImage" to drink.drinkImage,
                "drinkName" to drink.drinkName,
                "isAvailable" to drink.isAvailable,
                "orderCount" to drink.orderCount,
            )
            val collectionRef =
                firebaseDatabase.reference("/$collection").child("/${drink.drinkId}")
            collectionRef.setValue(drinkMap, buildSettings = { encodeDefaults = true })
        } catch (ex: Exception) {
            // handle http, socket exceptions
            // TODO remove this try catch and handle this via interceptors
        }
    }

    suspend fun updateJuiceAvailability(
        collection: String,
        drinkId: String,
        availability: Boolean
    ) {
        try {
            val availabilityMap = mapOf<String, Any>(
                "isAvailable" to availability
            )
            val collectionRef =
                firebaseDatabase.reference("/$collection").child("/${drinkId}")
            collectionRef.updateChildren(
                update = availabilityMap,
                buildSettings = { encodeDefaults = true })
        } catch (ex: Exception) {
            // handle http, socket exceptions
            // TODO remove this try catch and handle this via interceptors
        }
    }

    suspend fun getDrinksList(collection: String): Response<List<Drink>> {
        val drinksList: MutableList<Drink> = mutableListOf()
        try {
            val ref = firebaseDatabase.reference("/$collection")
            val dataSnapshot = Tasks.await(ref.android.get())
            for (snapshot in dataSnapshot.children) {
                val drink = snapshot.getValue(Drink::class.java)
                drinksList.add(drink!!)
            }
            return Response(status = Status.Success, data = drinksList)
        } catch (ex: Exception) {
            return Response(status = Status.Error, message = ex.message)
        }
    }

}