package auth.repositories

import com.google.android.gms.tasks.Tasks
import data.Drink
import data.Response
import data.Status
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val firebaseDatabase: FirebaseDatabase = Firebase.database
) {

    suspend fun login(email: String, password: String): Response<Status> {
        try {
            val authResult =
                firebaseAuth.signInWithEmailAndPassword(email = email, password = password)
            if (authResult.user == null) {
                return Response(status = Status.Error, message = "Login Failed")
            }
            return Response(status = Status.Success, message = "Login Success")
        } catch (ex: Exception) {
            return Response(status = Status.Error, message = ex.message)
        }
    }

    fun isLoggedIn(): Boolean = Firebase.auth.currentUser != null

    suspend fun getAdminsList(collection: String) {
        val drinksList: MutableList<Drink> = mutableListOf()
        try {
            val ref = firebaseDatabase.reference("/$collection")
            val dataSnapshot = Tasks.await(ref.android.get())
            for (snapshot in dataSnapshot.children) {
                val drink = snapshot.getValue(Drink::class.java)
                drinksList.add(drink!!)
            }

        } catch (ex: Exception) {

        }
    }

    fun isAdmin(): Boolean = false
}