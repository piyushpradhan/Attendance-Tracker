package com.example.college.ui.home

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
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
import com.github.guilhe.views.CircularProgressView
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
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
    private lateinit var attendanceProgressBar : CircularProgressView
    private lateinit var attendanceVal : TextView

    private lateinit var sharedPreferences: SharedPreferences

    private var attendance: Int = 0

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

        attendanceProgressBar = root.findViewById(R.id.total_attendance_circular)
        attendanceVal = root.findViewById(R.id.total_attendance)
        sharedPreferences = this.requireActivity().getSharedPreferences("attendance", 0)
        attendanceVal.text = attendance.toString()
        attendanceProgressBar.progress = attendance.toFloat()
        val editor = sharedPreferences.edit()

        //setting the attendance

        attendanceProgressBar.progress = attendance.toFloat()
        attendanceVal.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.attendance_progress)
        attendanceProgressBar.animation = AnimationUtils.loadAnimation(requireContext(), R.anim.attendance_progress)

        val uid = this.requireActivity().intent.getStringExtra("uid")

        val animator: ValueAnimator =
            ValueAnimator.ofInt(0, 600)
        animator.setDuration(5000)
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                attendanceVal.text = animation.animatedValue.toString()
            }
        })
        animator.start()

        attendanceVal.setOnClickListener {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update("attendance", attendance + 1)
                .addOnCompleteListener {
                    attendanceVal.text = attendance.toString()

                }
            attendance += 1;
            attendanceVal.text = attendance.toString()
            attendanceProgressBar.progress = attendance.toFloat()
            editor.putInt("attendance", attendance)
            editor.apply()
            editor.commit()
        }

        attendanceVal.setOnLongClickListener {

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .update("attendance", attendance - 1)
                .addOnCompleteListener {
                    attendanceVal.text = attendance.toString()
                }

            attendance -= 1
            attendanceVal.text = attendance.toString()
            attendanceProgressBar.progress = attendance.toFloat()
            editor.putInt("attendance", attendance)
            editor.apply()
            editor.commit()
            true
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnCompleteListener { task: Task<DocumentSnapshot> ->
                animator.cancel()
                attendance = Integer.parseInt(task.result!!.get("attendance").toString())
                attendanceVal.text = attendance.toString()
                attendanceProgressBar.progress = attendance.toFloat()
                editor.putInt("attendance", attendance)
                editor.apply()
                editor.commit()
            }

        //TODO:add a sharedPreference to store the attendance value to improve user experience
        //TODO: fix the issue with incrementing attendance

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

