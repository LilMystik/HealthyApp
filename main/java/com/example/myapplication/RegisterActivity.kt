package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val usernameEditText = findViewById<EditText>(R.id.registerUsernameEditText)
        val passwordEditText = findViewById<EditText>(R.id.registerPasswordEditText)
        val repeatPasswordEditText = findViewById<EditText>(R.id.registerRepeatPasswordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)

        val file = File(filesDir, "users.txt")

        registerButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val repeatPassword = repeatPasswordEditText.text.toString()

            // Проверка на заполнение всех полей
            if (username.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка совпадения паролей
            if (password != repeatPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка на наличие имени пользователя
            if (file.exists()) {
                val users = file.readLines()
                val isUsernameTaken = users.any { it.split(":")[0] == username } // Проверяем только имя

                if (isUsernameTaken) {
                    Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            // Сохранение данных в файл
            file.appendText("$username:$password\n")
            Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()

            // Переход на экран входа
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
