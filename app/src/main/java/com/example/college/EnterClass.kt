package com.example.college

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class EnterClass : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private val FILE_NAME = "com.example.college"
    private var userId : String? = null

    private lateinit var classCodeEdittext: EditText
    private lateinit var joinButton: Button
    private lateinit var addButton: Button

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
        addButton = findViewById(R.id.add_class_btn)
        classCodeEdittext = findViewById(R.id.enter_class_code)

        joinButton.setOnClickListener {
            joinClass()
        }

        addButton.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val builderView = LayoutInflater.from(this).inflate(R.layout.subject_dialog, null, false)

            val addSubjectButton = builderView.findViewById<FloatingActionButton>(R.id.add_subject_button)
            val addSubjectName = builderView.findViewById<EditText>(R.id.add_subject_editText)

            addSubjectName.hint = "New class code"

            builder.setView(builderView)
            val alertDialog = builder.create()
            alertDialog.setCancelable(true)
            alertDialog.show()

            addSubjectButton.setOnClickListener {
                val classCode = HashMap<String, String>()
                classCode.put("classCode", addSubjectName.text.toString())
                if(!addSubjectName.text.toString().isEmpty()) {
                    FirebaseFirestore.getInstance()
                        .collection("classes")
                        .document(addSubjectName.text.toString())
                        .set(classCode)
                        .addOnSuccessListener {
                            Toast.makeText(this,
                                "Class ${addSubjectName.text.toString()} created successfully",
                                Toast.LENGTH_SHORT).show()

                            alertDialog.dismiss()
                        }
                } else {
                    addSubjectName.requestFocus()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
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
