package com.example.paylasimmvvm.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowBinding
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.Bildirimler
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.util.TimeAgo
import com.example.paylasimmvvm.view.profil.UserProfilFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import java.util.*

class UserProfilRecyclerAdapter(var context: Context, private var tumKampanyalar: ArrayList<KullaniciKampanya>):
    RecyclerView.Adapter<UserProfilRecyclerAdapter.MyViewHolder>() {

    init {
        tumKampanyalar.sortWith { p0, p1 ->
            if (p0!!.postYuklenmeTarih!! > p1!!.postYuklenmeTarih!!) {
                -1
            } else 1
        }
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = RecyclerRowBinding.bind(itemView)



        private var profileImage =binding.profilImage
        private var userNameTitle = binding.kullaniciAdiTepe
        private var gonderi = binding.kampanyaPhoto
        private var userNameveAciklama = binding.textView21
        private var kampanyaTarihi = binding.kampanyaTarihi
        private var yorumYap = binding.imgYorum
        var gonderiBegen = binding.imgBegen
        var begenmeSayisi=binding.begenmeSayisi
        var yorumlariGoster=binding.tvYorumGoster
        private var postMenu=binding.postMesaj
        private var delete=binding.delete


        @SuppressLint("SetTextI18n")
        fun setData(anlikGonderi: KullaniciKampanya) {
            delete.visibility=View.GONE

            userNameTitle.text = anlikGonderi.userName
            if (anlikGonderi.userPhotoURL!!.isNotEmpty()){
                Picasso.get().load(anlikGonderi.userPhotoURL).placeholder(R.drawable.ic_baseline_person).error(
                    R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)


            }else {
                Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(
                    R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)


            }

            userNameveAciklama.text = anlikGonderi.userName.toString()+" "+anlikGonderi.postAciklama.toString()

            Picasso.get().load(anlikGonderi.postURL).into(gonderi)

            kampanyaTarihi.text = TimeAgo.getTimeAgo(anlikGonderi.postYuklenmeTarih!!)

            begeniKontrolu(anlikGonderi)
            yorumlariGoster(anlikGonderi)



            yorumYap.setOnClickListener {

                yorumlarFragmentiniBaslat(anlikGonderi)

            }

            postMenu.visibility= View.GONE



            yorumlariGoster.setOnClickListener {
                yorumlarFragmentiniBaslat(anlikGonderi)
            }

            gonderiBegen.setOnClickListener {

                val ref = FirebaseDatabase.getInstance().reference
                val currentID = FirebaseAuth.getInstance().currentUser!!.uid

                ref.child("begeniler").child(anlikGonderi.postID!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChild(currentID)) {
                                ref.child("begeniler").child(anlikGonderi.postID!!).child(currentID)
                                    .removeValue()
                                if (anlikGonderi.userID!= FirebaseAuth.getInstance().currentUser!!.uid){
                                    Bildirimler.bildirimKaydet(anlikGonderi.userID!!,
                                        Bildirimler.KAMPANYA_BEGENILDI_GERI,anlikGonderi.postID!!)


                                }
                                gonderiBegen.setImageResource(R.drawable.ic_launcher_like_foreground)

                            } else {
                                ref.child("begeniler").child(anlikGonderi.postID!!).child(currentID)
                                    .setValue(currentID)

                                if (anlikGonderi.userID!= FirebaseAuth.getInstance().currentUser!!.uid){
                                    Bildirimler.bildirimKaydet(anlikGonderi.userID!!,
                                        Bildirimler.KAMPANYA_BEGENILDI,anlikGonderi.postID!!)
                                }
                                gonderiBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)
                                begenmeSayisi.visibility= View.VISIBLE
                                begenmeSayisi.text = ""+ snapshot.childrenCount.toString()+" beğeni"



                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })

            }


        }

        private fun yorumlarFragmentiniBaslat(anlikGonderi: KullaniciKampanya) {


            EventBus.getDefault()
                .postSticky(EventbusData.YorumYapilacakGonderininIDsiniGonder(anlikGonderi.postID))

            val action= UserProfilFragmentDirections.actionUserProfilFragmentToYorumlarFragment()
            Navigation.findNavController(itemView).navigate(action)

        }


        private fun yorumlariGoster(anlikGonderi: KullaniciKampanya) {
            val mref= FirebaseDatabase.getInstance().reference
            mref.child("yorumlar").child(anlikGonderi.postID!!).addListenerForSingleValueEvent(object :
                ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    var yorumSayisi=0

                    for (ds in snapshot.children){
                        if (ds!!.key.toString() != anlikGonderi.postID){
                            yorumSayisi++
                        }

                    }


                    if (yorumSayisi>=1){
                        yorumlariGoster.visibility= View.VISIBLE
                        yorumlariGoster.text = "$yorumSayisi  yorum"


                    }else{
                        yorumlariGoster.visibility= View.GONE
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }

        private fun begeniKontrolu(anlikGonderi: KullaniciKampanya) {
            val mRef = FirebaseDatabase.getInstance().reference
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("begeniler").child(anlikGonderi.postID!!).addValueEventListener(object :
                ValueEventListener {


                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.value !=null){
                        begenmeSayisi.visibility= View.VISIBLE
                        begenmeSayisi.text = ""+ snapshot.childrenCount.toString()+" beğeni"

                    }else {
                        begenmeSayisi.visibility= View.GONE
                    }

                    if (snapshot.hasChild(userID)) {
                        gonderiBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)
                    } else {
                        gonderiBegen.setImageResource(R.drawable.ic_launcher_like_foreground)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }


            })




        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val viewHolder = LayoutInflater.from(context).inflate(R.layout.recycler_row, parent, false)

        return MyViewHolder(viewHolder)


    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(tumKampanyalar[position])


    }

    override fun getItemCount(): Int {
        return tumKampanyalar.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun kampanyalariGuncelle(yeniKampanyaListesi:List<KullaniciKampanya>){
        tumKampanyalar.clear()
        tumKampanyalar.addAll(yeniKampanyaListesi)
        notifyDataSetChanged()
    }
}