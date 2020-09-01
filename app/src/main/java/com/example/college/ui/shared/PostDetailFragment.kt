package com.example.college.ui.shared

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.college.R
import com.example.college.adapters.CommentAdapter
import com.example.college.models.CommentModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_post_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class PostDetailFragment : Fragment() {

    private val args by navArgs<PostDetailFragmentArgs>()

    private lateinit var image : ImageView
    private lateinit var likeBtn : FloatingActionButton
    private lateinit var commentBtn : FloatingActionButton
    private lateinit var postAuthor : TextView
    private lateinit var caption : TextView
    private lateinit var likes : TextView

    private var likesCount : Int? = 0
    private var liked : Boolean = false

    private lateinit var classCode : String
    private lateinit var postId : String

    private lateinit var dpUrl : String

    private lateinit var commentAdapter : CommentAdapter
    private var commentList: ArrayList<CommentModel> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_detail, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializing
        image = view.findViewById(R.id.post_image_detail)
        likeBtn = view.findViewById(R.id.like_button)
        commentBtn = view.findViewById(R.id.comment_button)
        postAuthor = view.findViewById(R.id.post_author_text)
        caption = view.findViewById(R.id.post_caption)
        likes = view.findViewById(R.id.likes_count)

        classCode = requireActivity().intent.getStringExtra("class")!!
        postId = args.postDetailArgs.author.toString() + args.postDetailArgs.timestamp.toString()

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
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
                    .document(classCode)
                    .collection("posts")
                    .document(postId)
                    .update("likes", likesCount)


            } else {
                likesCount = likesCount?.plus(1)
                likes.text = likesCount.toString() + " likes"
                liked = true
                likeBtn.setImageResource(R.drawable.ic_favorite_black_24dp)

                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(classCode)
                    .collection("posts")
                    .document(postId)
                    .update("likes", likesCount)
            }
        }
        commentBtn.setOnClickListener {
            addComment()
        }

        //setting up comments recycler View

        setupComments(postId)

        //deleting post

        if(FirebaseAuth.getInstance().currentUser?.uid.toString() == args.postDetailArgs.author.toString()) {
            delete_post.visibility = View.VISIBLE
            delete_post.isClickable = true
            delete_post.isEnabled = true
        } else {
            delete_post.visibility = View.GONE
            delete_post.isClickable = false
            delete_post.isEnabled = false
        }

        delete_post.setOnClickListener {
            if(FirebaseAuth.getInstance().currentUser?.uid.toString() == args.postDetailArgs.author.toString()) {
                val builder = AlertDialog.Builder(requireContext(), R.style.AlertDialog_AppCompat_Dark)
                builder.setTitle("Are you sure you want to delete this post?")
                builder.setPositiveButton("YES") { _, _ ->
                    FirebaseFirestore.getInstance()
                        .collection("classes")
                        .document(classCode)
                        .collection("posts")
                        .document(postId)
                        .delete()
                        .addOnSuccessListener {
                            findNavController().navigate(R.id.action_postDetailFragment_to_nav_shared)
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                }
                builder.setNeutralButton("NO") {_, _ -> }

                val dialog = builder.create()
                dialog.show()

            } else {
                Toast.makeText(requireContext(), "You don't have the permission to delete this post", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun addComment() {
        val builder = AlertDialog.Builder(requireContext())
        val builderView = LayoutInflater.from(requireContext()).inflate(R.layout.subject_dialog, null)

        val addCommentButton : FloatingActionButton = builderView.findViewById(R.id.add_subject_button)
        val addCommentEditText : EditText = builderView.findViewById(R.id.add_subject_editText)
        addCommentEditText.hint = "Enter your comment here..."

        builder.setView(builderView)

        val alertDialog : AlertDialog = builder.create()
        alertDialog.setCancelable(true)
        alertDialog.show()

        addCommentButton.setOnClickListener {
            if(addCommentEditText.text.toString().isNotEmpty()) {

                if(dpUrl.isEmpty()) {
                    dpUrl = "https://firebasestorage.googleapis.com/v0/b/college-795f8.appspot.com/o/profile_image_default.jpg?alt=media&token=a4a2e042-e076-4539-b155-ab2386a9b744"
                }

                val commentModel = CommentModel(
                    addCommentEditText.text.toString(),
                    args.postDetailArgs.name,
                    args.postDetailArgs.author,
                    dpUrl,
                    System.currentTimeMillis()
                )

                FirebaseFirestore.getInstance()
                    .collection("classes")
                    .document(classCode)
                    .collection("posts")
                    .document(postId)
                    .collection("comments")
                    .add(commentModel)

                alertDialog.dismiss()

            }
        }
    }

    private fun setupComments(post : String) {

        CoroutineScope(IO).launch {
            FirebaseFirestore.getInstance()
                .collection("classes")
                .document(classCode)
                .collection("posts")
                .document(post)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener { it, error ->
                    val list = it?.documents

                    if(list!!.isNotEmpty()) {
                        commentList = ArrayList()
                        for(d in list) {
                            val comment = d.toObject(CommentModel::class.java)
                            commentList.add(comment!!)
                        }
                        commentAdapter = CommentAdapter(commentList)

                        comments_rv.layoutManager = LinearLayoutManager(
                            requireContext(),
                            RecyclerView.VERTICAL,
                            false
                        )

                        comments_rv.adapter = commentAdapter
                        comments_rv.setHasFixedSize(true)
                    }
                }
        }

    }
}
