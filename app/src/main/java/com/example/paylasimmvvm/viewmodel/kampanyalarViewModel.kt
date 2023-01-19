package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.KampanyaOlustur
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class kampanyalarViewModel: ViewModel() {
    lateinit var mref: DatabaseReference
    var tumKullaniciIDleri=ArrayList<String>()
    var tumGonderiler=ArrayList<KullaniciKampanya>()

    val kampanyalar=MutableLiveData<List<KullaniciKampanya>>()
    val kampanyaYok=MutableLiveData<Boolean>()
    val yukleniyor=MutableLiveData<Boolean>()




    fun refreshKampanyalar(){

        yukleniyor.value=true

        mref=FirebaseDatabase.getInstance().reference

        val user = Firebase.auth.currentUser
        if (user != null) {
            tumGonderiler.clear()
            tumKullaniciIDleri.clear()


            mref.child("kampanya").addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.value !=null){
                        for(ds in p0.children){
                            tumKullaniciIDleri.add(ds.key!!)
                        }


                        verileriGetir()


                    }
                }


            })
            if (tumGonderiler.size==0){

                mref.child("kampanya").addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if(p0.value ==null){
                            kampanyaYok.value=true
                            yukleniyor.value=false

                        }else{
                            kampanyalar.value=tumGonderiler
                            kampanyaYok.value=false



                        }
                    }


                })


            }


        }


    }

    private fun verileriGetir() {

        yukleniyor.value=true

        mref=FirebaseDatabase.getInstance().reference

        for(i in 0 until tumKullaniciIDleri.size){

            val kullaniciID = tumKullaniciIDleri.get(i)



            mref.child("users").child("isletmeler").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val userID=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_id
                    val kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                    val photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture
                    val isletmeLati=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.latitude
                    val isletmeLongi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.longitude
                    val isletmeTuru=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.isletme_turu
                    val muzikTuru=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.muzik_turu




                    mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.value !=null) {

                                if (snapshot.hasChildren()) {

                                    for (ds in snapshot.children) {

                                        val eklenecekUserPost = KullaniciKampanya()

                                        eklenecekUserPost.userID = userID
                                        eklenecekUserPost.userName = kullaniciadi
                                        eklenecekUserPost.userPhotoURL = photoURL
                                        eklenecekUserPost.postID = ds.getValue(KampanyaOlustur::class.java)!!.post_id
                                        eklenecekUserPost.postURL = ds.getValue(KampanyaOlustur::class.java)!!.file_url
                                        eklenecekUserPost.postAciklama = ds.getValue(KampanyaOlustur::class.java)!!.aciklama
                                        eklenecekUserPost.postYuklenmeTarih = ds.getValue(KampanyaOlustur::class.java)!!.yuklenme_tarih
                                        eklenecekUserPost.geri_sayim = ds.getValue(KampanyaOlustur::class.java)!!.geri_sayim
                                        eklenecekUserPost.isletmeLatitude = isletmeLati
                                        eklenecekUserPost.isletmeLongitude = isletmeLongi
                                        eklenecekUserPost.isletme_turu=isletmeTuru
                                        eklenecekUserPost.muzik_turu=muzikTuru

                                        tumGonderiler.add(eklenecekUserPost)


                                        kampanyaYok.value=false
                                        yukleniyor.value=true
                                        kampanyalar.value= tumGonderiler
                                        yukleniyor.value=false
                                    }
                                }
                            }

                            if(i>=tumKullaniciIDleri.size-1){

                                if (tumKullaniciIDleri.size>0){
                                    kampanyalar.value= tumGonderiler
                                    kampanyaYok.value=false
                                    yukleniyor.value=false
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })

                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    fun getIsletmeList(){

        yukleniyor.value=true
        tumGonderiler.clear()

        


        mref=FirebaseDatabase.getInstance().reference
        mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.value !=null) {

                    if (snapshot.hasChildren()) {

                        for (ds in snapshot.children) {
                            Log.e("dfor","$ds")
                            val userID=ds.getValue(KullaniciBilgileri::class.java)!!.user_id
                            val kullaniciadi=ds.getValue(KullaniciBilgileri::class.java)!!.user_name
                            val photoURL=ds.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture
                            val isletmeLati=ds.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.latitude
                            val isletmeLongi=ds.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.longitude
                            val isletmeTuru=ds.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.isletme_turu
                            val muzikTuru=ds.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.muzik_turu


                            val eklenecekUserPost = KullaniciKampanya()

                            eklenecekUserPost.userID = userID
                            eklenecekUserPost.userName = kullaniciadi
                           eklenecekUserPost.userPhotoURL = photoURL
                            eklenecekUserPost.isletmeLatitude = isletmeLati
                            eklenecekUserPost.isletmeLongitude = isletmeLongi
                            eklenecekUserPost.isletme_turu=isletmeTuru
                            eklenecekUserPost.muzik_turu=muzikTuru

                            tumGonderiler.add(eklenecekUserPost)

                            kampanyaYok.value=false
                            yukleniyor.value=true
                            kampanyalar.value= tumGonderiler
                            yukleniyor.value=false


                        }

                    }
                }



            }
            override fun onCancelled(error: DatabaseError) {}

        })


    }


}