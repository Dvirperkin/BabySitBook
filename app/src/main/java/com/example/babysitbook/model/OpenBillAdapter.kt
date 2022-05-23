package com.example.babysitbook.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.OpenBillingRequestBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class OpenBillAdapter (
    private val options: FirestoreRecyclerOptions<OpenBill>
): FirestoreRecyclerAdapter<OpenBill, RecyclerView.ViewHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.open_billing_request, parent, false)
        val binding = OpenBillingRequestBinding.bind(view)
        return OpenBillViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: OpenBill
    ) {
        (holder as OpenBillViewHolder).bind(model)
    }

    inner class OpenBillViewHolder(private val binding: OpenBillingRequestBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : OpenBill){
            binding.date.text = item.date
            binding.time.text = item.time
            binding.totalSum.text = item.totalSum
            binding.user.text = item.emailToCharge
        }
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        println(e)
    }
}