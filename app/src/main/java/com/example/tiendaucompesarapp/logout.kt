package com.example.tiendaucompesarapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class logout : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_logout)

        auth = FirebaseAuth.getInstance()

       // Obtiene el botón de registro
        val registerButton = findViewById<TextView>(R.id.registerButton)

        registerButton.setOnClickListener {
            // Obtiendo el texto de los campos EditText
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            val confirmPassword = findViewById<EditText>(R.id.confirmPasswordEditText).text.toString()

            // Compara el texto de las contraseñas
            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Comprobar que los campos no estén vacíos

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear la cuenta con Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()


                    val intent = Intent(this, login::class.java)
                    startActivity(intent)
                    finish()  // Finalizar la actividad actual
                } else {
                    // Registro fallido
                    Toast.makeText(this, "Error al registrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }


            // Texto de logeo
        val registerTextView = findViewById<TextView>(R.id.registerTextView)
        registerTextView.setOnClickListener {
            val intent = Intent(this, login ::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }
}