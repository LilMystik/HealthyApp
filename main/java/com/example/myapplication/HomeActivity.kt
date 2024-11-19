package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.app.AlertDialog
import android.widget.Toast

data class Goal(
    val name: String,
    val description: String,
    val category: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val result: Int
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        LocalDate.parse(parcel.readString() ?: ""),
        LocalDate.parse(parcel.readString() ?: ""),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(category)
        parcel.writeString(startDate.toString())
        parcel.writeString(endDate.toString())
        parcel.writeInt(result)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Goal> {
        override fun createFromParcel(parcel: Parcel): Goal = Goal(parcel)
        override fun newArray(size: Int): Array<Goal?> = arrayOfNulls(size)
    }
}


class HomeActivity : AppCompatActivity() {

    private val goals = mutableListOf<Goal>()
    private lateinit var goalAdapter: ArrayAdapter<String>

    // Запуск AddGoalActivity с ожиданием результата
    private val addGoalLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val goal = result.data?.getParcelableExtra<Goal>("goal") // Получение данных
            goal?.let {
                // Добавление цели в список
                goals.add(it)
                updateGoalList("All")  // Обновляем список целей
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val categorySpinner = findViewById<Spinner>(R.id.categorySpinner)
        val goalListView = findViewById<ListView>(R.id.goalListView)
        val addGoalButton = findViewById<Button>(R.id.addGoalButton)

        // Категории здоровья
        val categories = listOf("All", "Fitness", "Nutrition", "Mental Health", "Sleep")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // Изначальное отображение всех целей
        goalAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, goals.map { it.name })
        goalListView.adapter = goalAdapter

        // Фильтрация целей по категории
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedCategory = categories[position]
                updateGoalList(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Обработчик нажатия кнопки добавления цели
        addGoalButton.setOnClickListener {
            val intent = Intent(this, AddGoalActivity::class.java)
            addGoalLauncher.launch(intent) // Запуск AddGoalActivity с ожиданием результата
        }

        // Отображение полной информации при нажатии на элемент списка
        goalListView.setOnItemClickListener { _, _, position, _ ->
            val goal = goals[position]

            // Создание строки с информацией
            val goalInfo = """
        Name: ${goal.name}
        Description: ${goal.description}
        Category: ${goal.category}
        Start Date: ${goal.startDate}
        End Date: ${goal.endDate}
        Result: ${goal.result}
    """.trimIndent()

            // Создаем AlertDialog для отображения информации
            val dialogBuilder = AlertDialog.Builder(this)
                .setTitle("Goal Information")
                .setMessage(goalInfo)  // Сообщение с информацией о цели
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }  // Кнопка закрытия
                .create()

            // Отображаем диалог
            dialogBuilder.show()
        }

        loadUserGoals()

        // Восстановление сохраненных данных
        if (savedInstanceState != null) {
            val savedGoals = savedInstanceState.getParcelableArrayList<Goal>("goals")
            savedGoals?.let {
                goals.clear()
                goals.addAll(it)
                updateGoalList("All")
            }
        }

    }

    private fun loadUserGoals() {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val username = sharedPreferences.getString("current_user", "") ?: return

        val userGoalsManager = UserGoalsManager(this)
        goals.clear()
        goals.addAll(userGoalsManager.loadGoals(username))
        updateGoalList("All")
    }

    private fun saveUserGoals() {
        val sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE)
        val username = sharedPreferences.getString("current_user", "") ?: return

        val userGoalsManager = UserGoalsManager(this)
        userGoalsManager.saveGoals(username, goals)
    }

    private fun updateGoalList(category: String) {
        val filteredGoals = if (category == "All") {
            goals
        } else {
            goals.filter { it.category == category }
        }
        goalAdapter.clear()
        goalAdapter.addAll(filteredGoals.map { it.name })
        goalAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        saveUserGoals() // Сохраняем цели при уходе с экрана
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем список целей в onSaveInstanceState
        outState.putParcelableArrayList("goals", ArrayList(goals))
    }


}