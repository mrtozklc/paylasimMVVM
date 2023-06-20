package com.example.paylasimmvvm.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class BildirimlerViewModel:ViewModel() {
    val tumBildirimlerLive= MutableLiveData<List<BildirimModel>>()
    val bildirimlerArray=ArrayList<BildirimModel>()
    val bildirimYok=MutableLiveData<Boolean>()
    val yukleniyor=MutableLiveData<Boolean>()
    lateinit var mref: DatabaseReference
    lateinit var mauth: FirebaseAuth

    fun refreshBildirimler(){
        bildirimlerArray.clear()
        mref= FirebaseDatabase.getInstance().reference
        mauth=Firebase.auth
        yukleniyor.value=true


        mref.child("bildirimler").child(mauth.currentUser!!.uid).orderByChild("time").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.getValue()!=null){

                    for (ds in snapshot.children){

                        val okunanBildirim=ds.getValue(BildirimModel::class.java)
                        okunanBildirim?.bildirim_id = ds.key

                        bildirimlerArray.add(okunanBildirim!!)
                    }
                    tumBildirimlerLive.value=bildirimlerArray
                    yukleniyor.value=false


                }else{

                    bildirimYok.value=true
                    yukleniyor.value=false

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })





    }



}