package com.example.college.ui.home

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.college.R
import com.example.college.adapters.SkippedClassesAdapter
import com.example.college.database.skipped_classes_subwise.SkippedClassModel
import com.example.college.database.skipped_classes_subwise.SkippedClassViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class HomeFragment : Fragment() {

    private lateinit var mSkippedClassViewModel: SkippedClassViewModel
    private lateinit var adapter: SkippedClassesAdapter
    private lateinit var subjectwise : RecyclerView
    private lateinit var add_subject_btn: FloatingActionButton
    private lateinit var addSubjectButton : FloatingActionButton
    private lateinit var addSubjectName: EditText
    private lateinit var date : TextView
    private lateinit var month : TextView
    private lateinit var day : TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mSkippedClassViewModel = ViewModelProvider(this).get(SkippedClassViewModel::class.java)

        mSkippedClassViewModel.readAllData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.setData(it)
        })
        add_subject_btn = root.findViewById(R.id.add_subject_btn)
        subjectwise = root.findViewById(R.id.subjectwise)

        //setting the date day month

        date = root.findViewById(R.id.date)
        month = root.findViewById(R.id.month)
        day = root.findViewById(R.id.day)

        val currentDate = LocalDateTime.now()
        val currentMonth= SimpleDateFormat("MMM")
        val currentDay = SimpleDateFormat("EEE")
        date.text = currentDate.dayOfMonth.toString()
        month.text = currentMonth.format(Date()).toString()
        day.text = currentDay.format(Date()).toString()


        //setting up the recycler view

        adapter = SkippedClassesAdapter()
        subjectwise.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        subjectwise.adapter = adapter

        add_subject_btn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val builderView = inflater.inflate(R.layout.subject_dialog, null)

            addSubjectButton = builderView.findViewById(R.id.add_subject_button)
            addSubjectName = builderView.findViewById(R.id.add_subject_editText)

            builder.setView(builderView)
            val alertDialog = builder.create()
            alertDialog.setCancelable(true)
            alertDialog.show()

            addSubjectButton.setOnClickListener {
                if(!addSubjectName.text.toString().isEmpty()) {
                    mSkippedClassViewModel.addSubject(SkippedClassModel(0, addSubjectName.text.toString(), 0))
                    alertDialog.dismiss()
                } else {
                    addSubjectName.requestFocus()
                }
            }

        }

        return root
    }

}

