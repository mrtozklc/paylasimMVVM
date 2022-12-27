package com.example.paylasimmvvm.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowMesajlarBinding
import com.example.paylasimmvvm.model.Mesajlar
import com.example.paylasimmvvm.util.TimeAgo
import com.example.paylasimmvvm.view.mesajlar.MesajlarFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MesajlarRecyclerAdapter(private var tumMesajlar:ArrayList<Mesajlar>):RecyclerView.Adapter<MesajlarRecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val binding=RecyclerRowMesajlarBinding.bind(itemView)
        private var sonAtilanmesaj=binding.sonMesajId
        private var gonderilmeZamani=binding.zamanOnceId
        var userpp=binding.imgKonusmalarpp
        var userName=binding.tvUsername
        private var okunduBilgisi=binding.okunduBilgisi

        var mref= FirebaseDatabase.getInstance().reference


        @SuppressLint("SetTextI18n")
        fun setData(oankiKonusmalar: Mesajlar) {

            var sonAtilanmesajText=oankiKonusmalar.son_mesaj.toString()


            if(sonAtilanmesajText.isNotEmpty()){
                sonAtilanmesajText=sonAtilanmesajText.replace("\n"," ")
                sonAtilanmesajText=sonAtilanmesajText.trim()

                if(sonAtilanmesajText.length>25){

                    sonAtilanmesaj.text=sonAtilanmesajText.substring(0,25)+"..."
                }else{
                    sonAtilanmesaj.text=sonAtilanmesajText
                }
            }else{
                sonAtilanmesajText=""
                sonAtilanmesaj.text=sonAtilanmesajText
            }

            gonderilmeZamani.text=TimeAgo.getTimeAgoForComments(oankiKonusmalar.gonderilmeZamani!!.toLong())

            if(oankiKonusmalar.goruldu==false){


                okunduBilgisi.visibility=View.VISIBLE
                userName.setTypeface(null, Typeface.BOLD)
                sonAtilanmesaj.setTypeface(null,Typeface.BOLD)
                sonAtilanmesaj.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))
                gonderilmeZamani.setTextColor(ContextCompat.getColor(itemView.context,R.color.black))

            }
            else {
                okunduBilgisi.visibility=View.INVISIBLE
                userName.setTypeface(null,Typeface.NORMAL)
                sonAtilanmesaj.setTypeface(null,Typeface.NORMAL)
                gonderilmeZamani.setTextColor(ContextCompat.getColor(itemView.context,R.color.yesil))
                sonAtilanmesaj.setTextColor(ContextCompat.getColor(itemView.context,R.color.yesil))

            }

            binding. tumLayout.setOnClickListener {
                val action= MesajlarFragmentDirections.actionMesajlarFragmentToChatFragment(oankiKonusmalar.user_id!!)


                FirebaseDatabase.getInstance().reference
                    .child("konusmalar")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(oankiKonusmalar.user_id.toString())
                    .child("goruldu").setValue(true)
                    .addOnCompleteListener {
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.mesajlarFragment, true)
                            .build()
                        Navigation.findNavController(binding.tumLayout).navigate(action, navOptions)

                    }

            }


            binding.tumLayout.setOnLongClickListener(View.OnLongClickListener {

                val alert = androidx.appcompat.app.AlertDialog.Builder(itemView.context, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog_Alert)
                    .setTitle("MESAJI SİL ")
                    .setPositiveButton("SİL") { p0, p1 ->
                        val silinicekKonusma = oankiKonusmalar.user_id


                        mref.child("mesajlar").child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(silinicekKonusma!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    snapshot.ref.removeValue()

                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })

                        mref.child("konusmalar")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(silinicekKonusma)
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

                return@OnLongClickListener true


            })


            konusulanKisininBilgilerinigetir(oankiKonusmalar.user_id.toString())

        }


        private fun konusulanKisininBilgilerinigetir(userID: String) {

            val mref= FirebaseDatabase.getInstance().reference

            mref.child("users").child("kullanicilar").child(userID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){



                        userName.text=snapshot.child("user_name").value.toString()

                        Picasso.get().load(snapshot.child("user_detail").child("profile_picture").value.toString()).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(userpp)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            mref.child("users").child("isletmeler").child(userID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){

                        userName.text=snapshot.child("user_name").value.toString()
                        if (snapshot.child("user_detail").child("profile_picture").value.toString().isNotEmpty()){
                            Picasso.get().load(snapshot.child("user_detail").child("profile_picture").value.toString()).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(userpp)

                        }else{
                            Picasso.get().load(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(userpp)

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_mesajlar,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tumMesajlar[position])
    }

    override fun getItemCount(): Int {
      return tumMesajlar.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun mesajlariGuncelle(yeniMesajListesi:List<Mesajlar>){
        tumMesajlar.clear()
        tumMesajlar.addAll(yeniMesajListesi)
        notifyDataSetChanged()
    }
}