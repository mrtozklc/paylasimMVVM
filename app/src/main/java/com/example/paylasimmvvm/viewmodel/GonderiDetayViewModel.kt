package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.KampanyaOlustur
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class GonderiDetayViewModel:ViewModel() {
    lateinit var mref: DatabaseReference
    var gonderiArray=ArrayList<KullaniciKampanya>()

    val gonderiMutable= MutableLiveData<List<KullaniciKampanya>>()
    val kampanyaYok= MutableLiveData<Boolean>()
    val yukleniyor= MutableLiveData<Boolean>()


    fun getGonderiDetayi(userID:String,gonderiID: String) {
        yukleniyor.value = true
        mref = FirebaseDatabase.getInstance().reference

        val user = Firebase.auth.currentUser
        if (user != null) {
            gonderiArray.clear()
            mref.child("users").child("isletmeler").child(userID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val userID=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_id
                    val kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                    val photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture
                    val isletmeLati=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.latitude
                    val isletmeLongi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.longitude
                    val isletmeTuru=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.isletme_turu
                    val muzikTuru=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.muzik_turu




                    mref.child("kampanya").child(userID!!).child(gonderiID).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val eklenecekUserPost = KullaniciKampanya()
                                eklenecekUserPost.userID = userID
                                eklenecekUserPost.userName = kullaniciadi
                                eklenecekUserPost.userPhotoURL = photoURL

                                // Access the values under the gonderiID child node
                                eklenecekUserPost.postID = gonderiID
                                eklenecekUserPost.postURL = snapshot.child("file_url").getValue(String::class.java)
                                eklenecekUserPost.postAciklama = snapshot.child("aciklama").getValue(String::class.java)
                                eklenecekUserPost.postYuklenmeTarih = snapshot.child("yuklenme_tarih").getValue(Long::class.java)
                                eklenecekUserPost.geri_sayim =
                                    snapshot.child("geri_sayim").getValue(Long::class.java).toString()
                                eklenecekUserPost.isletmeLatitude = isletmeLati
                                eklenecekUserPost.isletmeLongitude = isletmeLongi
                                eklenecekUserPost.isletme_turu = isletmeTuru
                                eklenecekUserPost.muzik_turu = muzikTuru

                                gonderiArray.add(eklenecekUserPost)

                                gonderiMutable.value = gonderiArray
                                kampanyaYok.value = false
                                yukleniyor.value = false
                            } else {

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
}