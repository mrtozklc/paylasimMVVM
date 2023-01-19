package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.KampanyaOlustur
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.model.Menuler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class ProfilViewModel:ViewModel() {
    val profilKampanya=MutableLiveData<List<KullaniciKampanya>>()
    val kampanyalarArray=ArrayList<KullaniciKampanya>()
    lateinit var mref: DatabaseReference
    val profilMenu=MutableLiveData<List<Menuler>>()
    val menuArray=ArrayList<Menuler>()
    val yukleniyor=MutableLiveData<Boolean>()
    val gonderiYok=MutableLiveData<Boolean>()


    fun refreshProfilKampanya(secilenUser:String){
        mref= FirebaseDatabase.getInstance().reference

        val user = Firebase.auth.currentUser
        if (user != null) {
            kampanyalarArray.clear()
            mref.child("users").child("isletmeler").child(secilenUser).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.value !=null){
                        val userID=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_id
                        val kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                  //      val photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture

                        mref.child("kampanya").child(secilenUser).addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.value !=null){
                                    if (snapshot.hasChildren()){

                                        for (ds in snapshot.children){
                                            val eklenecekUserPost= KullaniciKampanya()
                                            eklenecekUserPost.userID=userID
                                            eklenecekUserPost.userName=kullaniciadi
                                         //   eklenecekUserPost.userPhotoURL=photoURL
                                            eklenecekUserPost.postID=ds.getValue(KampanyaOlustur::class.java)!!.post_id
                                            eklenecekUserPost.postURL=ds.getValue(KampanyaOlustur::class.java)!!.file_url
                                            eklenecekUserPost.postAciklama=ds.getValue(
                                                KampanyaOlustur::class.java)!!.aciklama
                                            eklenecekUserPost.postYuklenmeTarih=ds.getValue(
                                                KampanyaOlustur::class.java)!!.yuklenme_tarih

                                            kampanyalarArray.add(eklenecekUserPost)
                                        }
                                    }
                                    profilKampanya.value=kampanyalarArray
                                    gonderiYok.value=false
                                    Log.e("gelenarray",""+kampanyalarArray)
                                }else{
                                    gonderiYok.value=true
                                }

                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

            mref.child("users").child("kullanicilar").child(FirebaseAuth.getInstance().currentUser!!.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.value !=null){


                        val kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                        val photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture

                        mref.child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.hasChildren()){

                                    for (ds in snapshot.children){
                                        val eklenecekUserPost= KullaniciKampanya()
                                        eklenecekUserPost.userName=kullaniciadi
                                        eklenecekUserPost.userPhotoURL=photoURL
                                        eklenecekUserPost.postID=ds.getValue(KampanyaOlustur::class.java)!!.post_id
                                        eklenecekUserPost.postURL=ds.getValue(KampanyaOlustur::class.java)!!.file_url
                                        eklenecekUserPost.postAciklama=ds.getValue(KampanyaOlustur::class.java)!!.aciklama
                                        eklenecekUserPost.postYuklenmeTarih=ds.getValue(
                                            KampanyaOlustur::class.java)!!.yuklenme_tarih

                                        kampanyalarArray.add(eklenecekUserPost)

                                    }
                                    profilKampanya.value=kampanyalarArray
                                    gonderiYok.value=false
                                    yukleniyor.value=false

                                }
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
            yukleniyor.value=false
        }
    }

    fun getMenus(secilenUser:String){
        yukleniyor.value=true
        menuArray.clear()

        mref.child("menuler").child(secilenUser) .addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuArray.clear()

                if (snapshot.value !=null){

                    for (ds in snapshot.children){



                       val menus=ds.getValue(Menuler::class.java)!!.menuler

                         val eklenecekMenu=Menuler()

                         eklenecekMenu.menuler=menus


                        menuArray.add(eklenecekMenu)
                        profilMenu.value=menuArray
                        Log.e("gelen menu array","${(profilMenu.value as ArrayList<Menuler>).size}")

                    }

                    profilMenu.value=menuArray
                    yukleniyor.value=false

                }else{

                    gonderiYok.value=true
                    yukleniyor.value=false
                }



            }

            override fun onCancelled(error: DatabaseError) {
            }

        })



    }
}