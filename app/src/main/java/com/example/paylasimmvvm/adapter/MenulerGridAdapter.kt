package com.example.paylasimmvvm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowMenuBinding
import com.example.paylasimmvvm.model.Menuler
import com.squareup.picasso.Picasso

class MenulerGridAdapter( val tumMenuler: ArrayList<Menuler>) : BaseAdapter() {






    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.recycler_row_menu, parent, false)
        val dataItem = tumMenuler[position]

        val binding= RecyclerRowMenuBinding.bind(view)

       Picasso.get().load(dataItem.menuler).into(binding.imgMenu)
        return view
    }

    override fun getItem(position: Int): Any {
        return tumMenuler[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return tumMenuler.size
    }

    fun menuleriGuncelle(yeniMenuListesi:List<Menuler>){
        tumMenuler.clear()
        tumMenuler.addAll(yeniMenuListesi)
        notifyDataSetChanged()



    }
}