package com.example.paylasimmvvm.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.SearchItemBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.view.home.HomeFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class SearchviewRecyclerAdapter(private var data: ArrayList<KullaniciBilgileri>,private val isQueryResult: Boolean) :
    RecyclerView.Adapter<SearchviewRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View,private val isQueryResult: Boolean) : RecyclerView.ViewHolder(itemView) {


        fun bind(searchResult: KullaniciBilgileri) {

            val binding = SearchItemBinding.bind(itemView)
            binding.username.text = searchResult.user_name
            binding.adiSoyadi.text=searchResult.adi_soyadi


           if (searchResult.user_detail!!.profile_picture!!.isNotEmpty()){
                Picasso.get().load(searchResult.user_detail!!.profile_picture!!).placeholder(R.drawable.ic_baseline_person).error(
                    R.drawable.ic_baseline_person).fit().centerCrop().into(binding.profilImage)


            }else {
                Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(
                    R.drawable.ic_baseline_person).fit().centerCrop().into(binding.profilImage)


            }
            if (isQueryResult) {
                binding.close.visibility = View.GONE
            } else {
                binding.close.visibility = View.VISIBLE
            }
            binding.tumLayout.setOnClickListener{
                val searchHistory=HashMap<String,Any>()
                searchHistory["user_id"] = searchResult.user_id!!

                FirebaseDatabase.getInstance().reference.child("SearchHistory").child(FirebaseAuth.getInstance().currentUser!!.uid).child(searchResult.user_id!!).setValue(searchHistory)
                val action= HomeFragmentDirections.actionHomeFragmentToUserProfilFragment(searchResult.user_id!!)
                Navigation.findNavController(itemView).navigate(action)

            }

            binding.close.setOnClickListener {

                val alert = androidx.appcompat.app.AlertDialog.Builder(itemView.context, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert)
                    .setTitle("Arama Geçmişinden Kaldır ")
                    .setPositiveButton("Kaldır") { p0, p1 ->
                        val silinicekKisi = searchResult.user_id


                        FirebaseDatabase.getInstance().reference.child("SearchHistory").child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(silinicekKisi!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    snapshot.ref.removeValue()


                                }
                                override fun onCancelled(error: DatabaseError) {
                                }
                            })


                    }
                    .setNegativeButton("VAZGEÇ"
                    ) { p0, p1 -> p0!!.dismiss() }
                    .create()

                alert.show()

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewHolder =
            LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)

        return MyViewHolder(viewHolder,isQueryResult)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(data[position])



    }

}