package com.example.foodordering.Repository

import com.example.foodordering.domain.SavedAddressModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    fun getSavedAddresses(userId: String): Flow<List<SavedAddressModel>> = callbackFlow {
        val ref = firebaseDatabase.getReference("Users").child(userId).child("addresses")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<SavedAddressModel>()
                for (item in snapshot.children) {
                    val address = item.getValue(SavedAddressModel::class.java)
                    if (address != null) {
                        list.add(address)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun saveAddress(userId: String, address: SavedAddressModel): Result<Unit> {
        return try {
            val ref = firebaseDatabase.getReference("Users").child(userId).child("addresses")
            val addressId = if (address.id.isBlank()) {
                ref.push().key ?: System.currentTimeMillis().toString()
            } else {
                address.id
            }
            address.id = addressId

            if (address.isDefault) {
                clearDefaultAddresses(userId, addressId)
            }

            ref.child(addressId).setValue(address).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteAddress(userId: String, addressId: String): Result<Unit> {
        return try {
            val ref = firebaseDatabase.getReference("Users").child(userId).child("addresses").child(addressId)
            ref.removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setDefaultAddress(userId: String, addressId: String): Result<Unit> {
        return try {
            clearDefaultAddresses(userId, addressId)
            val ref = firebaseDatabase.getReference("Users").child(userId).child("addresses").child(addressId).child("default")
            ref.setValue(true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun clearDefaultAddresses(userId: String, exceptId: String) {
        val ref = firebaseDatabase.getReference("Users").child(userId).child("addresses")
        val snapshot = ref.get().await()
        for (item in snapshot.children) {
            val id = item.key
            if (id != null && id != exceptId) {
                item.child("default").ref.setValue(false).await()
            }
        }
    }
}
