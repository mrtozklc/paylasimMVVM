package com.example.paylasimmvvm.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object Bildirimler {

    private var mref = FirebaseDatabase.getInstance().reference

    const val KAMPANYA_BEGENILDI = 1
    const val YORUM_YAPILDI = 2
    const val YORUM_BEGENILDI = 3
    const val YORUM_YAPILDI_ISLETME = 4

    private const val MAX_BILDIRIM_SAYISI = 20

    fun bildirimKaydet(
        bildirimYapanUserID: String,
        bildirimTuru: Int,
        gonderiID: String,
        postURL: String,
        yorum: String? = null,
        yorumKey: String? = null,
        gonderiSahibi:String?=null
    ) {

        val bildirimlerRef = mref.child("bildirimler").child(bildirimYapanUserID)

        bildirimlerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val bildirimSayisi = dataSnapshot.childrenCount.toInt()

                if (bildirimSayisi >= MAX_BILDIRIM_SAYISI) {
                    val silinecekBildirimSayisi = bildirimSayisi - MAX_BILDIRIM_SAYISI + 1
                    val eskiBildirimler = mutableListOf<DataSnapshot>()

                    for (bildirimSnapshot in dataSnapshot.children) {
                        val id=bildirimSnapshot.children.toString()
                        eskiBildirimler.add(bildirimSnapshot)
                        if (eskiBildirimler.size >= silinecekBildirimSayisi) {
                            break
                        }
                    }

                    for (eskiBildirimSnapshot in eskiBildirimler) {
                        eskiBildirimSnapshot.ref.removeValue()
                    }
                }

                kaydetYeniBildirim(bildirimYapanUserID, bildirimTuru, gonderiID, postURL, yorum, yorumKey, gonderiSahibi!!)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Bildirimler", "Bildirimleri getirme hatası: ${databaseError.message}")
            }
        })
    }

    private fun kaydetYeniBildirim(
        bildirimYapanUserID: String,
        bildirimTuru: Int,
        gonderiID: String,
        postURL: String,
        yorum: String?,
        yorumKey: String?,
        gonderiSahibi:String
    ) {
        val bildirimlerRef= mref.child("bildirimler").child(bildirimYapanUserID)
        val yeniBildirimID = bildirimlerRef.push().key.toString()
        val yeniBildirim = HashMap<String, Any>()
        yeniBildirim["bildirim_tur"] = bildirimTuru
        yeniBildirim["bildirim_yapan_id"] = bildirimYapanUserID
        yeniBildirim["gonderi_sahibi_id"] =gonderiSahibi
        yeniBildirim["post_url"] = postURL
        yeniBildirim["gonderi_id"] = gonderiID
        yeniBildirim["goruldu"] = false
        yeniBildirim["time"] = ServerValue.TIMESTAMP
        yeniBildirim["bildirim_id"] = yeniBildirimID
        yorum?.let { yeniBildirim["yorum"] = it }
        yorumKey?.let { yeniBildirim["yorum_key"] = it }
        bildirimlerRef.child(yeniBildirimID!!).setValue(yeniBildirim)
    }
}