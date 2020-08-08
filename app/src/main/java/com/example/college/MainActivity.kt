package com.example.college

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var goToRegister: TextView
    private lateinit var signInButton: Button
    private lateinit var emailField: EditText
    private lateinit var passwordField: EditText

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private val CLASS_CODE = "class"
    private val FILE_NAME = "com.example.college"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences(FILE_NAME, 0)
        var className = sharedPreferences.getString(CLASS_CODE,"noclass")

        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        updateUI(firebaseAuth.currentUser, className)

        goToRegister = findViewById(R.id.go_to_register)
        signInButton = findViewById(R.id.login_button)
        emailField = findViewById(R.id.login_email)
        passwordField = findViewById(R.id.login_password)

        goToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            signIn()
        }
    }

    private fun updateUI(currentUser: FirebaseUser?, className: String?) {
        if(currentUser != null && className == "noclass") {
            val intent = Intent(this, EnterClass::class.java)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        } else if(currentUser != null && className != "noclass") {
            val intent = Intent(this, HomePage::class.java)
            intent.putExtra("uid", currentUser.uid)
            intent.putExtra("class", className)
            startActivity(intent)
        }
    }


    private fun signIn() {
        if(!emailField.text.isEmpty() && !passwordField.text.isEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(emailField.text.toString(), passwordField.text.toString())
                .addOnSuccessListener {
                    firestore.collection("users")
                        .document(it.user?.uid.toString())
                        .addSnapshotListener{value, e ->
                            if(value != null) {
                                val className = value.data?.get("class").toString()
                                val editor = sharedPreferences.edit()
                                editor.putString("class", className)
                                editor.apply()
                                editor.commit()
                                updateUI(it.user, className)
                            }
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Please fill out all the fields", Toast.LENGTH_SHORT).show()
        }
    }

}
