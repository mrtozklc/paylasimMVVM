package com.example.paylasimmvvm.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class ChatViewModel:ViewModel() {
    val chatMutable = MutableLiveData<List<ChatModel>>()
    val chatArray = ArrayList<ChatModel>()
    val yukleniyor = MutableLiveData<Boolean>()
    val gosterilecekMesajSayisi=10
    var sayfaSayisi=1
    var mesajPosition=0
    var refreshMesajPosition=0
    var getirilenMesajId=""
    var zatenListedeOlanMesajID=""
    lateinit var mref: DatabaseReference

    fun refreshChat(sohbetEdilcekKisi:String) {
        mref = FirebaseDatabase.getInstance().reference
        var mesajGonderenId=FirebaseAuth.getInstance().currentUser!!.uid


        chatMutable.value = chatArray
        yukleniyor.value = false


        mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).limitToLast(gosterilecekMesajSayisi).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var okunanMesaj=snapshot.getValue(ChatModel::class.java)
                chatArray.add(okunanMesaj!! as ChatModel)

                if (mesajPosition==0){

                    getirilenMesajId= snapshot!!.key!!
                    zatenListedeOlanMesajID=snapshot!!.key!!

                }
                mesajPosition++


                var mesajGonderenId=FirebaseAuth.getInstance().currentUser!!.uid
                snapshot!!.key?.let {
                    FirebaseDatabase.getInstance().getReference()
                        .child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).child(it)
                        .child("goruldu").setValue(true)
                        .addOnCompleteListener {
                            FirebaseDatabase.getInstance().getReference().child("konusmalar")

                                .child(mesajGonderenId).child(sohbetEdilcekKisi).child("goruldu").setValue(true)
                        }
                }





                chatMutable.value = chatArray
                yukleniyor.value = false
                Log.e("getirilenmesajid",""+getirilenMesajId)


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }


        })
    }

}