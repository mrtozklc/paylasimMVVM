package com.example.paylasimmvvm.util

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.notifications.BildirimModel
import com.example.paylasimmvvm.massage.Mesajlar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BadgeViewModel:ViewModel() {

    val badgeLiveMessage= MutableLiveData<List<Mesajlar>>()
    val badgeArrayMessage=ArrayList<Mesajlar>()
    val badgeLiveNotification= MutableLiveData<List<BildirimModel>>()
    val badgeArrayNotification=ArrayList<BildirimModel>()
    val isletmeYorumlarBadgeLive=MutableLiveData<List<Int>>()
    val isletmeBadgArray=ArrayList<Int>()
    var mref: DatabaseReference=FirebaseDatabase.getInstance().reference
    private var mauth: FirebaseAuth= FirebaseAuth.getInstance()



    fun refreshMessageBadge() {

        var gorulmeyenKonusmaSayisi = 0
        mref.child("konusmalar").child(mauth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                badgeArrayMessage.clear()
                snapshot.children.forEach { dataSnapshot ->
                    val konusma = dataSnapshot.getValue(Mesajlar::class.java)
                    if (konusma != null && konusma.goruldu == false) {
                        gorulmeyenKonusmaSayisi++
                        konusma.goruldu_sayisi = gorulmeyenKonusmaSayisi
                        badgeArrayMessage.add(konusma)
                    } else if (konusma != null && konusma.goruldu == true) {
                        gorulmeyenKonusmaSayisi--
                        val konusmaIndex = badgeArrayMessage.indexOfFirst { it.son_mesaj == konusma.son_mesaj }
                        if (konusmaIndex != -1) {
                            badgeArrayMessage.removeAt(konusmaIndex)
                        }
                    }
                    badgeLiveMessage.value = badgeArrayMessage
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun refreshNotificationBadge(){

        var gorulmeyenBildirimSayisi = 0
        mref.child("bildirimler").child(mauth.currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                badgeArrayNotification.clear()
                snapshot.children.forEach { dataSnapshot ->

                    val bildirim = dataSnapshot.getValue(BildirimModel::class.java)

                    if (bildirim != null && bildirim.goruldu == false) {
                        gorulmeyenBildirimSayisi++

                        bildirim.goruldu_sayisi = gorulmeyenBildirimSayisi
                        badgeArrayNotification.add(bildirim)

                    } else if (bildirim != null && bildirim.goruldu == true) {
                        gorulmeyenBildirimSayisi--
                        val bildirimIndex = badgeArrayNotification.indexOfFirst { it.bildirim_id == bildirim.bildirim_id }

                        if (bildirimIndex != -1) {
                            badgeArrayNotification.removeAt(bildirimIndex)
                        }
                    }
                    badgeLiveNotification.value = badgeArrayNotification
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    fun refreshIsletmeYorumlarBadge(isletmeID:String){

       isletmeBadgArray.clear()


        mref.child("users").child("isletmeler").child(isletmeID).child("yorumlar").addValueEventListener(object :ValueEventListener{
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