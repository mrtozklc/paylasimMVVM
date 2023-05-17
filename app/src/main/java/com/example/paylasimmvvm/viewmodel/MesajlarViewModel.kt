package com.example.paylasimmvvm.viewmodel
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.Mesajlar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MesajlarViewModel:ViewModel() {
    val mesajlarMutable= MutableLiveData<List<Mesajlar>?>()
    val mesajlarArray=ArrayList<Mesajlar>()
    val yukleniyor=MutableLiveData<Boolean>()
    val mesajYok=MutableLiveData<Boolean>()
    lateinit var auth : FirebaseAuth
    lateinit var mref: DatabaseReference
    lateinit var mListener:ChildEventListener

    fun refreshMesajlar(){
        mesajlarArray.clear()

        auth=  FirebaseAuth.getInstance()
        mref = FirebaseDatabase.getInstance().reference

        mesajlarMutable.value=mesajlarArray
        yukleniyor.value=false

        if (mesajlarArray.size==0){
                    mref.child("konusmalar").child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value ==null){
                                mesajYok.value = true

                            }

                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                }

        mListener=  mref.child("konusmalar").child(auth.currentUser!!.uid).orderByChild("gonderilmeZamani").addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {


                mesajYok.value=false
                val eklenecekKonusma=snapshot.getValue(Mesajlar::class.java)
                eklenecekKonusma!!.user_id=snapshot.key
                Log.e("mesajlaradded calıstı","")
                mesajlarArray.add(0, eklenecekKonusma)
                mesajlarMutable.value=mesajlarArray
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val kontrol =konusmaPositionBul(snapshot.key.toString())
                if(kontrol != -1){

                    val guncellenecekKonusma = snapshot.getValue(Mesajlar::class.java)
                    guncellenecekKonusma!!.user_id= snapshot.key
                    Log.e("maesajlarchildchanged calıstıgoruldu",""+guncellenecekKonusma?.goruldu)
                    Log.e("maesajlarchildchanged calıstı",""+guncellenecekKonusma?.user_id)


                    mesajlarArray.removeAt(kontrol)
                    mesajlarArray.add(0,guncellenecekKonusma)
                    mesajlarMutable.value=mesajlarArray

                }

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val kontrol =konusmaPositionBul(snapshot.key.toString())
                if(kontrol != -1){

                    val guncellenecekKonusma = snapshot.getValue(Mesajlar::class.java)
                    guncellenecekKonusma!!.user_id= snapshot.key


                    mesajlarArray.removeAt(kontrol)
                    mesajlarMutable.value=mesajlarArray


                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }



    fun konusmaPositionBul(userID : String) : Int{


        for(i in 0 until mesajlarArray.size){
            val gecici = mesajlarArray[i]

            if(gecici.user_id.equals(userID)){
                return i
            }
        }

        return -1


    }
    fun removeListeners() {
        mListener?.let {
            Log.e("removemesajlarlistener",""+it)
            mref.child("konusmalar").child(auth.currentUser!!.uid).removeEventListener(it)
        }
    }


}