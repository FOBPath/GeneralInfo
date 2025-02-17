package com.example.fpgroup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val actionButton: Button = view.findViewById(R.id.actionButton)
        actionButton.setOnClickListener {
            Toast.makeText(requireContext(), "Action performed!", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
