package juices.repositories

import com.google.android.gms.tasks.Tasks
import data.Config
import data.Drink
import data.Order
import data.Response
import data.Status
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuiceVendorRepository(
    private val firebaseDatabase: FirebaseDatabase = Firebase.database,
    private val ordersCollection: String = "${Config.BASE_LOCATION}/${Config.ORDERS_COLLECTION}",
    private val juicesCollection: String = "${Config.BASE_LOCATION}/${Config.JUICES_COLLECTION}",
) {


    suspend fun getDrinkOrders(): Response<List<Order>> {
        val ordersList: MutableList<Order> = mutableListOf()
        try {
            val currentDateInnerCollection: String? = SimpleDateFormat(
                Config.DATE_FORMAT,
                Locale.getDefault()
            ).format(
                Date()
            )
            val ref =
                firebaseDatabase.reference("$ordersCollection/$currentDateInnerCollection")
            val dataSnapshot = Tasks.await(ref.android.orderByKey().get())
            for (snapshot in dataSnapshot.children) {
                (snapshot.value as Map<*, *>).entries.forEach { entry ->
                    val drinkId = (entry.value as HashMap<*, *>)["drinkId"]
                    val drinkName = (entry.value as HashMap<*, *>)["drinkName"]
                    val orderCount = (entry.value as HashMap<*, *>)["orderCount"]
                    val drinkImage = (entry.value as HashMap<*, *>)["drinkImage"]
                    val orderTimeStamp = (entry.value as HashMap<*, *>)["orderTimeStamp"]
                    val order = Order(
                        drinkId = drinkId.toString(),
                        drinkImage = drinkImage.toString(),
                        drinkName = drinkName.toString(),
                        orderTimeStamp = orderTimeStamp as Long,
                        orderCount = orderCount.toString().toInt()
                    )
                    ordersList.add(order)
                }
            }
            ordersList.sortBy { it.orderTimeStamp }
            return Response(status = Status.Success, data = ordersList)
        } catch (ex: Exception) {
            return Response(status = Status.Error, message = ex.message)
        }
    }

    suspend fun addNewDrink(drink: Drink) {
        try {
            val drinkMap = mapOf<String, Any>(
                "drinkId" to drink.drinkId,
                "drinkImage" to drink.drinkImage,
                "drinkName" to drink.drinkName,
                "isAvailable" to drink.isAvailable,
                "orderCount" to drink.orderCount,
            )
            val collectionRef =
                firebaseDatabase.reference(juicesCollection)
                    .child("/${drink.drinkId}")
            collectionRef.setValue(drinkMap, buildSettings = { encodeDefaults = true })
        } catch (ex: Exception) {
            // handle http, socket exceptions
            // TODO remove this try catch and handle this via interceptors
        }
    }

    suspend fun updateJuiceAvailability(
        drinkId: String,
        availability: Boolean
    ) {
        try {
            val availabilityMap = mapOf<String, Any>(
                "isAvailable" to availability
            )
            val collectionRef =
                firebaseDatabase.reference(juicesCollection)
                    .child("/${drinkId}")
            collectionRef.updateChildren(
                update = availabilityMap,
                buildSettings = { encodeDefaults = true })
        } catch (ex: Exception) {
            // handle http, socket exceptions
            // TODO remove this try catch and handle this via interceptors
        }
    }

    suspend fun getDrinksList(): Response<List<Drink>> {
        val drinksList: MutableList<Drink> = mutableListOf()
        try {
            val ref =
                firebaseDatabase.reference(juicesCollection)
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