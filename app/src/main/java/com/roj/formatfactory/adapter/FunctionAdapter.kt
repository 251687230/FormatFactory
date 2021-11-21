package com.roj.formatfactory

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class FunctionAdapter(val context : Context, val data : List<String>) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_function,parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.functionTv.text = data[position]
        when {
            position %4 == 0 -> {
                (holder.itemView as CardView).setCardBackgroundColor(Color.RED)
            }
            position % 4 == 1 -> {
                (holder.itemView as CardView).setCardBackgroundColor(Color.GREEN)
            }
            position % 4 == 2 -> {
                (holder.itemView as CardView).setCardBackgroundColor(Color.BLUE)
            }
            else -> {
                (holder.itemView as CardView).setCardBackgroundColor(Color.YELLOW)
            }
        }

        holder.itemView.setOnClickListener {
            adapterListener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int {
       return data.size
    }

    var adapterListener : AdapterListener?  = null
}

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val functionTv = itemView.findViewById<TextView>(R.id.tv_function)
}




interface AdapterListener{
    fun onItemClick(position: Int)
}