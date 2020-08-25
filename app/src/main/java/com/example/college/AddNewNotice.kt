package com.example.college

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.college.models.NoticeModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddNewNotice : Fragment() {

    private lateinit var saveNoticeButton: Button
    private lateinit var noticeTitle : EditText
    private lateinit var noticeContent : EditText

    private lateinit var mFirestore : FirebaseFirestore
    private lateinit var settings : FirebaseFirestoreSettings

    private lateinit var uid : String
    private lateinit var classCode : String
    private var profileUrl : String? = null
    private var displayName : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_new_notice, container, false)

        uid = this.requireActivity().intent.getStringExtra("uid")!!
        classCode = this.requireActivity().intent.getStringExtra("class")!!
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .addSnapshotListener { value, error ->
                if(value != null) {
                    profileUrl = value.data?.get("profileImage").toString()
                    displayName = value.data?.get("name").toString()
                }
            }

        mFirestore = FirebaseFirestore.getInstance()

        //getting reference to the XML elements
        saveNoticeButton = view.findViewById(R.id.new_notice_save)
        noticeTitle = view.findViewById(R.id.new_notice_title)
        noticeTitle.movementMethod = ScrollingMovementMethod()
        noticeContent = view.findViewById(R.id.new_notice_content)
        noticeContent.movementMethod = ScrollingMovementMethod()


        saveNoticeButton.setOnClickListener {

            if(!noticeTitle.text.toString().isEmpty() && !noticeContent.text.toString().isEmpty()) {
                val notice = NoticeModel(noticeTitle.text.toString(), noticeContent.text.toString(), uid, profileUrl!!, System.currentTimeMillis(), displayName!!)
                addNewNotice(notice, noticeTitle.text.toString())
                findNavController().navigate(R.id.action_addNewNotice_to_nav_notice)
            }
        }

        return view
    }

    private fun addNewNotice(notice : NoticeModel, title : String) {
        CoroutineScope(IO).launch {
            mFirestore.collection("classes")
                .document(classCode.toString())
                .collection("notices")
                .document(title)
                .set(notice)
        }
    }
}