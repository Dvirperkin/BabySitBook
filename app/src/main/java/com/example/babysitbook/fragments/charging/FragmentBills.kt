package com.example.babysitbook.fragments.charging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.babysitbook.databinding.FragmentBillsBinding

class FragmentBills : Fragment(){
    lateinit var binding: FragmentBillsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBillsBinding.inflate(inflater)
        return binding.root
    }
}