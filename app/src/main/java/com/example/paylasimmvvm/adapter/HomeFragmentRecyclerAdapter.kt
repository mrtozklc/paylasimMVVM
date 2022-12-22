package com.example.paylasimmvvm.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.CountDownTimer
import android.util.Log
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
import com.example.paylasimmvvm.view.home.HomeFragment
import com.example.paylasimmvvm.view.home.HomeFragmentDirections
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal
import java.math.RoundingMode


class HomeFragmentRecyclerAdapter (var context: Context, var tumKampanyalar:ArrayList<KullaniciKampanya>): RecyclerView.Adapter<HomeFragmentRecyclerAdapter.ViewHolder>(){

    class ViewHolder(itemView: View,homeFragment:Context): RecyclerView.ViewHolder(itemView) {

        val binding= RecyclerRowBinding.bind(itemView)
        var profileImage = binding.profilImage
        var userNameTitle = binding.kullaniciAdiTepe
        var gonderi = binding.kampanyaPhoto
        var userNameveAciklama = binding.textView21
        var myHomeFragment = homeFragment
        var kampanyaTarihi = binding.kampanyaTarihiId
        var yorumYap = binding.imgYorum
        var gonderiBegen = binding.imgBegen
        var begenmeSayisi=binding.begenmeSayisi
        var yorumlariGoster=binding.tvYorumGoster
        var postMenu=binding.postMesaj
        var geriSayim=binding.geriSayimId
        var mesafe=binding.twMesafe
        var delete=binding.delete

        fun setData(position: Int, anlikGonderi: KullaniciKampanya) {

            delete.visibility=View.GONE

            var ref = FirebaseDatabase.getInstance().reference.child("konumlar").child("kullanici_konum").child(
                FirebaseAuth.getInstance().currentUser?.uid.toString())
                .child(FirebaseAuth.getInstance().currentUser?.uid.toString()).addValueEventListener(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val latitude = snapshot.child("latitude").value.toString()
                        val longitude = snapshot.child("longitude").value.toString()

                        if (snapshot.exists()){
                            if (anlikGonderi.isletmeLatitude!=0.0 && anlikGonderi.isletmeLongitude!=0.0) {

                                val startPoint = Location("locationA")
                                startPoint.setLatitude(anlikGonderi.isletmeLatitude!!)
                                startPoint.setLongitude(anlikGonderi.isletmeLongitude!!)

                                val endPoint = Location("locationA")
                                endPoint.setLatitude(latitude.toDouble())
                                endPoint.setLongitude(longitude.toDouble())

                                val distance: Int =
                                    startPoint.distanceTo(endPoint).toDouble().toInt()


                                if (distance>1000){
                                    var distanceKM=distance.toDouble()/1000

                                    println(BigDecimal(distanceKM))
                                    var k=  distanceKM.toBigDecimal().setScale(2, RoundingMode.UP).toDouble()
                                    mesafe.setText(k.toDouble().toString() + "   km")


                                }else{
                                    mesafe.setText(distance.toString() + "   metre")


                                }


                            }


                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })



            userNameTitle.setText(anlikGonderi.userName)





            if (!anlikGonderi.userPhotoURL!!.isEmpty()){
                Picasso.get().load(anlikGonderi.userPhotoURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)


            }else {
                Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)


            }

            userNameveAciklama.setText(anlikGonderi.userName.toString()+" "+anlikGonderi.postAciklama.toString())

          Picasso.get().load(anlikGonderi.postURL).into(gonderi)



           kampanyaTarihi.setText(TimeAgo.getTimeAgo(anlikGonderi.postYuklenmeTarih!!))


            if (anlikGonderi.geri_sayim=="1 saat"){

                var time = anlikGonderi.postYuklenmeTarih

                if (time != null) {
                    if (time < 1000000000000L) {
                        // if timestamp given in seconds, convert to millis
                        time *= 1000

                    }
                    Log.e("gelentimecarpilmis","dsa"+time)
                    Log.e("gelentimecarpilmis","dsa"+ System.currentTimeMillis())
                }
                val now = System.currentTimeMillis()
                if (time != null) {
                    if (time > now || time <= 0) {


                    }else{

                        val difff =  now - time!!

                        if (difff>=3600000){
                            geriSayim.setText("kampanya süresi doldu!")


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



                                geriSayim.setText("$elapsedHours s $elapsedMinutes d $elapsedSeconds ")

                            }

                            override fun onFinish() {
                                geriSayim.setText("kampanya süresi doldu!")
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

                        val difff =  now - time!!

                        if (difff>=7200000){
                            geriSayim.setText("kampanya süresi doldu!")


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



                                geriSayim.setText("$elapsedHours s $elapsedMinutes d $elapsedSeconds ")

                            }

                            override fun onFinish() {
                                geriSayim.setText("kampanya süresi doldu!")
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

                        val difff =  now - time!!

                        if (difff>=10800000){
                            geriSayim.setText("kampanya süresi doldu!")


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



                                geriSayim.setText("$elapsedHours s $elapsedMinutes d $elapsedSeconds ")

                            }

                            override fun onFinish() {
                                geriSayim.setText("kampanya süresi doldu!")
                            }

                        }.start()


                    }
                }


            }

            userNameTitle.setOnClickListener {


                if (anlikGonderi.userID!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)){


                    val action= HomeFragmentDirections.actionHomeFragmentToProfilFragment()
                    Navigation.findNavController(it).navigate(action)


                }else{

                    val action= HomeFragmentDirections.actionHomeFragmentToUserProfilFragment(anlikGonderi.userID!!)
                    Navigation.findNavController(it).navigate(action)



                }

            }


