package com.example.paylasimmvvm.cocktail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowKokteylFiltersBinding


class KokteyllerRecyclerAdapter(private val tumKokteyller: ArrayList<String>):RecyclerView.Adapter<KokteyllerRecyclerAdapter.MViewHolder>() {

    class MViewHolder(itemview: View):RecyclerView.ViewHolder(itemview){

        fun setData(gelenKokteyl: String) {
            val binding= RecyclerRowKokteylFiltersBinding.bind(itemView)

                binding.kokteylFilters.text=gelenKokteyl


            binding.tumLayout.setOnClickListener {
                val action=
                    KokteyllerFiltreFragmentDirections.actionKokteyllerFragmentToKokteyllerFragment2(
                        gelenKokteyl
                    )
                Navigation.findNavController(it).navigate(action)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_kokteyl_filters, parent, false)

        return MViewHolder(viewHolder)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {

        holder.setData(tumKokteyller[position])

    }

    override fun getItemCount(): Int {
        return tumKokteyller.size
    }

    fun kokteylListesiniGuncelle(yeniKokteylListesi: List<String>){
        tumKokteyller.clear()
        tumKokteyller.addAll(yeniKokteylListesi)
        notifyDataSetChanged()
    }


}



