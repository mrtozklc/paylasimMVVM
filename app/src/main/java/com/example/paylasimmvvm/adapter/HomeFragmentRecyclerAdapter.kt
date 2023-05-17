package com.example.paylasimmvvm.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ScaleGestureDetector.OnScaleGestureListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowBinding
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.Bildirimler
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.util.TimeAgo
import com.example.paylasimmvvm.view.home.HomeFragmentDirections
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.math.RoundingMode


class HomeFragmentRecyclerAdapter (var context: Context, private var tumKampanyalar:ArrayList<KullaniciKampanya>): RecyclerView.Adapter<HomeFragmentRecyclerAdapter.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val binding= RecyclerRowBinding.bind(itemView)
        private var profileImage = binding.profilImage
        private var userNameTitle = binding.kullaniciAdiTepe
        private var gonderi = binding.kampanyaPhoto
        private var userNameveAciklama = binding.textView21
        private var kampanyaTarihi = binding.kampanyaTarihi
        private var yorumYap = binding.imgYorum
        var gonderiBegen = binding.imgBegen
        var begenmeSayisi=binding.begenmeSayisi
        var yorumlariGoster=binding.tvYorumGoster
        private var postMenu=binding.postMesaj
        var geriSayim=binding.geriSayimId
        private var delete=binding.delete

        @SuppressLint("SetTextI18n")
        fun setData(anlikGonderi: KullaniciKampanya) {

            delete.visibility=View.GONE


            userNameTitle.text = anlikGonderi.userName

            if (anlikGonderi.userPhotoURL!!.isNotEmpty()){
                Picasso.get().load(anlikGonderi.userPhotoURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)
            }

            else {
                Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)
            }

            userNameveAciklama.text = anlikGonderi.userName.toString()+" "+anlikGonderi.postAciklama.toString()

            Picasso.get().load(anlikGonderi.postURL).into(gonderi)



            gonderi.setOnClickListener {

                val builder = AlertDialog.Builder(itemView.context)

                val imageView = PhotoView(itemView.context)
                imageView.adjustViewBounds = true

                Picasso.get().load(anlikGonderi.postURL).into(imageView)

                builder.setView(imageView)
                val alertDialog = builder.create()
                alertDialog.setCanceledOnTouchOutside(true)
                imageView.setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.show()

            }



            kampanyaTarihi.text = TimeAgo.getTimeAgo(anlikGonderi.postYuklenmeTarih!!)

            if (anlikGonderi.geri_sayim=="1 saat"){

                var time = anlikGonderi.postYuklenmeTarih

                if (time != null) {
                    if (time < 1000000000000L) {
                        // if timestamp given in seconds, convert to millis
                        time *= 1000
                    }

                }
                val now = System.currentTimeMillis()
                if (time != null) {
                    if (time > now || time <= 0) {
                    }else{
                        val difff =  now - time
                        if (difff>=3600000){
                            geriSayim.text = "kampanya süresi doldu!"
                        }
                        object : CountDownTimer(3600000-difff, 1000) {

                            override fun onTick(millisUntilFinished: Long) {
                                var diff = millisUntilFinished
                                val secondsInMilli: Long = 1000
                                val minutesInMilli = secondsInMilli * 60
                                val hoursInMilli = minutesInMilli * 60
                                val elapsedHours = diff / hoursInMilli
                                diff %= hoursInMilli
                                val elapsedMinutes = diff / minutesInMilli
                                diff %= minutesInMilli
                                val elapsedSeconds = diff / secondsInMilli
                                geriSayim.text = "$elapsedHours s $elapsedMinutes d $elapsedSeconds "

                            }
                            override fun onFinish() {
                                geriSayim.text = "kampanya süresi doldu!"
                            }
                        }.start()
                    }
                }
            }

            else if (anlikGonderi.geri_sayim=="2 saat"){

                var time = anlikGonderi.postYuklenmeTarih

                if (time != null) {
                    if (time < 1000000000000L) {
                        // if timestamp given in seconds, convert to millis
                        time *= 1000
                    }
                }
                val now = System.currentTimeMillis()
                if (time != null) {
                    if (time > now || time <= 0) {

                    }else{
                        val difff =  now - time

                        if (difff>=7200000){
                            geriSayim.text = "kampanya süresi doldu!"

                        }

                        object : CountDownTimer(7200000-difff, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                var diff = millisUntilFinished
                                val secondsInMilli: Long = 1000
                                val minutesInMilli = secondsInMilli * 60
                                val hoursInMilli = minutesInMilli * 60
                                val elapsedHours = diff / hoursInMilli
                                diff %= hoursInMilli
                                val elapsedMinutes = diff / minutesInMilli
                                diff %= minutesInMilli
                                val elapsedSeconds = diff / secondsInMilli
                                geriSayim.text = "$elapsedHours s $elapsedMinutes d $elapsedSeconds "
                            }
                            override fun onFinish() {
                                geriSayim.text = "kampanya süresi doldu!"
                            }
                        }.start()
                    }
                }
            }

            else if(anlikGonderi.geri_sayim=="3 saat"){
                var time = anlikGonderi.postYuklenmeTarih
                if (time != null) {
                    if (time < 1000000000000L) {
                        // if timestamp given in seconds, convert to millis
                        time *= 1000
                    }
                }
                val now = System.currentTimeMillis()
                if (time != null) {
                    if (time > now || time <= 0) {

                    }else if(anlikGonderi.geri_sayim=="3 saat"){
                        val difff =  now - time
                        if (difff>=10800000){
                            geriSayim.text = "kampanya süresi doldu!"
                        }
                        object : CountDownTimer(10800000-difff, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                var diff = millisUntilFinished
                                val secondsInMilli: Long = 1000
                                val minutesInMilli = secondsInMilli * 60
                                val hoursInMilli = minutesInMilli * 60
                                val elapsedHours = diff / hoursInMilli
                                diff %= hoursInMilli
                                val elapsedMinutes = diff / minutesInMilli
                                diff %= minutesInMilli
                                val elapsedSeconds = diff / secondsInMilli
                                geriSayim.text = "$elapsedHours s $elapsedMinutes d $elapsedSeconds "

                            }
                            override fun onFinish() {
                                geriSayim.text = "kampanya süresi doldu!"
                            }
                        }.start()
                    }
                }
            }


            userNameTitle.setOnClickListener {

                if (anlikGonderi.userID!! == FirebaseAuth.getInstance().currentUser!!.uid){



                }else{
                    val action= HomeFragmentDirections.actionHomeFragmentToUserProfilFragment(anlikGonderi.userID!!)
                    Navigation.findNavController(it).navigate(action)

                }
            }

            postMenu.setOnClickListener {
                val mref= FirebaseDatabase.getInstance().reference
                val mauth= FirebaseAuth.getInstance().currentUser!!.uid

                mref.child("kampanya").child(anlikGonderi.userID!!).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.value !=null){
                            if (!anlikGonderi.userID.equals(mauth)){
                                val action=HomeFragmentDirections.actionHomeFragmentToChatFragment(anlikGonderi.userID!!)
                                Navigation.findNavController(it).navigate(action)
                            }else{
                                val action= HomeFragmentDirections.actionHomeFragmentToProfilFragment()
                                Navigation.findNavController(it).navigate(action)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            val mauth:String=FirebaseAuth.getInstance().currentUser!!.uid

            if (anlikGonderi.userID.equals(mauth)){
                postMenu.visibility=View.GONE
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
                                    Bildirimler.bildirimKaydet(
                                        anlikGonderi.userID!!,
                                        Bildirimler.KAMPANYA_BEGENILDI_GERI,
                                        anlikGonderi.postID!!)
                                }
                                gonderiBegen.setImageResource(R.drawable.ic_baseline_favorite)
                            } else {
                                ref.child("begeniler").child(anlikGonderi.postID!!).child(currentID)
                                    .setValue(currentID)
                                if (anlikGonderi.userID!= FirebaseAuth.getInstance().currentUser!!.uid){
                                    Bildirimler.bildirimKaydet(
                                        anlikGonderi.userID!!,
                                        Bildirimler.KAMPANYA_BEGENILDI,
                                        anlikGonderi.postID!!
                                    )
                                }
                                gonderiBegen.setImageResource(R.drawable.baseline_favorite_red_24)
                                begenmeSayisi.visibility=View.VISIBLE
                                begenmeSayisi.text = ""+ snapshot.childrenCount.toString()+" beğeni"
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }
            begeniKontrolu(anlikGonderi)

            yorumlariGoster(anlikGonderi)

            yorumYap.setOnClickListener {
                yorumlarFragmentiniBaslat(anlikGonderi)
            }

            yorumlariGoster.setOnClickListener {

                yorumlarFragmentiniBaslat(anlikGonderi)
            }

            if (anlikGonderi.userID.equals(mauth)){
                postMenu.visibility=View.GONE
            }
        }

        private fun begeniKontrolu(anlikGonderi: KullaniciKampanya) {

            val mRef = FirebaseDatabase.getInstance().reference
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("begeniler").child(anlikGonderi.postID!!).addValueEventListener(object : ValueEventListener {

                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.value !=null){

                        begenmeSayisi.visibility=View.VISIBLE
                        begenmeSayisi.text = ""+ snapshot.childrenCount.toString()+" beğeni"

                    }else {
                        begenmeSayisi.visibility=View.GONE
                    }

                    if (snapshot.hasChild(userID)) {
                        gonderiBegen.setImageResource(R.drawable.baseline_favorite_red_24)
                    } else {
                        gonderiBegen.setImageResource(R.drawable.ic_baseline_favorite)

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        private fun yorumlarFragmentiniBaslat(anlikGonderi: KullaniciKampanya) {

            if (anlikGonderi.userID!=FirebaseAuth.getInstance().currentUser!!.uid){

                Bildirimler.bildirimKaydet(anlikGonderi.userID!!,Bildirimler.YORUM_YAPILDI,anlikGonderi.postID!!)
            }
           val action= HomeFragmentDirections.actionHomeFragmentToCommentFragment(anlikGonderi.postID!!,true)
            Navigation.findNavController(itemView).navigate(action)


        }

        private fun yorumlariGoster(anlikGonderi: KullaniciKampanya) {
            val mref=FirebaseDatabase.getInstance().reference
            mref.child("yorumlar").child(anlikGonderi.postID!!).addListenerForSingleValueEvent(object :ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    var yorumSayisi=0
                    for (ds in snapshot.children){
                        if (ds!!.key.toString() != anlikGonderi.postID){
                            yorumSayisi++
                        }
                    }
                    if (yorumSayisi>=1){
                        yorumlariGoster.visibility=View.VISIBLE
                        yorumlariGoster.text = "$yorumSayisi  yorum"
                    }else{
                        yorumlariGoster.visibility=View.GONE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val viewHolder = LayoutInflater.from(context).inflate(R.layout.recycler_row, parent, false)

        return ViewHolder(viewHolder)
    }
    override fun getItemCount(): Int {
        return tumKampanyalar.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tumKampanyalar[position])

    }
    @SuppressLint("NotifyDataSetChanged")
    fun kampanyalariGuncelle(yeniKampanyaListesi:List<KullaniciKampanya>){
        tumKampanyalar.clear()
        tumKampanyalar.addAll(yeniKampanyaListesi)
        notifyDataSetChanged()
    }
}