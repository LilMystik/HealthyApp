package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GoalDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal_detail)

        // Получаем данные из Intent
        val goal = intent.getParcelableExtra<Goal>("goal")

        goal?.let {
            // Заполняем TextView соответствующими данными
            findViewById<TextView>(R.id.goalNameTextView).text = it.name
            findViewById<TextView>(R.id.goalDescriptionTextView).text = it.description
            findViewById<TextView>(R.id.goalCategoryTextView).text = it.category
            findViewById<TextView>(R.id.goalStartDateTextView).text = it.startDate.toString()
            findViewById<TextView>(R.id.goalEndDateTextView).text = it.endDate.toString()
            findViewById<TextView>(R.id.goalResultTextView).text = "Result: ${it.result}"
        }
    }
}