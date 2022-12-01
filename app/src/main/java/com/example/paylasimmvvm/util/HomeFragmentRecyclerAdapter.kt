package com.example.paylasimmvvm.util

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowBinding
import com.example.paylasimmvvm.model.KullaniciKampanya

class HomeFragmentRecyclerAdapter (var tumKampanyalar:ArrayList<KullaniciKampanya>):RecyclerView.Adapter<HomeFragmentRecyclerAdapter.ViewHolder>(){

    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView) {

        val binding=RecyclerRowBinding.bind(itemView)
        var profileImage = binding.profilImage
        var userNameTitle = binding.kullaniciAdiTepe
        var gonderi = binding.kampanyaPhoto
        var userNameveAciklama = binding.textView21

        var kampanyaTarihi = binding.kampanyaTarihiId
        var yorumYap = binding.imgYorum
        var gonderiBegen = binding.imgBegen
        var begenmeSayisi=binding.begenmeSayisi
        var yorumlariGoster=binding.tvYorumGoster
        var postMenu=binding.postMesaj
        var geriSayim=binding.geriSayimId
        var mesafe=binding.twMesafe
        var delete=binding.delete

        fun setData(position: Int, anlikGonderi: KullaniciKampanya){

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row, parent, false)

        return ViewHolder(view)

    }


    override fun getItemCount(): Int {
       return tumKampanyalar.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(position, tumKampanyalar[position])
        holder.userNameveAciklama.text=tumKampanyalar.get(position).userName
        holder.userNameTitle.text=tumKampanyalar.get(position).geri_sayim
    }

    fun kampanyalariGuncelle(yeniKampanyaListesi:List<KullaniciKampanya>){
        tumKampanyalar.clear()
        tumKampanyalar.addAll(yeniKampanyaListesi)
        notifyDataSetChanged()
    }

}


