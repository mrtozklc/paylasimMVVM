package com.example.paylasimmvvm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowKokteylBinding
import com.example.paylasimmvvm.databinding.RecyclerRowKokteylFiltersBinding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.service.KokteylApiServis
import com.example.paylasimmvvm.view.icecekler.KokteyllerFragmentDirections
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable

class KokteyllerRecyclerAdapter(private val tumKokteyller: ArrayList<Drink>):RecyclerView.Adapter<KokteyllerRecyclerAdapter.MViewHolder>() {

    class MViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){


        fun setData(gelenKokteyl: Drink) {
            val binding= RecyclerRowKokteylFiltersBinding.bind(itemView)




            if (gelenKokteyl.kokteyGlass!=null){

                binding.kokteylFilters.text=gelenKokteyl.kokteyGlass
                binding.tumLayout.setOnClickListener {
                    val action=KokteyllerFragmentDirections.actionKokteyllerFragmentToKokteyllerFragment2(gelenKokteyl.kokteyGlass!!)
                    Navigation.findNavController(it).navigate(action)

                }

            }else if(gelenKokteyl.kokteylicerik!=null){
                binding.kokteylFilters.text=gelenKokteyl.kokteylicerik
                binding.tumLayout.setOnClickListener {
                    val action=KokteyllerFragmentDirections.actionKokteyllerFragmentToKokteyllerFragment2(gelenKokteyl.kokteylicerik!!)
                    Navigation.findNavController(it).navigate(action)

                }

            }else if (gelenKokteyl.kokteylKategori!=null){

                binding.kokteylFilters.text=gelenKokteyl.kokteylKategori
                binding.tumLayout.setOnClickListener {
                    val action=KokteyllerFragmentDirections.actionKokteyllerFragmentToKokteyllerFragment2(gelenKokteyl.kokteylKategori!!)
                    Navigation.findNavController(it).navigate(action)

                }



            }










        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_kokteyl_filters, parent, false)

        return KokteyllerRecyclerAdapter.MViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {
        holder.setData(tumKokteyller[position])

        val tiklananIteminPozisyonu = position

        val tiklananItem = tumKokteyller[tiklananIteminPozisyonu]






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



