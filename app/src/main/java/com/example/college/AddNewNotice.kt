package com.example.college

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddNewNotice : Fragment() {

    private lateinit var saveNoticeButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_new_notice, container, false)

        saveNoticeButton = view.findViewById(R.id.new_notice_save)


        saveNoticeButton.setOnClickListener {
            findNavController().navigate(R.id.action_addNewNotice_to_nav_notice)
        }

        return view
    }
}
