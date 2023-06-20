package com.example.paylasimmvvm.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowIsletmeListBinding
import com.example.paylasimmvvm.createPost.KullaniciKampanya
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.math.BigDecimal
import java.math.RoundingMode

class IsletmeListRecyclerAdapter(var context: Context, private var tumKampanyalar:ArrayList<KullaniciKampanya>): RecyclerView.Adapter<IsletmeListRecyclerAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val binding= RecyclerRowIsletmeListBinding.bind(itemView)

        private var profileImage = binding.profilImage
        private var userNameTitle = binding.kullaniciAdiTepe
        private var isletmeTuru = binding.textView21
        var mesajGonder = binding.imgMesaj
        private var tumLayout=binding.tumLayout
        var muzikTuru=binding.geriSayimId
        var mesafe=binding.twMesafe
        var mudavimSayisi=binding.mudavimSayisi


        @SuppressLint("SetTextI18n")
        fun setData(anlikGonderi: KullaniciKampanya) {

            if (anlikGonderi.userID.equals(FirebaseAuth.getInstance().currentUser?.uid)){
                mesajGonder.visibility=View.GONE
            }


            FirebaseDatabase.getInstance().reference.child("konumlar").child("kullanici_konum").child(
                FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object :
                    ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(snapshot: DataSnapshot) {


                        val latitude = snapshot.child("latitude").value.toString()
                        val longitude = snapshot.child("longitude").value.toString()
                        if (snapshot.exists()){
                            if (anlikGonderi.isletmeLatitude!=0.0 && anlikGonderi.isletmeLongitude!=0.0) {

                                val startPoint = Location("locationA")
                                startPoint.latitude = anlikGonderi.isletmeLatitude!!
                                startPoint.longitude = anlikGonderi.isletmeLongitude!!

                                val endPoint = Location("locationA")
                                endPoint.latitude = latitude.toDouble()
                                endPoint.longitude = longitude.toDouble()

                                val distance: Int =
                                    startPoint.distanceTo(endPoint).toDouble().toInt()

                                if (distance>1000){
                                    val distanceKM=distance.toDouble()/1000

                                    println(BigDecimal(distanceKM))
                                    val k=  distanceKM.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                                    mesafe.text = "$k   km"

                                }else{
                                    mesafe.text = "$distance   metre"
                                }
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })



            userNameTitle.text = anlikGonderi.userName


            if (anlikGonderi.userPhotoURL!!.isNotEmpty()){
                Picasso.get().load(anlikGonderi.userPhotoURL).placeholder(R.drawable.ic_baseline_person).error(
                    R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)
            }

            else {

                Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(
                    R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)

            }

            isletmeTuru.text =  anlikGonderi.isletme_turu

            muzikTuru.text=anlikGonderi.muzik_turu


            mudavimSayisi.text="Müdavim Sayısı: ${anlikGonderi.mudavim_sayisi.toString()}"


            tumLayout.setOnClickListener {

                if (anlikGonderi.userID!! != FirebaseAuth.getInstance().currentUser!!.uid){
                    val action=
                        IsletmeListFragmentDirections.actionIsletmeListFragmentToUserProfilFragment(
                            anlikGonderi.userID!!
                        )
                    Navigation.findNavController(it).navigate(action)

                }
            }




            mesajGonder.setOnClickListener {
                val action= IsletmeListFragmentDirections.actionIsletmeListFragmentToChatFragment(
                    anlikGonderi.userID!!
                )
                Navigation.findNavController(it).navigate(action)

            }




        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val viewHolder = LayoutInflater.from(context).inflate(R.layout.recycler_row_isletme_list, parent, false)

        return ViewHolder(viewHolder)
    }
    override fun getItemCount(): Int {
        return tumKampanyalar.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tumKampanyalar[position])

    }
    @SuppressLint("NotifyDataSetChanged")
    fun IsletmeleriGuncelle(yeniIsletmeListesi:List<KullaniciKampanya>){
        tumKampanyalar.clear()
        tumKampanyalar.addAll(yeniIsletmeListesi)
        notifyDataSetChanged()
    }
}
