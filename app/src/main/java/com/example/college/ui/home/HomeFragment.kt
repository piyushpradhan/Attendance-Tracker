package com.example.college.ui.home

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Color.argb
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.college.R
import com.example.college.adapters.ClassStatsAdapter
import com.example.college.adapters.SkippedClassesAdapter
import com.example.college.database.skipped_classes_subwise.SkippedClassViewModel
import com.example.college.models.ClassStatsModel
import com.example.college.models.SkippedClassModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.github.guilhe.views.CircularProgressView
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import soup.neumorphism.NeumorphFloatingActionButton
import soup.neumorphism.NeumorphTextView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private lateinit var mSkippedClassViewModel: SkippedClassViewModel
    private lateinit var adapter: SkippedClassesAdapter
    private lateinit var subjectwise : RecyclerView
    private lateinit var add_subject_btn: NeumorphFloatingActionButton
    private lateinit var addSubjectButton : FloatingActionButton
    private lateinit var addSubjectName: EditText
    private lateinit var date : NeumorphTextView
    private lateinit var month : TextView
    private lateinit var day : TextView
    private lateinit var attendanceProgressBar : CircularProgressView
    private lateinit var attendanceVal : TextView
    private lateinit var classStatsRv : RecyclerView
    private lateinit var classStatsAdapter: ClassStatsAdapter
    private lateinit var reduceAttendanceButton : FloatingActionButton
    private lateinit var addSubjectClass : NeumorphFloatingActionButton
    private lateinit var lineChart : LineChart

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
        addSubjectClass = root.findViewById(R.id.add_subject_btn_class)
        subjectwise = root.findViewById(R.id.subjectwise)

        attendanceProgressBar = root.findViewById(R.id.total_attendance_circular)
        reduceAttendanceButton = root.findViewById(R.id.reduce_attendace)
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

        reduceAttendanceButton.setOnClickListener {
            if(attendance > 0) {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("attendance", attendance - 1)
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }

                attendance -= 1
                attendanceVal.text = attendance.toString()
                attendanceProgressBar.progress = attendance.toFloat()
                editor.putInt("attendance", attendance)
                editor.apply()
                editor.commit()
            }
        }

        attendanceVal.setOnLongClickListener {

            val resetAttendanceBuilder = AlertDialog.Builder(requireContext(), R.style.AlertDialog_AppCompat_Dark)
            resetAttendanceBuilder.setTitle("Are you sure you want to reset yout attendance ?")
            resetAttendanceBuilder.setPositiveButton("YES") { _, _ ->
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("attendance", 0)
                    .addOnSuccessListener {
                        attendance = 0
                        attendanceVal.text = attendance.toString()
                        attendanceProgressBar.progress = attendance.toFloat()
                        editor.putInt("attendance", attendance)
                        editor.apply()
                        editor.commit()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
            }

            resetAttendanceBuilder.setNegativeButton("NO") { _ , _ ->
            }
            val resetAttendaceDialog = resetAttendanceBuilder.create()
            resetAttendaceDialog.show()

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
                if(addSubjectName.text.toString().isNotEmpty()) {
                    mSkippedClassViewModel.addSubject(
                        SkippedClassModel(
                            0,
                            addSubjectName.text.toString(),
                            0
                        )
                    )
                    alertDialog.dismiss()
                } else {
                    addSubjectName.requestFocus()
                }
            }

        }

        //setting the class stats

        addSubjectClass.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val builderView = inflater.inflate(R.layout.subject_dialog, null)

            addSubjectButton = builderView.findViewById(R.id.add_subject_button)
            addSubjectName = builderView.findViewById(R.id.add_subject_editText)

            builder.setView(builderView)
            val alertDialog = builder.create()
            alertDialog.setCancelable(true)
            alertDialog.show()

            addSubjectButton.setOnClickListener {
                if(addSubjectName.text.toString().isNotEmpty()) {
                    val weekAttendance = ArrayList<Int>()
                    for(i in 1 until 53) {
                        weekAttendance.add(0)
                    }
                    val classStatsModel = ClassStatsModel(
                        addSubjectName.text.toString(),
                        Calendar.WEEK_OF_YEAR,
                        weekAttendance
                    )

                    FirebaseFirestore.getInstance()
                        .collection("classes")
                        .document(requireActivity().intent.getStringExtra("class")!!)
                        .collection("classStats")
                        .document(addSubjectName.text.toString())
                        .set(classStatsModel)

                    alertDialog.dismiss()
                } else {
                    addSubjectName.requestFocus()
                }
            }
        }

        lineChart = root.findViewById(R.id.lineChart)
        classStatsRv = root.findViewById(R.id.class_stats_rv)

        val query_stats = FirebaseFirestore.getInstance()
            .collection("classes")
            .document(requireActivity().intent.getStringExtra("class")!!)
            .collection("classStats")

        val options = FirestoreRecyclerOptions.Builder<ClassStatsModel>()
            .setQuery(query_stats, ClassStatsModel::class.java)
            .build()

        setupClassStatsGraph()

        classStatsAdapter = ClassStatsAdapter(options, requireActivity().intent.getStringExtra("class")!!)

        classStatsRv.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )

        classStatsRv.adapter = classStatsAdapter

        return root
    }

    private fun setupClassStatsGraph() {

        FirebaseFirestore.getInstance()
            .collection("classes")
            .document(requireActivity().intent.getStringExtra("class")!!)
            .collection("classStats")
            .addSnapshotListener { value, error ->
                val entries = mutableListOf<LineDataSet>()
                value?.documents?.forEach {
                    val temp: ArrayList<Int>? = it.data?.get("weekList") as ArrayList<Int>?
                    val entriesList = mutableListOf<Entry>()
                    temp?.forEachIndexed { index, i ->
                        entriesList.add(Entry(index.toFloat(), i.toFloat()))
                    }
                    entries.add(LineDataSet(entriesList, it.data?.get("subject").toString()))
                }

                entries.forEach {
                    it.setDrawValues(false)
                    it.setDrawFilled(false)
                    it.lineWidth = 3f
                    it.fillColor = R.color.colorAccent
                    it.fillAlpha = R.color.colorPrimaryDark
                    it.circleRadius = 1.5f
                }

                if(lineChart.data != null) {
                    lineChart.clear()
                    lineChart.data = LineData(entries as List<ILineDataSet>?)
                } else if(lineChart.data == null) {
                    lineChart.data = LineData(entries as List<ILineDataSet>?)
                }

                lineChart.legend.textColor = Color.WHITE
                lineChart.xAxis.textColor = Color.WHITE
                lineChart.xAxis.labelRotationAngle = 0f

                lineChart.axisRight.isEnabled = true
                lineChart.xAxis.axisMaximum = 53f

                lineChart.axisRight.mAxisMaximum = 20f
                lineChart.axisLeft.mAxisMaximum = 20f

                lineChart.axisRight.mAxisMinimum = 0f
                lineChart.axisLeft.mAxisMinimum = 0f

                lineChart.setTouchEnabled(true)
                lineChart.setPinchZoom(true)
                lineChart.setNoDataText("No classes bunked yet")

                lineChart.animateX(1800, Easing.EaseInExpo)
            }

    }

    override fun onStart() {
        super.onStart()
        classStatsAdapter.startListening()
    }

}

