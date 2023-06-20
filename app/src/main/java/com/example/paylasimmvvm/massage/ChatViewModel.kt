package com.example.paylasimmvvm.massage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    var sohbetEdilcekKisi: String = ""
    lateinit var mref: DatabaseReference
    var childEventListener: ChildEventListener? = null
    val mesajGonderenId=FirebaseAuth.getInstance().currentUser!!.uid
    private var isChildEventListenerActive = false

    fun refreshChat(sohbetEdilcekKisi:String) {

        mref = FirebaseDatabase.getInstance().reference



        chatMutable.value = chatArray
        yukleniyor.value = false


      childEventListener=  mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).limitToLast(gosterilecekMesajSayisi).addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if (isChildEventListenerActive) {
                    val okunanMesaj=snapshot.getValue(ChatModel::class.java)
                    chatArray.add(okunanMesaj!!)

                    if (mesajPosition==0){

                        getirilenMesajId= snapshot.key!!
                        zatenListedeOlanMesajID= snapshot.key!!

                    }
                    mesajPosition++



                    snapshot.key?.let { it ->

                        FirebaseDatabase.getInstance().reference
                            .child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).child(snapshot.key!!)
                            .child("goruldu").setValue(true)
                            .addOnCompleteListener {

                                FirebaseDatabase.getInstance().reference.child("konusmalar")
                                    .child(mesajGonderenId).child(sohbetEdilcekKisi).child("goruldu").setValue(true)

                            }

                    }

                    chatMutable.value = chatArray
                    yukleniyor.value = false
                }



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



    fun addListeners() {
        isChildEventListenerActive = true

    }
    fun removeListener() {
        isChildEventListenerActive = false
    }





}