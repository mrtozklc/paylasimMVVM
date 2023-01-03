package com.example.paylasimmvvm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowBiraListBinding
import com.example.paylasimmvvm.model.BiralarModel
import com.squareup.picasso.Picasso

class BiralarRecyclerAdapter(private val tumBiralar:ArrayList<BiralarModel>):RecyclerView.Adapter<BiralarRecyclerAdapter.mViewHolder>() {

    class mViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

        val binding= RecyclerRowBiraListBinding.bind(itemView)

        fun setData(gelenBiralar: BiralarModel) {
            binding.biraName.text=gelenBiralar.biraIsim
            binding.biraTag.text=gelenBiralar.biraTag

            binding.biraAlkolOrani.setText("Alcohol:${gelenBiralar.biraAlkolOrani}")
            binding.biraPH.setText("PH:${gelenBiralar.biraPHOrani}")

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_bira_list, parent, false)

        return BiralarRecyclerAdapter.mViewHolder(viewHolder)

    }

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {

        holder.setData(tumBiralar[position])

    }

    override fun getItemCount(): Int {
     return tumBiralar.size
    }

    fun kampanyalariGuncelle(yeniBiraistesi: List<BiralarModel>){
        tumBiralar.clear()
        tumBiralar.addAll(yeniBiraistesi)
        notifyDataSetChanged()
    }
}