package auth.repositories

import com.google.android.gms.tasks.Tasks
import data.Response
import data.Status
import data.User
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


    suspend fun addUser(user: User) {
        val userMap = mapOf<String, Any>(
            "id" to user.id,
            "email" to user.email,
            "role" to user.role,
            "timeStamp" to System.currentTimeMillis().toString()
        )

        val ref = firebaseDatabase.reference("/Users")
        ref.child(user.id).setValue(userMap) { encodeDefaults = true }
    }

    private suspend fun getCurrentLoggedInUserEmail() = Firebase.auth.currentUser?.email

    private suspend fun getUsersList(usersCollection: String): List<String> {
        val usersList: MutableList<String> = mutableListOf()
        try {
            val ref = firebaseDatabase.reference("/$usersCollection").orderByKey()
            val dataSnapshot = Tasks.await(ref.android.get())
            for (snapshot in dataSnapshot.children) {
                val user = snapshot.getValue(User::class.java)
                user?.email?.let { usersList.add(it) }
            }
        } catch (ex: Exception) {
            println(ex.message)
        }
        return usersList.toList()
    }

    suspend fun isAdmin(usersCollection: String): Boolean =
        getUsersList(usersCollection).contains(getCurrentLoggedInUserEmail())

}