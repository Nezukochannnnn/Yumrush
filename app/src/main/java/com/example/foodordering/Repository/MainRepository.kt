package com.example.foodordering.Repository

import com.example.foodordering.domain.CategoryModel
import com.example.foodordering.domain.FoodModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class MainRepository(
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
) {
    fun loadCategory(): Flow<List<CategoryModel>> = callbackFlow {
        val ref = firebaseDatabase.getReference("Category")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<CategoryModel>()
                for (item in snapshot.children) {
                    val model = item.getValue(CategoryModel::class.java)
                    if (model != null) {
                        list.add(model)
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

    fun loadBestFood(): Flow<List<FoodModel>> = callbackFlow {
        val ref = firebaseDatabase.getReference("Foods")
        val query: Query = ref.orderByChild("BestFood").equalTo(true)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<FoodModel>()
                for (item in snapshot.children) {
                    val model = item.getValue(FoodModel::class.java)
                    if (model != null) {
                        list.add(model)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }

    fun loadFilterFood(id: String?): Flow<List<FoodModel>> = callbackFlow {
        val ref = firebaseDatabase.getReference("Foods")
        val query: Query = if (!id.isNullOrEmpty()) {
            ref.orderByChild("CategoryId").equalTo(id)
        } else {
            ref
        }

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<FoodModel>()
                for (item in snapshot.children) {
                    val model = item.getValue(FoodModel::class.java)
                    if (model != null) {
                        list.add(model)
                    }
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        query.addValueEventListener(listener)
        awaitClose { query.removeEventListener(listener) }
    }
}