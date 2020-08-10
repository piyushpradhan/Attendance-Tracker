package com.example.college

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore

class EnterClass : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val FILE_NAME = "com.example.college"
    private var userId : String? = null

    private lateinit var classCodeEdittext: EditText
    private lateinit var joinButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_enter_class)

        userId = intent.getStringExtra("uid")
        sharedPreferences = this.getSharedPreferences(FILE_NAME, 0)

        joinButton = findViewById(R.id.join_class_btn)
        classCodeEdittext = findViewById(R.id.enter_class_code)

        joinButton.setOnClickListener {
            joinClass()
        }
    }

    private fun joinClass() {
        val classCode = classCodeEdittext.text.toString()
        val editor = sharedPreferences.edit()
        editor.putString("class", classCode)
        editor.apply()
        editor.commit()
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId.toString())
            .update("class", classCode)
        val intent = Intent(this, HomePage::class.java)
        intent.putExtra("uid", userId)
        intent.putExtra("class", classCode)
        startActivity(intent)
        finish()
    }
}
