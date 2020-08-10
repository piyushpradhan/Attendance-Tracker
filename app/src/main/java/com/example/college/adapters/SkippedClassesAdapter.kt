package com.example.college.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.college.R
import com.example.college.database.skipped_classes_subwise.SkippedClassModel
import com.example.college.ui.home.HomeFragmentDirections
import kotlinx.android.synthetic.main.skipped_classes_rv_single_item.view.*

class SkippedClassesAdapter() : RecyclerView.Adapter<SkippedClassesAdapter.ViewHolder>(){

    private var subjectList = emptyList<SkippedClassModel>()

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

        holder.progressBar.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.progress_bar_animation)
        holder.subjectName.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.sub_labels_anim)
        holder.progress.animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.sub_labels_anim)

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


