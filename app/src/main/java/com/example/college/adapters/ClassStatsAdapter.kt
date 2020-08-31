package com.example.college.adapters

import android.app.AlertDialog
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
import soup.neumorphism.NeumorphCardView
import java.util.*

class ClassStatsAdapter(options : FirestoreRecyclerOptions<ClassStatsModel>, classCode : String)
    : FirestoreRecyclerAdapter<ClassStatsModel, ClassStatsAdapter.ViewHolder>(options) {

    private val calendar = Calendar.getInstance()
    private val code = classCode

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val subject : TextView = itemView.findViewById(R.id.subject_name)
        val decrease : FloatingActionButton = itemView.findViewById(R.id.reduce)
        val increase : FloatingActionButton = itemView.findViewById(R.id.increase)
        val number : TextView = itemView.findViewById(R.id.numerical_display)
        val container : NeumorphCardView = itemView.findViewById(R.id.single_subject_card)
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
            holder.number.text = model.weekList!![calendar.get(Calendar.WEEK_OF_YEAR)].toString()
        } else {
            holder.number.text = "0"
        }

        holder.container.setOnLongClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context, R.style.AlertDialog_AppCompat_Dark)
            builder.setTitle("Delete this subject and all the data associated with it?")
            builder.setPositiveButton("YES") { _, _ ->
                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(code)
                    .collection("classStats")
                    .document(model.subject.toString())
                    .delete()

                notifyDataSetChanged()
            }

            builder.setNegativeButton("NO") { _, _ ->
            }

            val dialog = builder.create()
            dialog.show()
            true
        }

        holder.increase.setOnClickListener {

            model.weekList!![calendar.get(Calendar.WEEK_OF_YEAR)] = model.weekList!![calendar.get(Calendar.WEEK_OF_YEAR)] + 1
            holder.number.text = model.weekList!![calendar.get(Calendar.WEEK_OF_YEAR)].toString()
            FirebaseFirestore.getInstance()
                .collection("classes")
                .document(code)
                .collection("classStats")
                .document(model.subject.toString())
                .update("weekList", model.weekList)

            notifyDataSetChanged()

        }

        holder.decrease.setOnClickListener {

            model.weekList!![calendar.get(Calendar.WEEK_OF_YEAR)] = model.weekList!![calendar.get(Calendar.WEEK_OF_YEAR)] - 1
            if(model.weekList!![calendar.get(Calendar.WEEK_OF_YEAR)] >= 0) {
                holder.number.text = model.weekList!![calendar.get(Calendar.WEEK_OF_YEAR)].toString()
            } else {
                holder.number.text = "0"
            }
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