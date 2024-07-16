package juices.repositories

import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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

class JuiceVendorRepository(private val firebaseDatabase: FirebaseDatabase = Firebase.database) {

    suspend fun getDrinkOrders(): Response<List<Order>> {
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return getDrinkOrders(formatter.format(Date()))
    }

    private suspend fun getDrinkOrders(collection: String): Response<List<Order>> {
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


    suspend fun subscribeToDrinkOrders(collection: String) = flow<Response<Status>> {
        val collectionReference = firebaseDatabase.reference("/$collection")
        collectionReference.android.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    suspend fun addNewDrink(drink: Drink, collection: String) {
        try {
            firebaseDatabase.reference("/$collection").setValue(drink) {}
        } catch (ex: Exception) {
            // handle http, socket exceptions
            // TODO remove this try catch and handle this via interceptors
        }
    }

}