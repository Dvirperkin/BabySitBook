package com.example.babysitbook.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.babysitbook.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment(){
    lateinit var binding: FragmentNotificationsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentNotificationsBinding.inflate(inflater)
        return binding.root
    }
}