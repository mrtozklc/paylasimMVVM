package com.example.paylasimmvvm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowBildirimlerBinding
import com.example.paylasimmvvm.model.BildirimModel
import com.example.paylasimmvvm.util.TimeAgo
import com.example.paylasimmvvm.view.bildirimler.BildirimlerFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*

class BildirimlerRecyclerAdapter(private var tumBildirimler:ArrayList<BildirimModel>):
    RecyclerView.Adapter<BildirimlerRecyclerAdapter.mviewHolder>() {

    init {
        Collections.sort(tumBildirimler, object : Comparator<BildirimModel> {
            override fun compare(o1: BildirimModel?, o2: BildirimModel?): Int {
                return if (o1!!.time!! > o2!!.time!!) {
                    -1
                } else 1
            }
        })}






    class mviewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val binding = RecyclerRowBildirimlerBinding.bind(itemView)


        var gonderiBegenildi=binding.tvBegendi
        var yorumYapildi=binding.tvBegendi
        var begenenPP=binding.begenenppId
        var kampanya=binding.begenilenKampanyaId





        fun setdata(anlikBildirim: BildirimModel) {

            kampanya.setOnClickListener {
                val action=BildirimlerFragmentDirections.actionBildirimlerFragmentToProfilFragment()
                Navigation.findNavController(it).navigate(action)


            }

            if (anlikBildirim.bildirim_tur==1){

                idsiVerilenKullanicininBilgileriBegen(anlikBildirim.user_id, anlikBildirim.gonderi_id,anlikBildirim.time!!)


            }
            else if (anlikBildirim.bildirim_tur==2){


                idsiVerilenKullanicininBilgileriYorum(anlikBildirim.user_id, anlikBildirim.gonderi_id,anlikBildirim.time!!)

            }



        }


        private fun idsiVerilenKullanicininBilgileriYorum(user_id: String?, gonderi_id: String?, bildirimZamani: Long) {

            FirebaseDatabase.getInstance().getReference().child("users").child("kullanicilar").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue()!=null){

                        if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            val userName = snapshot!!.child("user_name").getValue().toString()
                            if (snapshot!!.child("user_name").getValue().toString().isNotEmpty())

                                yorumYapildi.setText(userName + " gönderine yorum yaptı.  " + TimeAgo.getTimeAgoForComments(bildirimZamani))




                            if (!snapshot!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                val takipEdenPicURL = snapshot!!.child("user_detail").child("profile_picture").getValue().toString()
                                Picasso.get().load(takipEdenPicURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)

                            }

                        }
                    }
                }




                override fun onCancelled(error: DatabaseError) {
                }

            })

            FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot)  {
                    if (snapshot.value !=null){

                        if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            val userName = snapshot!!.child("user_name").getValue().toString()
                            if (!snapshot!!.child("user_name").getValue().toString().isNullOrEmpty())

                                yorumYapildi.setText(userName + " gönderine yorum yaptı.  " + TimeAgo.getTimeAgoForComments(bildirimZamani))




                            if (!snapshot!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                var takipEdenPicURL = snapshot!!.child("user_detail").child("profile_picture").getValue().toString()
                                Picasso.get().load(takipEdenPicURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


            FirebaseDatabase.getInstance().getReference().child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(gonderi_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.getValue()!=null){
                            if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                                begenenPP.visibility=View.GONE
                                yorumYapildi.visibility=View.GONE
                                kampanya.visibility=View.GONE}

                            else if (!snapshot!!.child("file_url").getValue().toString().isNullOrEmpty()) {
                                kampanya.visibility = View.VISIBLE
                                var begenilenFotoURL = snapshot!!.child("file_url").getValue().toString()
                                Picasso.get().load(begenilenFotoURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(kampanya)

                            } else {
                                kampanya.visibility = View.INVISIBLE

                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })




        }

        private fun idsiVerilenKullanicininBilgileriBegen(user_id: String?, gonderi_id: String?, bildirimZamani: Long) {

            FirebaseDatabase.getInstance().getReference().child("users").child("kullanicilar").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue()!=null){

                        if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            var userName = snapshot!!.child("user_name").getValue().toString()
                            if (!snapshot!!.child("user_name").getValue().toString().isNullOrEmpty())

                                gonderiBegenildi.setText(userName + " Kampanyani Beğendi .  " + TimeAgo.getTimeAgoForComments(bildirimZamani))




                            if (!snapshot!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                var takipEdenPicURL = snapshot!!.child("user_detail").child("profile_picture").getValue().toString()
                                Picasso.get().load(takipEdenPicURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)

                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot)  {
                    if (snapshot.getValue()!=null){

                        if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            var userName = snapshot!!.child("user_name").getValue().toString()
                            if (!snapshot!!.child("user_name").getValue().toString().isNullOrEmpty())

                                gonderiBegenildi.setText(userName + " Kampanyani Beğendi .  " + TimeAgo.getTimeAgoForComments(bildirimZamani))




                            if (!snapshot!!.child("user_detail").child("profile_picture").getValue().toString().isNullOrEmpty()) {
                                var takipEdenPicURL = snapshot!!.child("user_detail").child("profile_picture").getValue().toString()
                                Picasso.get().load(takipEdenPicURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)

                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


            FirebaseDatabase.getInstance().getReference().child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(gonderi_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.getValue()!=null){
                            if (user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){  begenenPP.visibility=View.GONE
                                yorumYapildi.visibility=View.GONE
                                kampanya.visibility=View.GONE}
                            else


                                if (!snapshot!!.child("file_url").getValue().toString().isNullOrEmpty()) {
                                    kampanya.visibility = View.VISIBLE
                                    var begenilenFotoURL = snapshot!!.child("file_url").getValue().toString()
                                    Picasso.get().load(begenilenFotoURL).into(kampanya)
                                } else {
                                    kampanya.visibility = View.INVISIBLE

                                }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })



        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mviewHolder {

        var view=

            LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_bildirimler,parent,false)

        return BildirimlerRecyclerAdapter.mviewHolder(view)
    }

    override fun onBindViewHolder(holder: mviewHolder, position: Int) {

        holder.setdata(tumBildirimler.get(position))

    }

    override fun getItemCount(): Int {
        return tumBildirimler.size
    }
    fun kampanyalariGuncelle(yeniKampanyaListesi:List<BildirimModel>){
        tumBildirimler.clear()
        tumBildirimler.addAll(yeniKampanyaListesi)
        notifyDataSetChanged()
    }
}

