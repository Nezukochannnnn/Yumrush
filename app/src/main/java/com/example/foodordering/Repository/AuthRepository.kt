package com.example.foodordering.Repository

import com.example.foodordering.domain.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: UserModel?
        get() = firebaseAuth.currentUser?.toUserModel()

    val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    fun authStateFlow(): Flow<UserModel?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toUserModel())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun signIn(email: String, password: String): Result<UserModel> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user?.toUserModel()
                ?: return Result.failure(Exception("Failed to retrieve user profile"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<UserModel> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user?.toUserModel()
                ?: return Result.failure(Exception("Failed to create user account"))
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    private fun FirebaseUser.toUserModel(): UserModel {
        return UserModel(
            uid = uid,
            email = email.orEmpty(),
            displayName = displayName.orEmpty()
        )
    }
}
