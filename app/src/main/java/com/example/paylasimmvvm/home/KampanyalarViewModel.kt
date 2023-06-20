package com.example.paylasimmvvm.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.createPost.KampanyaOlustur
import com.example.paylasimmvvm.login.KullaniciBilgileri
import com.example.paylasimmvvm.createPost.KullaniciKampanya
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
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
        tumGonderiler.clear()
        tumKullaniciIDleri.clear()

        mref=FirebaseDatabase.getInstance().reference

        val user = Firebase.auth.currentUser
        if (user != null) {



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
        yukleniyor.value = true
        mref = FirebaseDatabase.getInstance().reference

        val kullaniciID = tumKullaniciIDleri.firstOrNull() ?: return

        mref.child("users").child("isletmeler").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(isletmeSnapshot: DataSnapshot) {
                if (isletmeSnapshot.exists()) {
                    val isletmeBilgileri = isletmeSnapshot.getValue(KullaniciBilgileri::class.java)
                    isletmeBilgileri?.let {
                        val userIDD = it.user_id
                        val kullaniciadi = it.user_name
                        val photoURL = it.user_detail?.profile_picture
                        val isletmeLati = it.user_detail?.latitude
                        val isletmeLongi = it.user_detail?.longitude
                        val isletmeTuru = it.user_detail?.isletme_turu
                        val muzikTuru = it.user_detail?.muzik_turu

                        mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(kampanyaSnapshot: DataSnapshot) {
                                for (ds in kampanyaSnapshot.children) {
                                    val kampanyaOlustur = ds.getValue(KampanyaOlustur::class.java)
                                    kampanyaOlustur?.let {
                                        val eklenecekUserPost = KullaniciKampanya().apply {
                                            userID = userIDD
                                            userName = kullaniciadi
                                            userPhotoURL = photoURL
                                            postID = it.post_id
                                            postURL = it.file_url
                                            postAciklama = it.aciklama
                                            postYuklenmeTarih = it.yuklenme_tarih
                                            geri_sayim = it.geri_sayim
                                            isletmeLatitude = isletmeLati
                                            isletmeLongitude = isletmeLongi
                                            isletme_turu = isletmeTuru
                                            muzik_turu = muzikTuru
                                        }
                                        tumGonderiler.add(eklenecekUserPost)
                                    }
                                }

                                tumKullaniciIDleri.removeAt(0)

                                if (tumKullaniciIDleri.isEmpty()) {
                                    if (tumGonderiler.isNotEmpty()) {
                                        kampanyalar.value = tumGonderiler
                                        kampanyaYok.value = false
                                    } else {
                                        kampanyalar.value = emptyList()
                                        kampanyaYok.value = true
                                    }

                                    yukleniyor.value = false
                                } else {
                                    verileriGetir()
                                }
                            }

                            override fun onCancelled(kampanyaError: DatabaseError) {
                                tumKullaniciIDleri.removeAt(0)

                                if (tumKullaniciIDleri.isEmpty()) {
                                    kampanyalar.value = emptyList()
                                    kampanyaYok.value = true
                                    yukleniyor.value = false
                                } else {
                                    verileriGetir()
                                }
                            }
                        })
                    }
                } else {
                    mref.child("users").child("kullanicilar").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(kullaniciSnapshot: DataSnapshot) {
                            if (kullaniciSnapshot.exists()) {
                                val kullaniciBilgileri = kullaniciSnapshot.getValue(KullaniciBilgileri::class.java)
                                kullaniciBilgileri?.let {
                                    val userIDD = it.user_id
                                    val kullaniciadi = it.user_name
                                    val photoURL = it.user_detail?.profile_picture
                                    val muzikTuru = it.user_detail?.muzik_turu

                                    mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(kampanyaSnapshot: DataSnapshot) {
                                            for (ds in kampanyaSnapshot.children) {
                                                val kampanyaOlustur = ds.getValue(KampanyaOlustur::class.java)
                                                kampanyaOlustur?.let {
                                                    val eklenecekUserPost = KullaniciKampanya().apply {
                                                        userID = userIDD
                                                        userName = kullaniciadi
                                                        userPhotoURL = photoURL
                                                        postID = it.post_id
                                                        postURL = it.file_url
                                                        postAciklama = it.aciklama
                                                        postYuklenmeTarih = it.yuklenme_tarih
                                                        geri_sayim = it.geri_sayim
                                                        muzik_turu = muzikTuru
                                                    }
                                                    tumGonderiler.add(eklenecekUserPost)
                                                }
                                            }

                                            tumKullaniciIDleri.removeAt(0)

                                            if (tumKullaniciIDleri.isEmpty()) {
                                                if (tumGonderiler.isNotEmpty()) {
                                                    kampanyalar.value = tumGonderiler
                                                    kampanyaYok.value = false
                                                } else {
                                                    kampanyalar.value = emptyList()
                                                    kampanyaYok.value = true
                                                }

                                                yukleniyor.value = false
                                            } else {
                                                verileriGetir()
                                            }
                                        }

                                        override fun onCancelled(kampanyaError: DatabaseError) {
                                            tumKullaniciIDleri.removeAt(0)

                                            if (tumKullaniciIDleri.isEmpty()) {
                                                kampanyalar.value = emptyList()
                                                kampanyaYok.value = true
                                                yukleniyor.value = false
                                            } else {
                                                verileriGetir()
                                            }
                                        }
                                    })
                                }
                            } else {
                                tumKullaniciIDleri.removeAt(0)

                                if (tumKullaniciIDleri.isEmpty()) {
                                    kampanyalar.value = emptyList()
                                    kampanyaYok.value = true
                                    yukleniyor.value = false
                                } else {
                                    verileriGetir()
                                }
                            }
                        }

                        override fun onCancelled(kullaniciError: DatabaseError) {
                            tumKullaniciIDleri.removeAt(0)

                            if (tumKullaniciIDleri.isEmpty()) {
                                kampanyalar.value = emptyList()
                                kampanyaYok.value = true
                                yukleniyor.value = false
                            } else {
                                verileriGetir()
                            }
                        }
                    })
                }
            }

            override fun onCancelled(isletmeError: DatabaseError) {
                tumKullaniciIDleri.removeAt(0)

                if (tumKullaniciIDleri.isEmpty()) {
                    kampanyalar.value = emptyList()
                    kampanyaYok.value = true
                    yukleniyor.value = false
                } else {
                    verileriGetir()
                }
            }
        })
    }

  /*  private fun verileriGetir() {
        yukleniyor.value = true
        mref = FirebaseDatabase.getInstance().reference

        for (i in 0 until tumKullaniciIDleri.size) {
            val kullaniciID = tumKullaniciIDleri[i]

            mref.child("users").child("isletmeler").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(isletmeSnapshot: DataSnapshot) {
                    if (isletmeSnapshot.value !=null){
                        val isletmeBilgileri = isletmeSnapshot.getValue(KullaniciBilgileri::class.java)
                        isletmeBilgileri?.let {
                            val userIDD = it.user_id
                            val kullaniciadi = it.user_name
                            val photoURL = it.user_detail?.profile_picture
                            val isletmeLati = it.user_detail?.latitude
                            val isletmeLongi = it.user_detail?.longitude
                            val isletmeTuru = it.user_detail?.isletme_turu
                            val muzikTuru = it.user_detail?.muzik_turu

                            mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(kampanyaSnapshot: DataSnapshot) {
                                    for (ds in kampanyaSnapshot.children) {
                                        val kampanyaOlustur = ds.getValue(KampanyaOlustur::class.java)
                                        kampanyaOlustur?.let {
                                            val eklenecekUserPost = KullaniciKampanya().apply {
                                                userID = userIDD
                                                userName = kullaniciadi
                                                userPhotoURL = photoURL
                                                postID = it.post_id
                                                postURL = it.file_url
                                                postAciklama = it.aciklama
                                                postYuklenmeTarih = it.yuklenme_tarih
                                                geri_sayim = it.geri_sayim
                                                isletmeLatitude = isletmeLati
                                                isletmeLongitude = isletmeLongi
                                                isletme_turu = isletmeTuru
                                                muzik_turu = muzikTuru
                                            }
                                            tumGonderiler.add(eklenecekUserPost)
                                        }
                                    }

                                    if (i >= tumKullaniciIDleri.size - 1) {
                                        if (tumKullaniciIDleri.size > 0) {
                                            kampanyalar.value = tumGonderiler
                                            kampanyaYok.value = false
                                            yukleniyor.value = false
                                        }
                                    }
                                }

                                override fun onCancelled(kampanyaError: DatabaseError) {
                                }
                            })
                        }
                    }else{
                        mref.child("users").child("kullanicilar").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(kullaniciSnapshot: DataSnapshot) {
                                val kullaniciBilgileri = kullaniciSnapshot.getValue(
                                    KullaniciBilgileri::class.java)
                                kullaniciBilgileri?.let {
                                    val userIDD = it.user_id
                                    val kullaniciadi = it.user_name
                                    val photoURL = it.user_detail?.profile_picture
                                    val isletmeLati = it.user_detail?.latitude
                                    val isletmeLongi = it.user_detail?.longitude
                                    val isletmeTuru = it.user_detail?.isletme_turu
                                    val muzikTuru = it.user_detail?.muzik_turu

                                    mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(kampanyaSnapshot: DataSnapshot) {
                                            for (ds in kampanyaSnapshot.children) {
                                                val kampanyaOlustur = ds.getValue(KampanyaOlustur::class.java)
                                                kampanyaOlustur?.let {
                                                    val eklenecekUserPost = KullaniciKampanya().apply {
                                                        userID = userIDD
                                                        userName = kullaniciadi
                                                        userPhotoURL = photoURL
                                                        postID = it.post_id
                                                        postURL = it.file_url
                                                        postAciklama = it.aciklama
                                                        postYuklenmeTarih = it.yuklenme_tarih
                                                        geri_sayim = it.geri_sayim
                                                        isletmeLatitude = isletmeLati
                                                        isletmeLongitude = isletmeLongi
                                                        isletme_turu = isletmeTuru
                                                        muzik_turu = muzikTuru
                                                    }
                                                    tumGonderiler.add(eklenecekUserPost)
                                                }
                                            }
                                            if (i >= tumKullaniciIDleri.size - 1) {
                                                if (tumKullaniciIDleri.size > 0) {
                                                    kampanyalar.value = tumGonderiler
                                                    kampanyaYok.value = false
                                                    yukleniyor.value = false
                                                }
                                            }
                                        }
                                        override fun onCancelled(kampanyaError: DatabaseError) {
                                        }
                                    })
                                }
                            }
                            override fun onCancelled(kullaniciError: DatabaseError) {
                            }
                        })

                    }

                }

                override fun onCancelled(isletmeError: DatabaseError) {
                }
            })

        }

    }*/

    fun getIsletmeList(){

        yukleniyor.value=true
        tumGonderiler.clear()


        mref=FirebaseDatabase.getInstance().reference
        mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.value !=null) {

                    if (snapshot.hasChildren()) {

                        for (ds in snapshot.children) {
                            ds.key?.let {
                                mref.child("mudavimler").child(it).addListenerForSingleValueEvent(object :ValueEventListener{
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val mudavimSayisi=snapshot.childrenCount.toInt()
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
                                        eklenecekUserPost.mudavim_sayisi=mudavimSayisi

                                        tumGonderiler.add(eklenecekUserPost)

                                        kampanyaYok.value=false
                                        yukleniyor.value=true
                                        kampanyalar.value= tumGonderiler
                                        yukleniyor.value=false
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                    }

                                })
                            }





                        }

                    }
                }



            }
            override fun onCancelled(error: DatabaseError) {}

        })


    }


}