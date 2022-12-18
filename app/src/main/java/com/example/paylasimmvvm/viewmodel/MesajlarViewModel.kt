package com.example.paylasimmvvm.viewmodel

import android.os.CountDownTimer
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.Mesajlar
import com.example.paylasimmvvm.util.Bildirimler.mref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MesajlarViewModel:ViewModel() {
    val mesajlarMutable= MutableLiveData<List<Mesajlar>>()
    val mesajlarArray=ArrayList<Mesajlar>()
    val yukleniyor=MutableLiveData<Boolean>()
    val mesajYok=MutableLiveData<Boolean>()
    lateinit var auth : FirebaseAuth
    lateinit var mref: DatabaseReference

    fun refreshMesajlar(){

        auth=  FirebaseAuth.getInstance()
        mref = FirebaseDatabase.getInstance().reference

        mesajlarMutable.value=mesajlarArray
        yukleniyor.value=false
         mref.child("konusmalar").child(auth.currentUser!!.uid).orderByChild("gonderilmeZamani").addChildEventListener(mListener)



          if (mesajlarArray.size==0){
                    mref.child("konusmalar").child(auth.currentUser!!.uid).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.getValue()==null){
                                mesajYok.value=true

                              //  binding.mesajYok.visibility= View.VISIBLE
                               // binding.recyclerMesajlar.visibility= View.GONE
                            }else{
                                mesajYok.value=false
                              // binding.mesajYok.visibility= View.GONE
                             //   binding. recyclerMesajlar.visibility= View.VISIBLE

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })
                }


    }
    var mListener=object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            mesajYok.value=false
            mesajlarMutable.value=mesajlarArray
           // binding. mesajYok.visibility= View.GONE
            //binding. recyclerMesajlar.visibility= View.VISIBLE
            var eklenecekKonusma=snapshot.getValue(Mesajlar::class.java)
            eklenecekKonusma!!.user_id=snapshot.key
            mesajlarMutable.value=mesajlarArray
         //  tumKonusmalar.add(0,eklenecekKonusma!!)
           // madapter.notifyItemInserted(0)

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            var kontrol =konusmaPositionBul(snapshot!!.key.toString())
            if(kontrol != -1){

                var guncellenecekKonusma = snapshot!!.getValue(Mesajlar::class.java)
                guncellenecekKonusma!!.user_id=snapshot!!.key

                mesajlarMutable.value=mesajlarArray

                // tumKonusmalar.removeAt(kontrol)
                //madapter.notifyItemRemoved(kontrol)
                //tumKonusmalar.add(0,guncellenecekKonusma)
                //madapter.notifyItemInserted(0)


            }

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            var kontrol =konusmaPositionBul(snapshot!!.key.toString())
            if(kontrol != -1){

                var guncellenecekKonusma = snapshot!!.getValue(Mesajlar::class.java)
                guncellenecekKonusma!!.user_id=snapshot!!.key

                mesajlarMutable.value=mesajlarArray


              //  tumKonusmalar.removeAt(kontrol)
              //  madapter.notifyItemRemoved(kontrol)




            }
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }

    }


    fun konusmaPositionBul(userID : String) : Int{


        for(i in 0..mesajlarArray.size-1){
            var gecici = mesajlarArray.get(i)

            if(gecici.user_id.equals(userID)){
                return i
            }
        }

        return -1


    }

}