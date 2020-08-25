package com.example.college.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.college.R
import com.example.college.models.NoticeModel
import com.example.college.ui.notice.NoticeFragmentDirections
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mikhaellopez.circularimageview.CircularImageView

class NoticeAdapter(options: FirestoreRecyclerOptions<NoticeModel>) :
    FirestoreRecyclerAdapter<NoticeModel, NoticeAdapter.ViewHolder>(options) {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<TextView>(R.id.single_notice_title)
        val desc = itemView.findViewById<TextView>(R.id.single_notice_desc)
        val author = itemView.findViewById<TextView>(R.id.single_notice_author)
        val authorProfilePic = itemView.findViewById<CircularImageView>(R.id.single_notice_dp)

        val singleNoticeItem = itemView.findViewById<ConstraintLayout>(R.id.notice_single_item)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.notice_single_item, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: NoticeModel) {

        holder.title.text = model.title
        holder.desc.text = model.content
        holder.author.text = model.displayName

        if(model.profileUrl!!.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(model.profileUrl)
                .into(holder.authorProfilePic)
        } else {
            Glide.with(holder.itemView.context)
                .load(R.drawable.profile)
                .into(holder.authorProfilePic)
        }

        //navigating to the detail fragment
        holder.singleNoticeItem.setOnClickListener {
            val action = NoticeFragmentDirections.actionNavNoticeToNoticeDetail(model)
            holder.itemView.findNavController().navigate(action)
        }

    }
}