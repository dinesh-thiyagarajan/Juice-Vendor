package juices.repositories

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import data.Config
import data.Drink
import data.Order
import data.Response
import data.Status
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuiceVendorRepository(
    private val firebaseDatabase: FirebaseDatabase = Firebase.database,
    private val ordersCollection: String = "${Config.BASE_LOCATION}/${Config.ORDERS_COLLECTION}",
    private val juicesCollection: String = "${Config.BASE_LOCATION}/${Config.JUICES_COLLECTION}",
) {

    suspend fun getDrinkOrders() = callbackFlow {
        val ordersList: MutableList<Order> = mutableListOf()
        try {
            val currentDateInnerCollection: String? = SimpleDateFormat(
                Config.DATE_FORMAT,
                Locale.getDefault()
            ).format(Date())

            val collectionReference =
                firebaseDatabase.reference("$ordersCollection/$currentDateInnerCollection")

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        ordersList.clear()
                        for (snapshot in dataSnapshot.children) {
                            val entryMap = snapshot.value as? Map<*, *>
                            entryMap?.entries?.forEach { entry ->
                                val entryValue = entry.value as? HashMap<*, *>
                                entryValue?.let {
                                    val drinkId = it["drinkId"]
                                    val drinkName = it["drinkName"]
                                    val orderCount = it["orderCount"]
                                    val orderTimeStamp = it["orderTimeStamp"]
                                    val order = Order(
                                        drinkId = drinkId.toString(),
                                        drinkName = drinkName.toString(),
                                        orderTimeStamp = orderTimeStamp as Long,
                                        orderCount = orderCount.toString().toInt()
                                    )
                                    ordersList.add(order)
                                }
                            }
                        }
                        ordersList.sortBy { it.orderTimeStamp }
                        trySend(ordersList.toList().reversed()).isSuccess
                    } catch (e: Exception) {
                        trySend(emptyList()).isSuccess
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    trySend(emptyList()).isSuccess
                }
            }

            collectionReference.android.addValueEventListener(valueEventListener)

            awaitClose {
                collectionReference.android.removeEventListener(valueEventListener)
            }
        } catch (e: Exception) {
            trySend(emptyList()).isSuccess
        }
    }


    suspend fun addNewDrink(drink: Drink) {
        try {
            val drinkMap = mapOf<String, Any>(
                "drinkId" to drink.drinkId,
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
            val dataSnapshot = ref.android.get().await()
            for (snapshot in dataSnapshot.children) {
                val drink = snapshot.getValue(Drink::class.java)
                drinksList.add(drink!!)
            }
            return Response(status = Status.Success, data = drinksList)
        } catch (ex: Exception) {
            return Response(status = Status.Error, message = ex.message)
        }
    }

    suspend fun getReportForDateInterval(datesList: List<String>): Response<Pair<Int, MutableList<Order>>> {
        val ordersList = mutableListOf<Order>()
        var totalOrderCount = 0
        try {
            val collectionReference = firebaseDatabase.reference(ordersCollection)
            repeat(datesList.size) {
                val dataSnapshot = collectionReference.child(datesList[it]).android.get().await()
                for (snapshot in dataSnapshot.children) {
                    val entryMap = snapshot.value as? Map<*, *>
                    entryMap?.entries?.forEach { entry ->
                        val entryValue = entry.value as? HashMap<*, *>
                        entryValue?.let { value ->
                            val drinkId = value["drinkId"]
                            val drinkName = value["drinkName"]
                            val orderCount = value["orderCount"].toString().toIntOrNull() ?: 0
                            val orderTimeStamp = value["orderTimeStamp"]
                            totalOrderCount += orderCount
                            val order = Order(
                                drinkId = drinkId.toString(),
                                drinkName = drinkName.toString(),
                                orderTimeStamp = orderTimeStamp as Long,
                                orderCount = orderCount
                            )
                            ordersList.add(order)
                        }
                    }
                }
            }
            return Response(status = Status.Success, data = Pair(totalOrderCount, ordersList))
        } catch (ex: Exception) {
            return Response(status = Status.Error, message = ex.message)
        }
    }

}