package com.example.college.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.college.HomePage
import com.example.college.R
import com.example.college.database.skipped_classes_subwise.SkippedClassModel
import com.example.college.database.skipped_classes_subwise.SkippedClassViewModel
import com.example.college.ui.home.HomeFragment
import com.example.college.ui.home.HomeFragmentDirections
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.skipped_classes_rv_single_item.view.*

class SkippedClassesAdapter() : RecyclerView.Adapter<SkippedClassesAdapter.ViewHolder>(){

    private var subjectList = listOf<SkippedClassModel>(SkippedClassModel(
        0,
        "Subject Name",
        20
    ))

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val subjectName = itemView.findViewById<TextView>(R.id.subject_name_single_item)
        val progressBar = itemView.findViewById<ProgressBar>(R.id.progress_bar_single_item)
        val progress = itemView.findViewById<TextView>(R.id.progress_single_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skipped_classes_rv_single_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subjectList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.subjectName.text = subjectList[position].subjectName
        holder.progressBar.progress = subjectList[position].classesSkipped
        holder.progress.text = subjectList[position].classesSkipped.toString()

        holder.itemView.subjectwise_single_item.setOnClickListener {
            val action = HomeFragmentDirections.actionNavHomeToAddSubject(subjectList[position])
            holder.itemView.findNavController().navigate(action)
        }
    }

    fun setData(data: List<SkippedClassModel>) {
        this.subjectList = data
        notifyDataSetChanged()
    }
}


