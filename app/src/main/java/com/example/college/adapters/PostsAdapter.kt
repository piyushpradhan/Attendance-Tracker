package com.example.college.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.NavigatorProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.college.R
import com.example.college.models.PostModel
import com.example.college.ui.shared.SharedFragement
import com.example.college.ui.shared.SharedFragementDirections
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.internal.NavigationMenu
import soup.neumorphism.NeumorphCardView

class PostsAdapter(options : FirestoreRecyclerOptions<PostModel>) :
    FirestoreRecyclerAdapter<PostModel, PostsAdapter.ViewHolder>(options) {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val label : TextView = itemView.findViewById(R.id.single_post_label)
        val image : ImageView = itemView.findViewById(R.id.single_post_image)
        val postCard : NeumorphCardView = itemView.findViewById(R.id.single_post_card)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_post_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: PostModel) {

        holder.label.text = model.name
        Glide.with(holder.itemView.context)
            .load(model.imageUrl)
            .into(holder.image)

        holder.postCard.setOnClickListener {
            val action = SharedFragementDirections.actionNavSharedToPostDetailFragment(model)
            holder.itemView.findNavController().navigate(action)
        }

    }
}