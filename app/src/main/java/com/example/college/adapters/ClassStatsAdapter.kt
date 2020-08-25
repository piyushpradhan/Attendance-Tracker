package com.example.college.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.college.R
import com.example.college.models.ClassStatsModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*

class ClassStatsAdapter(options : FirestoreRecyclerOptions<ClassStatsModel>, classCode : String)
    : FirestoreRecyclerAdapter<ClassStatsModel, ClassStatsAdapter.ViewHolder>(options) {

    val code = classCode

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val subject = itemView.findViewById<TextView>(R.id.subject_name)
        val decrease = itemView.findViewById<FloatingActionButton>(R.id.reduce)
        val increase = itemView.findViewById<FloatingActionButton>(R.id.increase)
        val number = itemView.findViewById<TextView>(R.id.numerical_display)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.single_subject_class_stats,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ClassStatsModel) {
        holder.subject.text = model.subject
        if(model.weekList != null) {
            holder.number.text = model.weekList!![Calendar.WEEK_OF_YEAR].toString()
        } else {
            holder.number.text = "0"
        }
        holder.increase.setOnClickListener {

            model.weekList!![Calendar.WEEK_OF_YEAR] = model.weekList!![Calendar.WEEK_OF_YEAR] + 1
            holder.number.text = model.weekList!![Calendar.WEEK_OF_YEAR].toString()
            FirebaseFirestore.getInstance()
                .collection("classes")
                .document(code)
                .collection("classStats")
                .document(model.subject.toString())
                .update("weekList", model.weekList)

            notifyDataSetChanged()

        }

        holder.decrease.setOnClickListener {

            model.weekList!![Calendar.WEEK_OF_YEAR] = model.weekList!![Calendar.WEEK_OF_YEAR] - 1
            holder.number.text = model.weekList!![Calendar.WEEK_OF_YEAR].toString()
            FirebaseFirestore.getInstance()
                .collection("classes")
                .document(code)
                .collection("classStats")
                .document(model.subject.toString())
                .update("weekList", model.weekList)

            notifyDataSetChanged()

        }
    }

}