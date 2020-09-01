package com.example.college.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.college.R
import com.example.college.models.CommentModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mikhaellopez.circularimageview.CircularImageView

class CommentAdapter(private val options : ArrayList<CommentModel>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val dp : CircularImageView = itemView.findViewById(R.id.comment_dp)
        val author : TextView = itemView.findViewById(R.id.comment_author)
        val commentText : TextView = itemView.findViewById(R.id.comment_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.single_comment_item,
            parent,
            false
        )

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return options.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.author.text = options[position].author
        holder.commentText.text = options[position].comment
        Glide.with(holder.itemView.context)
            .load(options[position].dpUrl)
            .into(holder.dp)
    }

}