package com.example.paylasimmvvm.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
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
        tumBildirimler.sortWith { o1, o2 ->
            if (o1!!.time!! > o2!!.time!!) {
                -1
            } else 1
        }
    }






    class mviewHolder(itemview: View): RecyclerView.ViewHolder(itemview) {
        val binding = RecyclerRowBildirimlerBinding.bind(itemView)


        var gonderiBegenildi=binding.tvBegendi
        var yorumYapildi=binding.tvBegendi
        var begenenPP=binding.begenenppId
        var kampanya=binding.begenilenKampanyaId





        fun setdata(anlikBildirim: BildirimModel) {

            kampanya.setOnClickListener {
                val action=BildirimlerFragmentDirections.actionBildirimlerFragmentToProfilFragment()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.bildirimlerFragment, true)
                    .build()

                Navigation.findNavController(it).navigate(action,navOptions)


            }

            if (anlikBildirim.bildirim_tur==1){

                idsiVerilenKullanicininBilgileriBegen(anlikBildirim.user_id, anlikBildirim.gonderi_id,anlikBildirim.time!!)


            }
            else if (anlikBildirim.bildirim_tur==2){


                idsiVerilenKullanicininBilgileriYorum(anlikBildirim.user_id, anlikBildirim.gonderi_id,anlikBildirim.time!!)

            }



        }


        private fun idsiVerilenKullanicininBilgileriYorum(user_id: String?, gonderi_id: String?, bildirimZamani: Long) {

            FirebaseDatabase.getInstance().reference.child("users").child("kullanicilar").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){

                        if (user_id == FirebaseAuth.getInstance().currentUser!!.uid){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            val userName = snapshot.child("user_name").value.toString()
                            if (snapshot.child("user_name").value.toString().isNotEmpty())

                                yorumYapildi.text = userName + " gönderine yorum yaptı.  " + TimeAgo.getTimeAgoForComments(bildirimZamani)




                            if (snapshot.child("user_detail").child("profile_picture").value.toString().isNotEmpty()) {
                                val takipEdenPicURL = snapshot.child("user_detail").child("profile_picture").value.toString()
                                Picasso.get().load(takipEdenPicURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)

                            }else{
                                begenenPP.setBackgroundResource(R.drawable.ic_baseline_person)


                            }

                        }
                    }
                }




                override fun onCancelled(error: DatabaseError) {
                }

            })

            FirebaseDatabase.getInstance().reference.child("users").child("isletmeler").child(
                user_id
            ).addListenerForSingleValueEvent(object :ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot)  {
                    if (snapshot.value !=null){

                        if (user_id == FirebaseAuth.getInstance().currentUser!!.uid){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            val userName = snapshot.child("user_name").value.toString()
                            if (snapshot.child("user_name").value.toString().isNotEmpty())

                                yorumYapildi.text = userName + " gönderine yorum yaptı.  " + TimeAgo.getTimeAgoForComments(bildirimZamani)




                            if (snapshot.child("user_detail").child("profile_picture").value.toString()
                                    .isNotEmpty()) {
                                val takipEdenPicURL = snapshot.child("user_detail").child("profile_picture").value.toString()
                                Picasso.get().load(takipEdenPicURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)
                            }
                            else{
                                begenenPP.setBackgroundResource(R.drawable.ic_baseline_person)


                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


            FirebaseDatabase.getInstance().reference.child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(gonderi_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.value !=null){
                            if (user_id == FirebaseAuth.getInstance().currentUser!!.uid){
                                begenenPP.visibility=View.GONE
                                yorumYapildi.visibility=View.GONE
                                kampanya.visibility=View.GONE}

                            else if (snapshot.child("file_url").value.toString().isNotEmpty()) {
                                kampanya.visibility = View.VISIBLE
                                val begenilenFotoURL = snapshot.child("file_url").value.toString()
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

            FirebaseDatabase.getInstance().reference.child("users").child("kullanicilar").child(user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){

                        if (user_id == FirebaseAuth.getInstance().currentUser!!.uid){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            val userName = snapshot.child("user_name").value.toString()
                            if (snapshot.child("user_name").value.toString().isNotEmpty())

                                gonderiBegenildi.text = userName + " Kampanyani Beğendi .  " + TimeAgo.getTimeAgoForComments(bildirimZamani)




                            if (snapshot.child("user_detail").child("profile_picture").value.toString().isNotEmpty()) {
                                val takipEdenPicURL = snapshot.child("user_detail").child("profile_picture").value.toString()
                                Picasso.get().load(takipEdenPicURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)

                            }else{
                                begenenPP.setBackgroundResource(R.drawable.ic_baseline_person)
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            FirebaseDatabase.getInstance().reference.child("users").child("isletmeler").child(
                user_id
            ).addListenerForSingleValueEvent(object :ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot)  {
                    if (snapshot.value !=null){

                        if (user_id == FirebaseAuth.getInstance().currentUser!!.uid){
                            begenenPP.visibility=View.GONE
                            yorumYapildi.visibility=View.GONE
                            kampanya.visibility=View.GONE

                        }else{
                            val userName = snapshot.child("user_name").value.toString()
                            if (snapshot.child("user_name").value.toString().isNotEmpty())

                                gonderiBegenildi.text = userName + " Kampanyani Beğendi .  " + TimeAgo.getTimeAgoForComments(bildirimZamani)




                            if (snapshot.child("user_detail").child("profile_picture").value.toString()
                                    .isNotEmpty()) {
                                val takipEdenPicURL = snapshot.child("user_detail").child("profile_picture").value.toString()
                                Picasso.get().load(takipEdenPicURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)

                            }else{
                                begenenPP.setBackgroundResource(R.drawable.ic_baseline_person
                                )
                            }

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


            FirebaseDatabase.getInstance().reference.child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(gonderi_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.value !=null){
                            if (user_id == FirebaseAuth.getInstance().currentUser!!.uid){  begenenPP.visibility=View.GONE
                                yorumYapildi.visibility=View.GONE
                                kampanya.visibility=View.GONE}
                            else


                                if (snapshot.child("file_url").value.toString().isNotEmpty()) {
                                    kampanya.visibility = View.VISIBLE
                                    val begenilenFotoURL = snapshot.child("file_url").value.toString()
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

        val view=

            LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_bildirimler,parent,false)

        return mviewHolder(view)
    }

    override fun onBindViewHolder(holder: mviewHolder, position: Int) {

        holder.setdata(tumBildirimler[position])

    }

    override fun getItemCount(): Int {
        return tumBildirimler.size
    }
    @SuppressLint("NotifyDataSetChanged")
    fun kampanyalariGuncelle(yeniKampanyaListesi:List<BildirimModel>){
        tumBildirimler.clear()
        tumBildirimler.addAll(yeniKampanyaListesi)
        notifyDataSetChanged()
    }
}

