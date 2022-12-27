package com.example.paylasimmvvm.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

object Bildirimler {

    var mref= FirebaseDatabase.getInstance().reference
    private var mauth= FirebaseAuth.getInstance()
    private var muser= mauth.currentUser!!.uid

    const val KAMPANYA_BEGENILDI=1
    const val YORUM_YAPILDI=2
    const val KAMPANYA_BEGENILDI_GERI=3

    fun bildirimKaydet(bildirimYapanUserID:String,bildirimTuru:Int,gonderiID:String){

        when(bildirimTuru){

            KAMPANYA_BEGENILDI->{

                val yeniBildirimID=mref.child("bildirimler").child(bildirimYapanUserID).push().key
                val yeniBildirim=HashMap<String,Any>()
                yeniBildirim["bildirim_tur"] = KAMPANYA_BEGENILDI
                yeniBildirim["user_id"] = muser
                yeniBildirim["gonderi_id"] = gonderiID
                yeniBildirim["time"] = ServerValue.TIMESTAMP
                mref.child("bildirimler").child(bildirimYapanUserID).child(yeniBildirimID!!).setValue(yeniBildirim)


            }


            YORUM_YAPILDI->{
                val yeniBildirimID=mref.child("bildirimler").child(bildirimYapanUserID).push().key
                val yeniBildirim=HashMap<String,Any>()
                yeniBildirim["bildirim_tur"] = YORUM_YAPILDI
                yeniBildirim["user_id"] = muser
                yeniBildirim["gonderi_id"] = gonderiID
                yeniBildirim["time"] = ServerValue.TIMESTAMP
                mref.child("bildirimler").child(bildirimYapanUserID).child(yeniBildirimID!!).setValue(yeniBildirim)
            }

            KAMPANYA_BEGENILDI_GERI->{

                mref.child("bildirimler").child(bildirimYapanUserID).orderByChild("gonderi_id").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        for (bildirim in snapshot.children){

                            val okunanBildirimKey=bildirim!!.key

                            if(bildirim.child("bildirim_tur").value.toString().toInt() == KAMPANYA_BEGENILDI && bildirim.child("gonderi_id").value!! == gonderiID){
                                mref.child("bildirimler").child(bildirimYapanUserID).child(okunanBildirimKey!!).removeValue()
                                break
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


            }



        }
    }

}