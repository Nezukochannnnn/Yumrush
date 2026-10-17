package com.example.foodordering.Helper

import android.content.Context
import android.widget.Toast
import com.example.foodordering.domain.FoodModel

class ManagmentCart(val context: Context) {

    private val tinyDB = TinyDB(context)

    fun insertItem(item: FoodModel) {
        val listFood = getListCart()
        val index = listFood.indexOfFirst { it.Title == item.Title }

        if (index != -1) {
            listFood[index].numberInCart = if (item.numberInCart > 0) {
                item.numberInCart
            } else {
                listFood[index].numberInCart + 1
            }
        } else {
            if (item.numberInCart <= 0) item.numberInCart = 1
            listFood.add(item)
        }
        tinyDB.putListObject("CartList", listFood)
        Toast.makeText(context, "Added to your Cart", Toast.LENGTH_SHORT).show()
    }

    fun getListCart(): ArrayList<FoodModel> {
        val list = tinyDB.getListObject("CartList") ?: arrayListOf()
        return ArrayList(list.filterNotNull())
    }

    fun minusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (position < 0 || position >= listFood.size) return
        val currentCount = listFood[position].numberInCart
        if (currentCount <= 1) {
            listFood.removeAt(position)
        } else {
            listFood[position].numberInCart = currentCount - 1
        }
        tinyDB.putListObject("CartList", listFood)
        listener.onChanged()
    }

    fun plusItem(listFood: ArrayList<FoodModel>, position: Int, listener: ChangeNumberItemsListener) {
        if (position < 0 || position >= listFood.size) return
        listFood[position].numberInCart++
        tinyDB.putListObject("CartList", listFood)
        listener.onChanged()
    }

    fun removeItem(position: Int, listener: ChangeNumberItemsListener) {
        val listFood = getListCart()
        if (position in listFood.indices) {
            listFood.removeAt(position)
            tinyDB.putListObject("CartList", listFood)
            listener.onChanged()
        }
    }

    fun clearCart() {
        tinyDB.putListObject("CartList", arrayListOf<FoodModel>())
    }

    fun getTotalFee(): Double {
        val listFood = getListCart()
        var fee = 0.0
        for (item in listFood) {
            fee += item.Price * item.numberInCart
        }
        return fee
    }
}