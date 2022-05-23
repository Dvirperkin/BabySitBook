package com.example.babysitbook.fragments.charging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.babysitbook.databinding.BillingHistoryBinding
import com.example.babysitbook.model.ClosedBill
import com.example.babysitbook.model.searchableProfile.ClosedBillAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class BillingHistoryFragment : Fragment(){
    lateinit var binding: BillingHistoryBinding
    private lateinit var adapter: ClosedBillAdapter
    private lateinit var query: Query
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var manager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BillingHistoryBinding.inflate(inflater)
        firestore = Firebase.firestore
        auth = Firebase.auth

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
    }

    override fun onPause() {
        super.onPause()
        binding.billingHistoryRecyclerView.recycledViewPool.clear()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    private fun setRecyclerView() {
        query = if (arguments?.get("isBabysitter") as Boolean) {
            firestore.collection("ClosedBills")
                .whereEqualTo("uid", auth.currentUser!!.uid)
                .orderBy("date")
        } else {
            firestore.collection("ClosedBills")
                .whereEqualTo("emailToCharge", auth.currentUser!!.email)
                .orderBy("date")
        }

        val options = FirestoreRecyclerOptions.Builder<ClosedBill>()
            .setQuery(query, ClosedBill::class.java)
            .build()
        adapter = ClosedBillAdapter(options)
        manager = LinearLayoutManager(context)

        binding.billingHistoryRecyclerView.layoutManager = manager
        binding.billingHistoryRecyclerView.adapter = adapter
    }
}