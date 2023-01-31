package com.example.paylasimmvvm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.navigation.Navigation
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowKokteylBinding
import com.example.paylasimmvvm.model.Drink
import com.example.paylasimmvvm.view.icecekler.KokteyllerFragment2Directions
import com.squareup.picasso.Picasso

class KokteyllerGridAdapter(private val tumKokteyller: ArrayList<Drink> ): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.recycler_row_kokteyl, parent, false)
        val dataItem = tumKokteyller[position]

        val binding= RecyclerRowKokteylBinding.bind(view)


        binding.isim.text=dataItem.kokteylIsim
        Picasso.get().load(dataItem.kokteylGorsel).into(binding.imageKokteyl)

        binding.tumLayout.setOnClickListener {
            val action= KokteyllerFragment2Directions.actionKokteyllerFragment2ToKokteylDetayiFragment(dataItem.kokteylID!!)

            Navigation.findNavController(it).navigate(action)

        }



        return view
    }

    override fun getItem(position: Int): Any {
        return tumKokteyller[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return tumKokteyller.size
    }

    fun kokteylListesiniGuncelle(yeniKokteylListesi: List<Drink>){
        tumKokteyller.clear()
        tumKokteyller.addAll(yeniKokteylListesi)
        notifyDataSetChanged()
    }



}