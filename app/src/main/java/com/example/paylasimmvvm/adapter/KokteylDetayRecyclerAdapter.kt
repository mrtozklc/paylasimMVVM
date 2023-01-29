package com.example.paylasimmvvm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowKokteylBinding
import com.example.paylasimmvvm.databinding.RecyclerRowKokteylDetaylariBinding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.model.DrinkDetay
import com.example.paylasimmvvm.model.KokteylDetay
import com.squareup.picasso.Picasso

class KokteylDetayRecyclerAdapter(var tumKokteyller:ArrayList<DrinkDetay>):RecyclerView.Adapter<KokteylDetayRecyclerAdapter.MviewHolder>() {
    class MviewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {


        fun setData(gelenKokteylDetay: DrinkDetay){
            val binding= RecyclerRowKokteylDetaylariBinding.bind(itemView)



            binding.isim.text=gelenKokteylDetay.kokteylIsim
            binding.glass.text=gelenKokteylDetay.kokteylbardak
            binding.malzemeler.text = gelenKokteylDetay.kokteylMalzemeler.joinToString("\n")
            binding.tarif.text=gelenKokteylDetay.kokteylTarif

            Picasso.get().load(gelenKokteylDetay.kokteylGorsel).into(binding.imageKokteyl)




        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MviewHolder {

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_kokteyl_detaylari, parent, false)

        return KokteylDetayRecyclerAdapter.MviewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: MviewHolder, position: Int) {
        holder.setData(tumKokteyller[position])
    }

    override fun getItemCount(): Int {

        return tumKokteyller.size
    }

    fun kokteylDetayListesiniGuncelle(yeniKokteylListesi: List<DrinkDetay>){
        tumKokteyller.clear()
        tumKokteyller.addAll(yeniKokteylListesi)
        notifyDataSetChanged()
    }
}