            postMenu.setOnClickListener {
                var mref= FirebaseDatabase.getInstance().reference
                var mauth= FirebaseAuth.getInstance().currentUser!!.uid


                mref.child("kampanya").child(anlikGonderi.userID!!).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot!!.getValue()!=null){
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

                var ref = FirebaseDatabase.getInstance().reference
                var currentID = FirebaseAuth.getInstance().currentUser!!.uid

                ref.child("begeniler").child(anlikGonderi.postID!!)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot!!.hasChild(currentID)) {

                                ref.child("begeniler").child(anlikGonderi.postID!!).child(currentID)
                                    .removeValue()

                                if (anlikGonderi.userID!= FirebaseAuth.getInstance().currentUser!!.uid){
                                    Bildirimler.bildirimKaydet(
                                        anlikGonderi.userID!!,
                                        Bildirimler.KAMPANYA_BEGENILDI_GERI,
                                        anlikGonderi.postID!!
                                    )


                                }

                        gonderiBegen.setImageResource(R.drawable.ic_launcher_like_foreground)



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
                             gonderiBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)

                                begenmeSayisi.visibility=View.VISIBLE
                                begenmeSayisi.setText(""+snapshot!!.childrenCount!!.toString()+" beğeni")



                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })

            }
            begeniKontrolu(anlikGonderi)

            yorumlariGoster(position,anlikGonderi)

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

        fun begeniKontrolu(anlikGonderi: KullaniciKampanya) {

            var mRef = FirebaseDatabase.getInstance().reference
            var userID = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("begeniler").child(anlikGonderi.postID!!).addValueEventListener(object : ValueEventListener {


                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot!!.getValue()!=null){

                        begenmeSayisi.visibility=View.VISIBLE
                        begenmeSayisi.setText(""+snapshot!!.childrenCount!!.toString()+" beğeni")

                    }else {
                        begenmeSayisi.visibility=View.GONE
                    }

                    if (snapshot!!.hasChild(userID)) {
                    gonderiBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)
                    } else {
                      gonderiBegen.setImageResource(R.drawable.ic_launcher_like_foreground)


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }


            })




        }
        fun yorumlarFragmentiniBaslat(anlikGonderi: KullaniciKampanya) {

            if (anlikGonderi.userID!=FirebaseAuth.getInstance().currentUser!!.uid){

                Bildirimler.bildirimKaydet(anlikGonderi.userID!!,Bildirimler.YORUM_YAPILDI,anlikGonderi.postID!!)



            }


            EventBus.getDefault()
                .postSticky(EventbusData.YorumYapilacakGonderininIDsiniGonder(anlikGonderi!!.postID))

            val action=HomeFragmentDirections.actionHomeFragmentToYorumlarFragment()
            Navigation.findNavController(itemView).navigate(action)





        }

        private fun yorumlariGoster(position: Int, anlikGonderi: KullaniciKampanya) {



            var mref=FirebaseDatabase.getInstance().reference
            mref.child("yorumlar").child(anlikGonderi.postID!!).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var yorumSayisi=0

                    for (ds in snapshot!!.children){
                        if (!ds!!.key.toString().equals(anlikGonderi.postID)){
                            yorumSayisi++
                        }

                    }


                    if (yorumSayisi>=1){

                        yorumlariGoster.visibility=View.VISIBLE
                        yorumlariGoster.setText(yorumSayisi.toString()+"  yorum")


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


            var viewHolder = LayoutInflater.from(context).inflate(R.layout.recycler_row, parent, false)

            return ViewHolder(viewHolder,context)



    }


    override fun getItemCount(): Int {
        return tumKampanyalar.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(position, tumKampanyalar[position])




    }

    fun kampanyalariGuncelle(yeniKampanyaListesi:List<KullaniciKampanya>){
        tumKampanyalar.clear()
        tumKampanyalar.addAll(yeniKampanyaListesi)
        notifyDataSetChanged()
    }

}

