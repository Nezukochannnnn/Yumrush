package com.example.foodordering.Repository

import com.example.foodordering.domain.OrderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class OrderRepository(
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    suspend fun placeOrder(order: OrderModel): Result<OrderModel> {
        return try {
            val ref = firebaseDatabase.getReference("Orders")
                .child(order.userId)
                .child(order.orderId)
            ref.setValue(order).await()
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(userId: String, orderId: String, newStatus: String): Result<Unit> {
        return try {
            val ref = firebaseDatabase.getReference("Orders")
                .child(userId)
                .child(orderId)
                .child("status")
            ref.setValue(newStatus).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getOrders(userId: String): Flow<List<OrderModel>> = callbackFlow {
        val ref = firebaseDatabase.getReference("Orders").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<OrderModel>()
                for (item in snapshot.children) {
                    val order = item.getValue(OrderModel::class.java)
                    if (order != null) {
                        list.add(order)
                    }
                }
                list.sortByDescending { it.timestamp }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    fun getOrder(userId: String, orderId: String): Flow<OrderModel?> = callbackFlow {
        val ref = firebaseDatabase.getReference("Orders").child(userId).child(orderId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(OrderModel::class.java)
                trySend(order)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }
}
