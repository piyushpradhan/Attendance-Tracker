package com.example.college.ui.notice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.college.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class NoticeFragment : Fragment() {

    private lateinit var noticeFragmentViewModel: NoticeFragmentViewModel

    private lateinit var addNoticeButton : FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notices, container, false)

        addNoticeButton = root.findViewById(R.id.add_new_notice)


        addNoticeButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_notice_to_addNewNotice)
        }

        return root
    }
}
