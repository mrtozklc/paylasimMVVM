package com.example.paylasimmvvm.adapter;

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowMesajAlanBinding
import com.example.paylasimmvvm.model.ChatModel
import com.google.firebase.auth.FirebaseAuth


class ChatFragmentRecyclerAdapter(var tumMesajlar:ArrayList<ChatModel>): RecyclerView.Adapter<ChatFragmentRecyclerAdapter.ViewHolder>(){

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val binding=RecyclerRowMesajAlanBinding.bind(itemView)
        var tvmesajGonderen=binding.tvMesaj

        fun setData(anlikMesaj: ChatModel) {
            tvmesajGonderen.text=anlikMesaj.mesaj

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var myView:View?=null

        if(viewType==1){
            myView=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_mesaj_gonderen,parent,false)
            return ViewHolder(myView)

        }else  {
            myView=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_mesaj_alan,parent,false)
            return ViewHolder(myView)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tumMesajlar.get(position))
    }

    override fun getItemCount(): Int {
     return tumMesajlar.size
    }
    override fun getItemViewType(position: Int): Int {
        if(tumMesajlar.get(position).user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
            return 1
        }else return 2
    }
    fun mesajlariGuncelle(yeniMesajListesi:List<ChatModel>){
        tumMesajlar.clear()
        tumMesajlar.addAll(yeniMesajListesi)
        notifyDataSetChanged()



    }


}
