package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.model.KampanyaOlustur
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.EventbusData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.greenrobot.eventbus.EventBus

class ProfilViewModel:ViewModel() {
    val profilKampanya=MutableLiveData<List<KullaniciKampanya>>()
    val kampanyalarArray=ArrayList<KullaniciKampanya>()
    lateinit var mref: DatabaseReference
    lateinit var muser: FirebaseUser

    fun refreshProfilKampanya(){
        mref= FirebaseDatabase.getInstance().reference
        muser= FirebaseAuth.getInstance().currentUser!!
        kampanyalarArray.clear()


            mref.child("users").child("isletmeler").child(muser.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                 //   var userID=kullanicid
                    var kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                    var photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture

                    mref.child("kampanya").child(muser!!.uid).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot!!.hasChildren()){

                                for (ds in snapshot!!.children){
                                    var eklenecekUserPost= KullaniciKampanya()
                                 //   eklenecekUserPost.userID=userID
                                    eklenecekUserPost.userName=kullaniciadi
                                    eklenecekUserPost.userPhotoURL=photoURL
                                    eklenecekUserPost.postID=ds.getValue(KampanyaOlustur::class.java)!!.post_id
                                    eklenecekUserPost.postURL=ds.getValue(KampanyaOlustur::class.java)!!.file_url
                                    eklenecekUserPost.postAciklama=ds.getValue(KampanyaOlustur::class.java)!!.aciklama
                                    eklenecekUserPost.postYuklenmeTarih=ds.getValue(KampanyaOlustur::class.java)!!.yuklenme_tarih

                                    kampanyalarArray.add(eklenecekUserPost)

                                    Log.e("tomprofil","sayisi:"+kampanyalarArray.size)


                                }

                            }

                            profilKampanya.value=kampanyalarArray
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }


                    })

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

            mref.child("users").child("kullanicilar").child(muser!!.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                    if (snapshot!!.getValue()!=null){

                      //  var userID=kullanicid
                        var kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                        var photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture

                        mref.child("kampanya").child(muser!!.uid).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot!!.hasChildren()){

                                    for (ds in snapshot!!.children){
                                        var eklenecekUserPost= KullaniciKampanya()
                                       // eklenecekUserPost.userID=userID
                                        eklenecekUserPost.userName=kullaniciadi
                                        eklenecekUserPost.userPhotoURL=photoURL
                                        eklenecekUserPost.postID=ds.getValue(KampanyaOlustur::class.java)!!.post_id
                                        eklenecekUserPost.postURL=ds.getValue(KampanyaOlustur::class.java)!!.file_url
                                        eklenecekUserPost.postAciklama=ds.getValue(KampanyaOlustur::class.java)!!.aciklama
                                        eklenecekUserPost.postYuklenmeTarih=ds.getValue(KampanyaOlustur::class.java)!!.yuklenme_tarih

                                        kampanyalarArray.add(eklenecekUserPost)







                                    }

                                }

                                profilKampanya.value=kampanyalarArray

                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })

                    }



                }

                override fun onCancelled(error: DatabaseError) {
                }

            })







        profilKampanya.value=kampanyalarArray
    }
}