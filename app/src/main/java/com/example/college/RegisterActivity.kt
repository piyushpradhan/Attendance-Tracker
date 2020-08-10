package com.example.college

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var goToSignIn : TextView
    private lateinit var nameField: EditText
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText
    private lateinit var confirmPasswordField: EditText
    private lateinit var registerButton: Button

    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        goToSignIn = findViewById(R.id.go_to_signin)
        registerButton = findViewById(R.id.register_button)
        nameField = findViewById(R.id.register_name)
        emailField = findViewById(R.id.register_email)
        passwordField = findViewById(R.id.register_password)
        confirmPasswordField = findViewById(R.id.register_confirm_password)

        goToSignIn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_right_exit)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            register()
        }

    }

    private fun register() {
        if(!nameField.text.isEmpty() && !emailField.text.isEmpty() && !passwordField.text.isEmpty() && !confirmPasswordField.text.isEmpty()) {
            if(passwordField.text.toString() == confirmPasswordField.text.toString()) {
                firebaseAuth.createUserWithEmailAndPassword(emailField.text.toString(), passwordField.text.toString())
                    .addOnSuccessListener {
                        val userData = HashMap<String, String>()
                        userData.put("uid", it.user!!.uid.toString())
                        userData.put("name", nameField.text.toString())
                        userData.put("email", emailField.text.toString())
                        userData.put("class", "noclass")
                        Toast.makeText(applicationContext, "Hello, ${nameField.text.toString()}!", Toast.LENGTH_SHORT).show()

                        firestore.collection("users")
                            .document(it.user!!.uid)
                            .set(userData)

                        val intent = Intent(this, EnterClass::class.java)
                        intent.putExtra("uid", it.user!!.uid.toString())
                        startActivity(intent)
                        finish()
                    }

                    .addOnFailureListener {
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "All fields are mandatory!", Toast.LENGTH_SHORT).show()
        }
    }
}
