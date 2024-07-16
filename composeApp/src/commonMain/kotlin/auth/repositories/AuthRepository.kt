package auth.repositories

import data.Response
import data.Status
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.auth

class AuthRepository(private val firebaseAuth: FirebaseAuth = Firebase.auth) {

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

    fun isAdmin(): Boolean = false
}