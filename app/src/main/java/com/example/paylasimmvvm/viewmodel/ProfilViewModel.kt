package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.*
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
    val menuSayisiMutable=MutableLiveData<List<Int>>()
    val menuSayisiArray=ArrayList<Int>()
    val mudavimlerMutableLiveData=MutableLiveData<List<Mudavimler>>()
    val mudavimlerArray=ArrayList<Mudavimler>()
    val mudavimSayisiMutableLiveData=MutableLiveData<List<Int>>()
    val mudavimSayisiArray=ArrayList<Int>()
    val yukleniyor=MutableLiveData<Boolean>()
    val gonderiYok=MutableLiveData<Boolean>()
    val mudavimYok=MutableLiveData<Boolean>()


    fun refreshProfilKampanya(secilenUser:String){
        yukleniyor.value=true
        mref= FirebaseDatabase.getInstance().reference

        val user = Firebase.auth.currentUser
        if (user != null) {
            kampanyalarArray.clear()
            mref.child("users").child("isletmeler").child(secilenUser).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.value !=null){
                        val userID=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_id
                        val kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                        val photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture

                        mref.child("kampanya").child(secilenUser).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.value !=null){
                                    if (snapshot.hasChildren()){

                                        for (ds in snapshot.children){
                                            val eklenecekUserPost= KullaniciKampanya()
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
                                        }
                                    }

                                    profilKampanya.value=kampanyalarArray
                                    gonderiYok.value=false
                                    yukleniyor.value=false
                                }else{
                                    yukleniyor.value=false
                                    gonderiYok.value=true

                                }

                            }
                            override fun onCancelled(error: DatabaseError) {
                            }
                        })
                    }else{
                        mref.child("users").child("kullanicilar").child(FirebaseAuth.getInstance().currentUser!!.uid).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {

                                if (snapshot.value !=null){


                                    val kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                                    val photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture

                                    mref.child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid).addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value !=null){
                                                if (snapshot.hasChildren()){

                                                    for (ds in snapshot.children){
                                                        val eklenecekUserPost= KullaniciKampanya()
                                                        eklenecekUserPost.userName=kullaniciadi
                                                        eklenecekUserPost.userPhotoURL=photoURL
                                                        eklenecekUserPost.postID=ds.getValue(KampanyaOlustur::class.java)!!.post_id
                                                        eklenecekUserPost.postURL=ds.getValue(KampanyaOlustur::class.java)!!.file_url
                                                        eklenecekUserPost.postAciklama=ds.getValue(KampanyaOlustur::class.java)!!.aciklama
                                                        eklenecekUserPost.postYuklenmeTarih=ds.getValue(KampanyaOlustur::class.java)!!.yuklenme_tarih

                                                        kampanyalarArray.add(eklenecekUserPost)

                                                    }
                                                    profilKampanya.value=kampanyalarArray
                                                    gonderiYok.value=false
                                                    yukleniyor.value=false

                                                }

                                            }
                                            yukleniyor.value=false
                                            gonderiYok.value=true


                                        }


                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })

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



        }
    }

    fun getMenus(secilenUser:String){
        yukleniyor.value=true
        menuArray.clear()
        menuSayisiArray.clear()
        mref= FirebaseDatabase.getInstance().reference

        mref.child("menuler").child(secilenUser) .addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuArray.clear()

                menuSayisiArray.add(snapshot.childrenCount.toInt())
                menuSayisiMutable.value=menuSayisiArray

                if (snapshot.value !=null){

                    for (ds in snapshot.children){

                       val menus=ds.getValue(Menuler::class.java)!!.menuler

                         val eklenecekMenu=Menuler()

                         eklenecekMenu.menuler=menus

                        menuArray.add(eklenecekMenu)


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

    fun getMudavimSayisi(secilenUser: String){
        mudavimlerArray.clear()
        mudavimSayisiArray.clear()
        mref= FirebaseDatabase.getInstance().reference
        mref.child("mudavimler").child(secilenUser).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mudavimSayisiArray.clear()

                mudavimSayisiArray.add(snapshot.childrenCount.toInt())
                mudavimSayisiMutableLiveData.value = mudavimSayisiArray
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    fun getMudavimler(secilenUser:String) {
        yukleniyor.value=true
        mudavimlerArray.clear()
        mudavimSayisiArray.clear()
        mref= FirebaseDatabase.getInstance().reference



     mref.child("mudavimler").child(secilenUser).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                    mudavimSayisiArray.clear()

                    mudavimSayisiArray.add(snapshot.childrenCount.toInt())
                    mudavimSayisiMutableLiveData.value = mudavimSayisiArray

                if (snapshot.childrenCount.toInt()>0) {
                    for (ds in snapshot.children) {
                        val mudavimID = ds.getValue(Mudavimler::class.java)!!.mudavim_id
                        if (mudavimID != null) {
                            mref.child("users")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {

                                        for (userTypeSnapshot in dataSnapshot.children) {
                                            for (userSnapshot in userTypeSnapshot.children) {
                                                val userID = userSnapshot.key
                                                if (userID == mudavimID) {
                                                    val kullaniciadi =
                                                        userSnapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                                                    val photoURL =
                                                        userSnapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture
                                                    val eklenecekMudavim = Mudavimler()
                                                    eklenecekMudavim.mudavim_id = userID
                                                    eklenecekMudavim.mudavim_photo = photoURL
                                                    eklenecekMudavim.mudavim_userName = kullaniciadi

                                                    mudavimlerArray.add(eklenecekMudavim)

                                                }

                                            }
                                        }

                                        mudavimlerMutableLiveData.value = mudavimlerArray
                                        yukleniyor.value = false

                                    }

                                    override fun onCancelled(error: DatabaseError) {}
                                })
                        }

                    }

                }else{

                    mudavimYok.value=true
                    yukleniyor.value=false

                }
            }
            override fun onCancelled(error: DatabaseError) { }
        })


    }


}