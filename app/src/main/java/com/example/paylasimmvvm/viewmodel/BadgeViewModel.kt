package com.example.paylasimmvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.Mesajlar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BadgeViewModel:ViewModel() {

    val badgeLive= MutableLiveData<List<Mesajlar>>()
    val badgeArray=ArrayList<Mesajlar>()
    var mref: DatabaseReference=FirebaseDatabase.getInstance().reference
    var mauth: FirebaseAuth= FirebaseAuth.getInstance()


    fun refreshBadge(){

        badgeLive.value=badgeArray
        var gorulmeyenKonusmaSayisi=0

       mref.child("konusmalar").child(mauth.currentUser!!.uid).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                badgeArray.clear()
               snapshot.children.forEach {


                  if(it.child("goruldu").getValue()?.equals(false) == true){
                      gorulmeyenKonusmaSayisi++
                      val eklenecekKonusma=snapshot.getValue(Mesajlar::class.java)
                      eklenecekKonusma!!.goruldu_sayisi=gorulmeyenKonusmaSayisi
                     badgeArray.add(eklenecekKonusma)
                      badgeLive.value=badgeArray


                  }

               }


            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }
}