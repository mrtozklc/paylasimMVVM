package com.example.paylasimmvvm.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.Mesajlar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BadgeViewModel:ViewModel() {

    val badgeLive= MutableLiveData<List<Mesajlar>>()
    val badgeArray=ArrayList<Mesajlar>()
    val isletmeYorumlarBadgeLive=MutableLiveData<List<Int>>()
    val isletmeBadgArray=ArrayList<Int>()
    var mref: DatabaseReference=FirebaseDatabase.getInstance().reference
    private var mauth: FirebaseAuth= FirebaseAuth.getInstance()



    fun refreshBadge() {

        var gorulmeyenKonusmaSayisi = 0
        mref.child("konusmalar").child(mauth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                badgeArray.clear()
                snapshot.children.forEach { dataSnapshot ->
                    val konusma = dataSnapshot.getValue(Mesajlar::class.java)
                    if (konusma != null && konusma.goruldu == false) {
                        gorulmeyenKonusmaSayisi++
                        konusma.goruldu_sayisi = gorulmeyenKonusmaSayisi
                        badgeArray.add(konusma)
                    } else if (konusma != null && konusma.goruldu == true) {
                        gorulmeyenKonusmaSayisi--
                        val konusmaIndex = badgeArray.indexOfFirst { it.son_mesaj == konusma.son_mesaj }
                        if (konusmaIndex != -1) {
                            badgeArray.removeAt(konusmaIndex)
                        }
                    }
                    badgeLive.value = badgeArray
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun refreshIsletmeYorumlarBadge(isletmeID:String){

       isletmeBadgArray.clear()


        mref.child("yorumlar").child(isletmeID).addValueEventListener(object :ValueEventListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {

              val yorumSayisi= snapshot.childrenCount.toInt()


                isletmeBadgArray.add(yorumSayisi)


                isletmeYorumlarBadgeLive.value=isletmeBadgArray

            }

            override fun onCancelled(error: DatabaseError) {
            }

        })

    }


}