package com.example.paylasimmvvm.cocktail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.navigation.Navigation
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowKokteylBinding
import com.squareup.picasso.Picasso

class KokteyllerFiltreGridAdapter(private val tumKokteyller: ArrayList<String>): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.recycler_row_kokteyl, parent, false)
        val dataItem = tumKokteyller[position]


        val binding= RecyclerRowKokteylBinding.bind(view)

        binding.imageKokteyl.adjustViewBounds = true
       // binding.imageKokteyl.scaleType = ImageView.ScaleType.CENTER_CROP
        binding.isim.text=dataItem

        val kategoriResimMap = hashMapOf(
            "Kokteyl" to R.mipmap.ic_launcher_kokteyl_gorsel_foreground,
            "Shot" to R.mipmap.ic_launcher_shot_gorsel_foreground,
            "Basit" to R.mipmap.ic_launcher_ordinaryy_gorsel_foreground,
            "Diğer" to R.mipmap.ic_launcher_diger_gorsel_foreground,
            "Etkinlik/Parti" to R.mipmap.ic_launcher_etkinlik_gorsel_foreground,
            "Kahve/Çay" to R.mipmap.ic_launcher_kahve_gorsel_foreground,
            "Bira" to R.mipmap.ic_launcher_bira_gorsel_foreground,
            "Shake" to R.mipmap.ic_launcher_shake_gorsel_foreground,
            "Yumuşak İçim" to R.mipmap.ic_launcher_soft_gorsel_foreground,
            "Ev Yapımı Likör" to R.mipmap.ic_launcher_likor_gorsel_foreground,
            "Kakao" to R.mipmap.ic_launcher_kakao_gorsel_foreground,

            )

        val resimId = kategoriResimMap[dataItem]
        if (resimId != null) {
            Picasso.get().load(resimId).centerCrop().fit().into(binding.imageKokteyl)
        }

        binding.tumLayout.setOnClickListener {
            val action= KokteyllerFiltreFragmentDirections.actionKokteyllerFragmentToKokteyllerFragment2(dataItem)
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

    fun kokteylListesiniGuncelle(yeniKokteylListesi: List<String>){
        tumKokteyller.clear()
        tumKokteyller.addAll(yeniKokteylListesi)
        notifyDataSetChanged()
    }




}