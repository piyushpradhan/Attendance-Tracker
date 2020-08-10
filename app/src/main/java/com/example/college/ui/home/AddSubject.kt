package com.example.college.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.college.R
import com.example.college.database.skipped_classes_subwise.SkippedClassModel
import com.example.college.database.skipped_classes_subwise.SkippedClassViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddSubject : Fragment() {

    private val args by navArgs<AddSubjectArgs>()

    private lateinit var mSkippedClassViewModel: SkippedClassViewModel
    private lateinit var addSubjectButton: FloatingActionButton
    private lateinit var addSubjectEditTextView: EditText

    private lateinit var incrementRecord: FloatingActionButton
    private lateinit var decrementRecord: FloatingActionButton
    private lateinit var deleteSubject : FloatingActionButton
    private lateinit var subjectName: TextView
    private lateinit var recordView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add_subject, container, false)

        addSubjectButton = view.findViewById(R.id.add_subject_btn)
        addSubjectEditTextView = view.findViewById(R.id.add_subject_et)
        var record = args.indSubArgs.classesSkipped

        incrementRecord = view.findViewById(R.id.increment_record)
        decrementRecord = view.findViewById(R.id.decrement_record)
        subjectName = view.findViewById(R.id.subject_name)
        recordView = view.findViewById(R.id.record_int)
        deleteSubject = view.findViewById(R.id.delete_subject)

        subjectName.text = args.indSubArgs.subjectName
        recordView.text = args.indSubArgs.classesSkipped.toString()

        mSkippedClassViewModel = ViewModelProvider(this).get(SkippedClassViewModel::class.java)

        addSubjectButton.setOnClickListener {
            if(!addSubjectEditTextView.text.isEmpty()) {
                mSkippedClassViewModel.addSubject(SkippedClassModel(
                    0,
                    addSubjectEditTextView.text.toString(),
                    0
                ))
                findNavController().navigate(R.id.action_addSubject_to_nav_home)
            }
        }

        incrementRecord.setOnClickListener {
            mSkippedClassViewModel.addSubject(SkippedClassModel(args.indSubArgs.id, args.indSubArgs.subjectName, ++record))
            recordView.text = record.toString()
        }

        decrementRecord.setOnClickListener {
            mSkippedClassViewModel.addSubject(SkippedClassModel(args.indSubArgs.id, args.indSubArgs.subjectName, --record))
            recordView.text = record.toString()
        }

        deleteSubject.setOnClickListener {
            mSkippedClassViewModel.deleteSubject(SkippedClassModel(args.indSubArgs.id, args.indSubArgs.subjectName, args.indSubArgs.classesSkipped))
            findNavController().navigate(R.id.action_addSubject_to_nav_home)
        }

        return view
    }
}
