package com.example.college.ui.shared

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController

import com.example.college.R
import com.example.college.models.PostModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.util.*

class NewPostFragment : Fragment() {

    private lateinit var image : ImageView
    private lateinit var caption : TextView
    private lateinit var post : Button

    private lateinit var mStorageRef : StorageReference
    private var selectedImageUri : Uri? = null

    private var displayName : String? = null

    //constants
    private val READ_EXTERNAL_STORAGE_CODE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_post, container, false)

        //initializing
        image = view.findViewById(R.id.new_image)
        caption = view.findViewById(R.id.caption)
        post = view.findViewById(R.id.post_button)

        mStorageRef = FirebaseStorage.getInstance().reference

        CoroutineScope(IO).launch {

            val uid = requireActivity().intent.getStringExtra("uid")!!

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .addSnapshotListener { value, error ->
                    if(value != null) {
                        displayName = value.data?.get("name").toString()
                    }
                }
        }

        setImage()

        post.setOnClickListener {
            Toast.makeText(requireContext(), "Posting...", Toast.LENGTH_SHORT).show()
            postImage(selectedImageUri)
        }

        return view
    }

    private fun askForPermissionToReadExternalStorage() : Boolean {
        val result = requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissionToReadExternalStorage() {
        try {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_CODE
            )
        } catch (e : Exception) {
            e.printStackTrace()
            throw e
        }
    }

    private fun setImage() {
        if(askForPermissionToReadExternalStorage()) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 10)
        } else {
            requestPermissionToReadExternalStorage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 10 && data != null) {
            image.setImageURI(data.data)
            selectedImageUri = data.data
        }
    }

    private fun postImage(imageUri : Uri?) {
        if(imageUri != null) {

            CoroutineScope(IO).launch {
                var bmp : Bitmap? = null

                try {
                    bmp = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                val baos : ByteArrayOutputStream = ByteArrayOutputStream()

                bmp?.compress(Bitmap.CompressFormat.JPEG, 25, baos)

                val sizeInBytes : ByteArray = baos.toByteArray()

                val profileStorageRef : StorageReference = mStorageRef.child(imageUri.path.toString())

                profileStorageRef.putBytes(sizeInBytes).addOnSuccessListener {

                    Toast.makeText(requireContext(), "Posting...", Toast.LENGTH_SHORT).show()

                    profileStorageRef.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()

                        val timestamp = System.currentTimeMillis()

                        val postModel = PostModel(
                            caption.text.toString(),
                            downloadUrl.toString(),
                            FirebaseAuth.getInstance().currentUser?.uid.toString(),
                            displayName,
                            timestamp,
                            LocalDateTime.now().toString(),
                            0
                        )

                        FirebaseFirestore.getInstance()
                            .collection("classes")
                            .document(requireActivity().intent.getStringExtra("class")!!)
                            .collection("posts")
                            .document(timestamp.toString())
                            .set(postModel)
                            .addOnSuccessListener {
                                view?.findNavController()?.navigate(R.id.action_newPostFragment_to_nav_shared)
                            }
                    }
                }
            }

        } else {
            Toast.makeText(requireContext(), "Please select an image to continue", Toast.LENGTH_SHORT).show()
        }
    }
}
