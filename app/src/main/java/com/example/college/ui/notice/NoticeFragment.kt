package com.example.college.ui.notice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.college.R
import com.example.college.adapters.NoticeAdapter
import com.example.college.models.NoticeModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class NoticeFragment : Fragment() {

    private lateinit var addNoticeButton : FloatingActionButton
    private lateinit var noticeList : RecyclerView

    private lateinit var adapter : NoticeAdapter
    private lateinit var classCode : String



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_notices, container, false)

        classCode = this.requireActivity().intent.getStringExtra("class")!!

        addNoticeButton = root.findViewById(R.id.add_new_notice)
        noticeList = root.findViewById(R.id.notice_list)

        val query = FirebaseFirestore.getInstance()
            .collection("classes")
            .document(classCode)
            .collection("notices")
            .orderBy("timestamp", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<NoticeModel>()
            .setQuery(query, NoticeModel::class.java)
            .build()

        adapter = NoticeAdapter(options)

        noticeList.setHasFixedSize(true)
        noticeList.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )
        noticeList.adapter = adapter

        addNoticeButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_notice_to_addNewNotice)
        }

        return root
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
}
