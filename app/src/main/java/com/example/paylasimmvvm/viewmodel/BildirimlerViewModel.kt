package com.example.paylasimmvvm.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.BildirimModel
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
        bildirimYok.value=true
        yukleniyor.value=true


        mref.child("bildirimler").child(mauth.currentUser!!.uid).orderByChild("time").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.getValue()!=null){



                    for (ds in snapshot.children){
                        var okunanBildirim=ds.getValue(BildirimModel::class.java)


                        bildirimlerArray.add(okunanBildirim!!)
                    }
                   // recyclerAdapter()
                    tumBildirimlerLive.value=bildirimlerArray


                }else{
                  // binding. progressBarBildirim.visibility= View.GONE
                   // binding.recyclerBildirim.visibility= View.VISIBLE
                    tumBildirimlerLive.value=bildirimlerArray
                    yukleniyor.value=false

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

        if (bildirimlerArray.size==0){


            mref.child("bildirimler").child(mauth.currentUser!!.uid).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.getValue()==null){
                        bildirimYok.value=true

                      //  binding.recyclerBildirim.visibility= View.GONE
                      //  binding.bildirimYok.visibility= View.VISIBLE

                    }
                    else{
                        bildirimYok.value=false
                        yukleniyor.value=false
                        tumBildirimlerLive.value=bildirimlerArray

                      //  binding.recyclerBildirim.visibility= View.VISIBLE
                       // binding.bildirimYok.visibility= View.GONE

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }

        tumBildirimlerLive.value=bildirimlerArray

    }



}