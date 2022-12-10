package com.example.paylasimmvvm.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.paylasimmvvm.databinding.FragmentHomeBinding
import com.example.paylasimmvvm.model.KampanyaOlustur
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class kampanyalarViewModel: ViewModel() {
    lateinit var mref: DatabaseReference
    var tumPostlar=ArrayList<String>()
    var tumGonderiler=ArrayList<KullaniciKampanya>()

    val kampanyalar=MutableLiveData<List<KullaniciKampanya>>()
    val kampanyaYok=MutableLiveData<Boolean>()
    val yukleniyor=MutableLiveData<Boolean>()






    fun refreshKampanyalar(){
        yukleniyor.value=true

        mref=FirebaseDatabase.getInstance().reference

        tumGonderiler.clear()
        tumPostlar.clear()


        mref.child("kampanya").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.getValue()!=null){
                    for(ds in p0.children){
                        tumPostlar.add(ds.key!!)
                    }


                    verileriGetir()


                }
            }


        })
        if (tumGonderiler.size==0){
            kampanyalar.value= tumGonderiler
            kampanyaYok.value=true
            yukleniyor.value=false
        }



    }

    private fun verileriGetir() {

        mref=FirebaseDatabase.getInstance().reference

        for(i in 0 until tumPostlar.size){

            var kullaniciID = tumPostlar.get(i)


            Log.e("kullaniciID", "kullaniciID:" +tumPostlar.get(i))

            mref.child("users").child("isletmeler").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var userID=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_id
                    var kullaniciadi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                    var photoURL=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture
                    var isletmeLati=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.latitude
                    var isletmeLongi=snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.longitude




                    mref.child("kampanya").child(kullaniciID).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.getValue()!=null) {

                                if (snapshot!!.hasChildren()) {


                                    for (ds in snapshot!!.children) {

                                        var eklenecekUserPost = KullaniciKampanya()

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

                                        tumGonderiler.add(eklenecekUserPost)
                                        Log.e("tumgoderilerlistesi","saddasd"+tumGonderiler)

                                    }

                                }
                            }


                            if(i>=tumPostlar.size-1){

                                if (tumPostlar.size>0){
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


}