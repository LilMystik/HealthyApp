package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class AddGoalActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_goal)

        val goalNameEditText = findViewById<EditText>(R.id.goalNameEditText)
        val goalDescriptionEditText = findViewById<EditText>(R.id.goalDescriptionEditText)
        val goalCategorySpinner = findViewById<Spinner>(R.id.goalCategorySpinner)
        val startDateButton = findViewById<Button>(R.id.startDateButton)
        val endDateButton = findViewById<Button>(R.id.endDateButton)
        val resultEditText = findViewById<EditText>(R.id.resultEditText) // Новое поле
        val saveGoalButton = findViewById<Button>(R.id.saveGoalButton)

        val categories = listOf("Fitness", "Nutrition", "Mental Health", "Sleep")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        goalCategorySpinner.adapter = spinnerAdapter

        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        var startDate: LocalDate? = null
        var endDate: LocalDate? = null

        // Выбор начальной даты
        startDateButton.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                startDateButton.text = startDate?.format(dateFormatter) ?: "Select Start Date"
            }
        }

        // Выбор конечной даты
        endDateButton.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = selectedDate
                endDateButton.text = endDate?.format(dateFormatter) ?: "Select End Date"
            }
        }

        saveGoalButton.setOnClickListener {
            val name = goalNameEditText.text.toString()
            val description = goalDescriptionEditText.text.toString()
            val category = goalCategorySpinner.selectedItem.toString()

            // Проверяем, что дата выбрана перед ее использованием
            if (startDate == null || endDate == null) {
                Toast.makeText(this, "Please select both start and end dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val result = resultEditText.text.toString().toIntOrNull() ?: 0

            if (name.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val newGoal = Goal(
                    name = name,
                    description = description,
                    category = category,
                    startDate = startDate!!, // Убедитесь, что значение startDate и endDate не null
                    endDate = endDate!!,
                    result = result // Убираем progress
                )

                val resultIntent = Intent().apply {
                    putExtra("goal", newGoal)
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker(onDateSelected: (LocalDate) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(selectedDate)
        }, year, month, day).show()
    }
}
