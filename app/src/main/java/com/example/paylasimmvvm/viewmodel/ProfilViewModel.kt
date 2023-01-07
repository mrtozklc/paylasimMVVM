package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.model.*
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.view.profil.ProfilFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus

class ProfilViewModel:ViewModel() {
    val profilKampanya=MutableLiveData<List<KullaniciKampanya>>()
    val kampanyalarArray=ArrayList<KullaniciKampanya>()
    lateinit var mref: DatabaseReference
    val profilMenu=MutableLiveData<List<Menuler>>()
    val menuArray=ArrayList<Menuler>()

    fun refreshProfilKampanya(secilenUser:String){
        mref= FirebaseDatabase.getInstance().reference


        val user = Firebase.auth.currentUser
        if (user != null) {

            kampanyalarArray.clear()


            mref.child("users").child("isletmeler").child(secilenUser).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {



                    if (snapshot.getValue()!=null){
                        var userID=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_id
                        //   var userID=kullanicid
                        var kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                        var photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture

                        mref.child("kampanya").child(secilenUser).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.getValue()!=null){
                                    if (snapshot!!.hasChildren()){

                                        for (ds in snapshot!!.children){
                                            var eklenecekUserPost= KullaniciKampanya()
                                            eklenecekUserPost.userID=userID
                                            eklenecekUserPost.userName=kullaniciadi
                                            eklenecekUserPost.userPhotoURL=photoURL
                                            eklenecekUserPost.postID=ds.getValue(KampanyaOlustur::class.java)!!.post_id
                                            eklenecekUserPost.postURL=ds.getValue(KampanyaOlustur::class.java)!!.file_url
                                            eklenecekUserPost.postAciklama=ds.getValue(
                                                KampanyaOlustur::class.java)!!.aciklama
                                            eklenecekUserPost.postYuklenmeTarih=ds.getValue(
                                                KampanyaOlustur::class.java)!!.yuklenme_tarih

                                            kampanyalarArray.add(eklenecekUserPost)

                                            Log.e("tomprofil","sayisi:"+kampanyalarArray.size)


                                        }

                                    }

                                    profilKampanya.value=kampanyalarArray

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


                    if (snapshot!!.getValue()!=null){

                        //  var userID=kullanicid
                        var kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                        var photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture

                        mref.child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid).addListenerForSingleValueEvent(object :
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
                                        eklenecekUserPost.postYuklenmeTarih=ds.getValue(
                                            KampanyaOlustur::class.java)!!.yuklenme_tarih

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

    fun getMenus(secilenUser:String){
        menuArray.clear()

        mref.child("menuler").child(secilenUser) .addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {



                if (snapshot.getValue()!=null){

                    for (ds in snapshot.children){



                       var menus=ds.getValue(Menuler::class.java)!!.menuler

                         var eklenecekMenu=Menuler()

                         eklenecekMenu.menuler=menus
                        menuArray.add(eklenecekMenu)


                        profilMenu.value=menuArray
                        Log.e("gelen array","${menuArray.size}")




                    }

                    profilMenu.value=menuArray

                }



            }

            override fun onCancelled(error: DatabaseError) {
            }

        })



    }
}