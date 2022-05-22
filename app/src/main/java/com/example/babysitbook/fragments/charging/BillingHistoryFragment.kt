package com.example.babysitbook.fragments.charging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.babysitbook.databinding.BillingHistoryBinding

class BillingHistoryFragment : Fragment(){
    lateinit var binding: BillingHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BillingHistoryBinding.inflate(inflater)
        return binding.root
    }
}