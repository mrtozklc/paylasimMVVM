package com.example.paylasimmvvm.massage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowMesajAlanBinding
import com.google.firebase.auth.FirebaseAuth


class ChatFragmentRecyclerAdapter(private var tumMesajlar:ArrayList<ChatModel>): RecyclerView.Adapter<ChatFragmentRecyclerAdapter.ViewHolder>(){

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val binding=RecyclerRowMesajAlanBinding.bind(itemView)
        private var tvmesajGonderen=binding.tvMesaj

        fun setData(anlikMesaj: ChatModel) {
            tvmesajGonderen.text=anlikMesaj.mesaj

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val myView: View?

        return if(viewType==1){
            myView=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_mesaj_gonderen,parent,false)
            ViewHolder(myView)

        }else  {
            myView=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_mesaj_alan,parent,false)
            ViewHolder(myView)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tumMesajlar[position])
    }

    override fun getItemCount(): Int {
     return tumMesajlar.size
    }
    override fun getItemViewType(position: Int): Int {
        return if(tumMesajlar[position].user_id!! == FirebaseAuth.getInstance().currentUser!!.uid){
            1
        }else 2
    }
    @SuppressLint("NotifyDataSetChanged")
    fun mesajlariGuncelle(yeniMesajListesi:List<ChatModel>){
        tumMesajlar.clear()
        tumMesajlar.addAll(yeniMesajListesi)
        notifyDataSetChanged()



    }


}
