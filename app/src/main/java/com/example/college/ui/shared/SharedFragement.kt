package com.example.college.ui.shared

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.college.R
import com.example.college.adapters.PostsAdapter
import com.example.college.models.PostModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class SharedFragement : Fragment() {

    private lateinit var temp : FloatingActionButton

    private lateinit var postsRecyclerView : RecyclerView
    private lateinit var adapter : PostsAdapter
    private lateinit var newPost : Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        newPost = root.findViewById(R.id.add_new_post)

        //setting up the posts recycler view
        postsRecyclerView = root.findViewById(R.id.posts_rv)

        setupRecyclerView()

        newPost.setOnClickListener {
            root.findNavController().navigate(R.id.action_nav_shared_to_newPostFragment)
        }

        return root
    }

    private fun setupRecyclerView() {

        val query = FirebaseFirestore.getInstance()
            .collection("classes")
            .document(requireActivity().intent.getStringExtra("class")!!)
            .collection("posts")

        val options = FirestoreRecyclerOptions.Builder<PostModel>()
            .setQuery(query, PostModel::class.java)
            .build()

        adapter = PostsAdapter(options)

        postsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            RecyclerView.VERTICAL,
            false
        )

        postsRecyclerView.adapter = adapter
        postsRecyclerView.setHasFixedSize(true)

    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
}
