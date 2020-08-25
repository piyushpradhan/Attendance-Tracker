package com.example.college

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mikhaellopez.circularimageview.CircularImageView
import java.io.ByteArrayOutputStream
import java.io.IOException

class HomePage : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var uid: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var classCode : String

    private lateinit var navigationView : NavigationView
    private lateinit var navHeader : View
    private lateinit var displayName : TextView
    private lateinit var email : TextView
    private lateinit var profileImage : CircularImageView

    private lateinit var mStorageRef : StorageReference

    private val CHANNEL_ID = "Firestore updates"
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel

    private val FILE_NAME = "com.example.college"
    private val CLASS_CODE = "class"
    private val READ_EXTERNAL_STORAGE_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        uid = intent.getStringExtra("uid")
        classCode = intent.getStringExtra("class")

        mStorageRef = FirebaseStorage.getInstance().reference


        sharedPreferences = this.getSharedPreferences(FILE_NAME, 0)

        //setting the profile data
        //on nav bar header
        navigationView = findViewById(R.id.nav_view)
        navHeader = navigationView.inflateHeaderView(R.layout.nav_header_home_page)
        displayName = navHeader.findViewById(R.id.nav_bar_displayName)
        email = navHeader.findViewById(R.id.nav_bar_email)
        profileImage = navHeader.findViewById(R.id.nav_bar_profile)

        setUserInfoToNavHeader()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_notice, R.id.nav_shared
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //notifications
        FirebaseFirestore.getInstance()
            .collection("classes")
            .document(classCode)
            .collection("notices")
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null) {
                    snapshot.documentChanges.forEach {
                        sendNotifications(it.document["title"].toString())
                    }
                }
            }

    }

    private fun sendNotifications(title : String) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationChannel = NotificationChannel(CHANNEL_ID, "Firestore updated", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableVibration(true)
        notificationChannel.enableLights(false)
        notificationManager.createNotificationChannel(notificationChannel)

        val notificationIntent = Intent(this, HomePage::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notificationBuilder = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.profile)
            .setContentTitle(title)
            .setContentText("Yep it's working just fine.")
            .setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, notificationBuilder.build())
        }
    }

    private fun askForPermissionToReadExternalStorage() : Boolean {
        val result = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionToReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_CODE
            )
        } catch (e : Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun setUserInfoToNavHeader() {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .addSnapshotListener { value, error ->
                displayName.text = value?.data!!["name"].toString()
                email.text = value?.data!!["email"].toString()
                if(value.data!!["profileImage"].toString().isEmpty() || value.data!!["profileImage"] == null) {
                    Glide.with(this)
                        .load(R.drawable.profile_image_default)
                        .into(profileImage)
                } else {
                    Glide.with(this)
                        .load(value.data!!.get("profileImage").toString())
                        .into(profileImage)
                }
            }

        email.text = FirebaseAuth.getInstance().currentUser!!.email.toString()

        profileImage.setOnClickListener {
            if(askForPermissionToReadExternalStorage()) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                startActivityForResult(intent, 10)
            } else {
                requestPermissionToReadExternalStorage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 10 && data != null) {
            profileImage.setImageURI(data.data)

            val selectedImageUri = data.data
            var bmp : Bitmap? = null

            try {
                bmp = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            val baos : ByteArrayOutputStream = ByteArrayOutputStream()
            bmp?.compress(Bitmap.CompressFormat.JPEG, 25, baos)

            val sizeInBytes : ByteArray = baos.toByteArray()

            val profileStorageRef : StorageReference = mStorageRef.child(uid)

            profileStorageRef.putBytes(sizeInBytes).addOnSuccessListener {
                profileStorageRef.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .update("profileImage", downloadUrl.toString())
                }
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home_page, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_leave_class -> {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("class", "")
                    .addOnSuccessListener {
                        Toast.makeText(this, "Class Left", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, EnterClass::class.java)
                        intent.putExtra("uid", uid)
                        val editor = sharedPreferences.edit()
                        editor.remove(CLASS_CODE)
                        editor.apply()
                        editor.commit()
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
