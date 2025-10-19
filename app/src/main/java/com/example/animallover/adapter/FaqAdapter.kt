package com.example.animallover.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animallover.R
import com.example.animallover.model.Faq

class FaqAdapter(private val faqList: List<Faq>) : RecyclerView.Adapter<FaqAdapter.FaqViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            FaqViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
        return FaqViewHolder(view)
    }

    override fun onBindViewHolder(holder: FaqViewHolder, position: Int) {
        val faq = faqList[position]
        holder.question.text = faq.question
        holder.answer.text = faq.answer

        holder.itemView.setOnClickListener {
            val isVisible = holder.answer.visibility == View.VISIBLE
            holder.answer.visibility = if (isVisible) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount() = faqList.size

    class FaqViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val question: TextView = itemView.findViewById(R.id.faqQuestion)
        val answer: TextView = itemView.findViewById(R.id.faqAnswer)
    }
}