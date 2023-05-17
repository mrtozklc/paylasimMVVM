package com.example.paylasimmvvm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowMudavimlerBinding
import com.example.paylasimmvvm.model.Mudavimler
import com.example.paylasimmvvm.view.home.HomeFragmentDirections
import com.example.paylasimmvvm.view.profil.ProfilFragmentDirections
import com.example.paylasimmvvm.view.profil.UserProfilFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MudavimlerRecyclerAdapter(private val tumMudavimler: ArrayList<Mudavimler>,private val isProfileFragment:Boolean): RecyclerView.Adapter<MudavimlerRecyclerAdapter.MViewHolder>() {

    class MViewHolder(itemview: View,private val isProfileFragment:Boolean): RecyclerView.ViewHolder(itemview){

        fun setData(gelenMudavim: Mudavimler) {
            val binding= RecyclerRowMudavimlerBinding.bind(itemView)


            binding.mudavimUsername.text=gelenMudavim.mudavim_userName

            if (gelenMudavim.mudavim_photo!!.isNotEmpty()){
                Picasso.get().load(gelenMudavim.mudavim_photo).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).fit().centerCrop().into(binding.profilPhoto)
            }else{
                Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).fit().centerCrop().into(binding.profilPhoto)
            }
            binding.tumLayout.setOnClickListener {
                if (!isProfileFragment){
                    if (gelenMudavim.mudavim_id!! != FirebaseAuth.getInstance().currentUser!!.uid){
                        val action = UserProfilFragmentDirections.actionUserProfilFragmentSelf(gelenMudavim.mudavim_id!!)
                        Navigation.findNavController(itemView).navigate(action)


                    }

                }else{
                    if (gelenMudavim.mudavim_id!! != FirebaseAuth.getInstance().currentUser!!.uid){
                        val action = ProfilFragmentDirections.actionProfilFragmentToUserProfilFragment(gelenMudavim.mudavim_id!!)
                        Navigation.findNavController(itemView).navigate(action)


                    }


                }



            }






        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MViewHolder {

        val viewHolder = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_mudavimler, parent, false)

        return MViewHolder(viewHolder,isProfileFragment)
    }

    override fun onBindViewHolder(holder: MViewHolder, position: Int) {

        holder.setData(tumMudavimler[position])



    }

    override fun getItemCount(): Int {
        return tumMudavimler.size
    }

    fun mudavimListesiniGuncelle(yeniMudavimListesi: List<Mudavimler>){
        tumMudavimler.clear()
        tumMudavimler.addAll(yeniMudavimListesi)
        notifyDataSetChanged()
    }


}
