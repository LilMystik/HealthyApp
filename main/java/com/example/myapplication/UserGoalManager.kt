package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class UserGoalsManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_goals", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Сохранение списка целей для пользователя
    fun saveGoals(userId: String, goals: List<Goal>) {
        val goalsJson = gson.toJson(goals)
        sharedPreferences.edit().putString(userId, goalsJson).apply()
    }

    // Загрузка списка целей для пользователя
    fun loadGoals(userId: String): List<Goal> {
        val goalsJson = sharedPreferences.getString(userId, null) ?: return emptyList()
        val type = object : TypeToken<List<Goal>>() {}.type
        return gson.fromJson(goalsJson, type)
    }
}
