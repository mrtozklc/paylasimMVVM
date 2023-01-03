package com.example.paylasimmvvm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowKokteylBinding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.view.icecekler.KokteyllerFragment2Directions
import com.example.paylasimmvvm.view.icecekler.KokteyllerFragmentDirections
import com.squareup.picasso.Picasso

class KokteyllerRecyclerAdapter2 (private val tumKokteyller: ArrayList<Drink>): RecyclerView.Adapter<KokteyllerRecyclerAdapter2.MViewHolder>() {

    class MViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){

        fun setData(gelenKokteyl: Drink) {
            val binding= RecyclerRowKokteylBinding.bind(itemView)
            Log.e("gelenkokteyll",""+gelenKokteyl)

            binding.isim.text=gelenKokteyl.kokteylIsim

            binding.kategoryId.text=gelenKokteyl.kokteylKategori

            binding.tumLayout.setOnClickListener {
                val action=KokteyllerFragment2Directions.actionKokteyllerFragment2ToKokteylDetayiFragment(gelenKokteyl.kokteylID!!)
                Navigation.findNavController(it).navigate(action)

            }

            Picasso.get().load(gelenKokteyl.kokteylGorsel).into(binding.imageKokteyl)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_kokteyl, parent, false)

        return KokteyllerRecyclerAdapter2.MViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.setData(tumKokteyller[position])



    }

    override fun getItemCount(): Int {
        return tumKokteyller.size
    }

    fun kokteylListesiniGuncelle(yeniKokteylListesi: List<Drink>){
        tumKokteyller.clear()
        tumKokteyller.addAll(yeniKokteylListesi)
        notifyDataSetChanged()
    }



}
