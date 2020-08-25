package com.example.college.ui.notice

import android.os.Bundle
import android.text.method.MovementMethod
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.example.college.R
import com.example.college.models.NoticeModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class NoticeDetail : Fragment() {

    private val args by navArgs<NoticeDetailArgs>()

    private lateinit var title : TextView
    private lateinit var content : EditText
    private lateinit var save : FloatingActionButton
    private lateinit var delete : FloatingActionButton
    private lateinit var date : TextView
    private lateinit var currentUser : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notice_detail, container, false)

        val timestamp = args.noticeArgs.timestamp
        (activity as AppCompatActivity).supportActionBar?.title = args.noticeArgs.displayName

        //setting the current user
        currentUser = FirebaseAuth.getInstance().currentUser!!.uid

        //getting reference to the XML elements
        title = view.findViewById(R.id.update_notice_title)
        content = view.findViewById(R.id.update_notice_content)
        content.movementMethod = ScrollingMovementMethod()
        save = view.findViewById(R.id.update_notice_save)
        date = view.findViewById(R.id.update_notice_date)
        delete = view.findViewById(R.id.update_notice_delete)

        title.text = args.noticeArgs.title
        content.setText(args.noticeArgs.content)
        date.text = getTimefromTimestamp(args.noticeArgs.timestamp!!)

        save.setOnClickListener {
            if(content.text.toString().isNotEmpty() &&
                currentUser.toString() == args.noticeArgs.author) {

                //updating notice
                val updateMap = mutableMapOf<String, Any>()

                updateMap["title"] = title.text.toString()
                updateMap["content"] = content.text.toString()

                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(requireActivity().intent.getStringExtra("class")!!)
                    .collection("notices")
                    .document(title.text.toString())
                    .update(updateMap)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Notice updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                findNavController().navigate(R.id.action_noticeDetail_to_nav_notice)

           } else if(currentUser.toString() != args.noticeArgs.author) {

                //permission denied
                Toast.makeText(requireContext(),
                    "You don't have the permission to edit this Notice ${args.noticeArgs.displayName}",
                    Toast.LENGTH_SHORT)
                    .show()
                findNavController().navigate(R.id.action_noticeDetail_to_nav_notice)

            } else {
                //blank notice warning
                Toast.makeText(requireContext(), "please fill out all the fields", Toast.LENGTH_SHORT).show()
            }
        }

        delete.setOnClickListener {
            if(currentUser.toString() == args.noticeArgs.author) {
                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(requireActivity().intent.getStringExtra("class")!!)
                    .collection("notices")
                    .document(args.noticeArgs.title.toString())
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Notice Deleted", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_noticeDetail_to_nav_notice)
                    }
            } else {
                Toast.makeText(requireContext(),
                    "You don't have the permission to delete this Notice ${args.noticeArgs.displayName}",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    private fun getTimefromTimestamp(timestamp : Long) : String {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val datetime = Date(timestamp)
        return format.format(datetime)
    }
}
