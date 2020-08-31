package com.example.college.ui.shared

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.example.college.R
import com.example.college.adapters.CommentAdapter
import com.example.college.models.CommentModel
import com.example.college.models.PostModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailFragment : Fragment() {

    private val args by navArgs<PostDetailFragmentArgs>()

    private lateinit var image : ImageView
    private lateinit var likeBtn : FloatingActionButton
    private lateinit var commentBtn : FloatingActionButton
    private lateinit var postAuthor : TextView
    private lateinit var caption : TextView
    private lateinit var likes : TextView
    private lateinit var commentRv : RecyclerView

    private var likesCount : Int? = 0
    private var liked : Boolean = false

    private lateinit var dpUrl : String

    private lateinit var commentAdapter : CommentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        //initializing
        image = view.findViewById(R.id.post_image_detail)
        likeBtn = view.findViewById(R.id.like_button)
        commentBtn = view.findViewById(R.id.comment_button)
        postAuthor = view.findViewById(R.id.post_author_text)
        caption = view.findViewById(R.id.post_caption)
        likes = view.findViewById(R.id.likes_count)


        commentRv = view.findViewById(R.id.comments_rv)
        setupComments(args.postDetailArgs.timestamp.toString())


        FirebaseFirestore.getInstance()
            .collection("users")
            .document(args.postDetailArgs.author.toString())
            .addSnapshotListener { value, _ ->
                dpUrl = value?.get("profileImage").toString()
            }


        Glide.with(requireContext())
            .load(args.postDetailArgs.imageUrl)
            .into(image)

        postAuthor.text = args.postDetailArgs.name
        caption.text = args.postDetailArgs.label

        likesCount = args.postDetailArgs.likes

        likes.text = likesCount.toString() + " likes"

        likeBtn.setOnClickListener {

            if(liked) {
                likesCount = likesCount?.minus(1)
                likes.text = likesCount.toString() + " likes"
                liked = false
                likeBtn.setImageResource(R.drawable.ic_favorite_border_black_24dp)

                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(requireActivity().intent.getStringExtra("class")!!)
                    .collection("posts")
                    .document(args.postDetailArgs.timestamp.toString())
                    .update("likes", likesCount)


            } else {
                likesCount = likesCount?.plus(1)
                likes.text = likesCount.toString() + " likes"
                liked = true
                likeBtn.setImageResource(R.drawable.ic_favorite_black_24dp)

                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(requireActivity().intent.getStringExtra("class")!!)
                    .collection("posts")
                    .document(args.postDetailArgs.timestamp.toString())
                    .update("likes", likesCount)
            }
        }

        commentBtn.setOnClickListener {
            addComment(inflater)
        }

        return view
    }

    private fun addComment(inflater : LayoutInflater) {
        val builder = AlertDialog.Builder(requireContext())
        val builderView = inflater.inflate(R.layout.subject_dialog, null)

        val addCommentButton : FloatingActionButton = builderView.findViewById(R.id.add_subject_button)
        val addCommentEditText : EditText = builderView.findViewById(R.id.add_subject_editText)
        addCommentEditText.hint = "Enter your comment here..."

        builder.setView(builderView)

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()

        addCommentButton.setOnClickListener {
            if(addCommentEditText.text.toString().isNotEmpty()) {
                val commentModel = CommentModel(
                    addCommentEditText.text.toString(),
                    args.postDetailArgs.name,
                    args.postDetailArgs.author,
                    dpUrl
                )

                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(requireActivity().intent.getStringExtra("class")!!)
                    .collection("posts")
                    .document(args.postDetailArgs.timestamp.toString())
                    .collection("comments")
                    .add(commentModel)

                alertDialog.dismiss()

            }
        }
    }

    private fun setupComments(post : String) {

        val query = FirebaseFirestore.getInstance()
            .collection("classes")
            .document(requireActivity().intent.getStringExtra("class")!!)
            .collection("posts")
            .document(post)
            .collection("comments")

        val options = FirestoreRecyclerOptions.Builder<CommentModel>()
            .setQuery(query, CommentModel::class.java)
            .build()

        commentAdapter = CommentAdapter(options)

        commentRv.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )

        commentRv.adapter = commentAdapter
        commentRv.setHasFixedSize(true)

    }

    override fun onStart() {
        super.onStart()
        commentAdapter.startListening()
    }
}
