package com.example.babysitbook.model.searchableProfile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.babysitbook.R
import com.example.babysitbook.databinding.ClosedBillingRequestBinding
import com.example.babysitbook.model.ClosedBill
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException

class ClosedBillAdapter (
    private val options: FirestoreRecyclerOptions<ClosedBill>
): FirestoreRecyclerAdapter<ClosedBill, RecyclerView.ViewHolder>(options){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.closed_billing_request, parent, false)
        val binding = ClosedBillingRequestBinding.bind(view)
        return ClosedBillViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        model: ClosedBill
    ) {
        (holder as ClosedBillViewHolder).bind(model)
    }

    inner class ClosedBillViewHolder(private val binding: ClosedBillingRequestBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : ClosedBill){
            binding.date.text = item.date
            binding.time.text = item.time
            binding.totalSum.text = item.totalSum
            binding.user.text = item.emailToCharge

            if(item.paid){
                binding.statusIcon.setBackgroundResource(R.drawable.ic_baseline_check_24)
            }
            else {
                binding.statusIcon.setBackgroundResource(R.drawable.ic_baseline_x_24)
            }
        }
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        println(e)
    }
}