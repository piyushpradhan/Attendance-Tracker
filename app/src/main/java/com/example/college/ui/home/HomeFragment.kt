package com.example.college.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.college.R
import com.example.college.adapters.SkippedClassesAdapter
import com.example.college.database.skipped_classes_subwise.SkippedClassModel
import com.example.college.database.skipped_classes_subwise.SkippedClassViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var mSkippedClassViewModel: SkippedClassViewModel
    private lateinit var adapter: SkippedClassesAdapter
    private lateinit var subjectwise : RecyclerView
    private lateinit var add_subject_btn: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mSkippedClassViewModel = ViewModelProvider(this).get(SkippedClassViewModel::class.java)

        mSkippedClassViewModel.readAllData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            adapter.setData(it)
        })
        subjectwise = root.findViewById(R.id.subjectwise)
        adapter = SkippedClassesAdapter()
        subjectwise.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        subjectwise.adapter = adapter

        return root
    }

}