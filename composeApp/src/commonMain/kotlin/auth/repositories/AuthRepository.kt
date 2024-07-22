package auth.repositories

import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import com.google.android.gms.tasks.Tasks
import data.Config
import data.Response
import data.Role
import data.Status
import data.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.database.FirebaseDatabase
import dev.gitlive.firebase.database.database

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = Firebase.auth,
    private val firebaseDatabase: FirebaseDatabase = Firebase.database,
    private val usersCollection: String = "${Config.BASE_LOCATION}/${Config.USERS_COLLECTION}"
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
            "name" to user.name,
            "role" to user.role,
            "timeStamp" to System.currentTimeMillis().toString()
        )

        val ref = firebaseDatabase.reference(usersCollection)
        ref.child(user.id).setValue(userMap) { encodeDefaults = true }
    }

    suspend fun deleteUser(user: User) {
        val ref = firebaseDatabase.reference(usersCollection)
        ref.child(user.id).removeValue()
    }

    private fun getCurrentLoggedInUserEmail(): String? {
        try {
            return Firebase.auth.currentUser?.email
        } catch (ex: Throwable) {
            println(ex.localizedMessage)
            return null
        }
    }

    private suspend fun checkAdminEmailList(
        currentUserMailId: String?
    ): Boolean {
        try {
            val ref = firebaseDatabase.reference("/$usersCollection").orderByKey()
            val dataSnapshot = Tasks.await(ref.android.get())
            for (snapshot in dataSnapshot.children) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    if (it.email == currentUserMailId?.toLowerCase(Locale.current) && it.role == Role.ADMIN) {
                        return true
                    }
                }
            }
        } catch (ex: Exception) {
            println(ex.message)
        }
        return false
    }

    private suspend fun isAdmin(): Boolean =
        checkAdminEmailList(getCurrentLoggedInUserEmail())

    suspend fun hasAdminAccess(): Boolean {
        return isAdmin() || isServiceAccount()
    }

    suspend fun allowDeletion(userEmail: String): Boolean =
        getCurrentLoggedInUserEmail() != userEmail && getCurrentLoggedInUserEmail() != Config.SERVICE_ACCOUNT_ID

    private suspend fun isServiceAccount(): Boolean =
        Config.SERVICE_ACCOUNT_ID == getCurrentLoggedInUserEmail()

    suspend fun getUsersList(): Response<List<User>> {
        val usersList: MutableList<User> = mutableListOf()
        try {
            val ref = firebaseDatabase.reference("/$usersCollection").orderByKey()
            val dataSnapshot = Tasks.await(ref.android.get())
            for (snapshot in dataSnapshot.children) {
                val user = snapshot.getValue(User::class.java)
                user?.let {
                    usersList.add(user)
                }
            }
            return Response(
                status = Status.Success,
                data = usersList,
                message = "Users fetched successfully"
            )
        } catch (ex: Exception) {
            return Response(
                status = Status.Error,
                message = ex.message
            )
        }
    }

}