package com.example.paylasimmvvm.view.mesajlar

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.ChatFragmentRecyclerAdapter
import com.example.paylasimmvvm.adapter.MesajlarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentChatBinding
import com.example.paylasimmvvm.model.ChatModel
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.Mesajlar
import com.example.paylasimmvvm.view.profil.UserProfilFragmentArgs
import com.example.paylasimmvvm.viewmodel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class ChatFragment : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private lateinit var auth : FirebaseAuth
    lateinit var mref: DatabaseReference
    private lateinit var recyclerAdapter: ChatFragmentRecyclerAdapter
    private lateinit var chatViewModeli: ChatViewModel
    var tumMesajlar=ArrayList<ChatModel>()
    lateinit var sohbetEdilcekKisi:String
    lateinit var mesajGonderenId:String
    lateinit var myRecyclerview: RecyclerView
    lateinit var eventListener: ChildEventListener
    lateinit var eventListenerRefresh: ChildEventListener
    val gosterilecekMesajSayisi=10
    var sayfaSayisi=1
    var mesajPosition=0
    var refreshMesajPosition=0
    var getirilenMesajId=""
    var zatenListedeOlanMesajID=""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentChatBinding.inflate(layoutInflater,container,false)
        val view =binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val sohbetEdilcekKisi= UserProfilFragmentArgs.fromBundle(it).userId
            sohbetEdilenUserName(sohbetEdilcekKisi)
            mesajGonderenId=FirebaseAuth.getInstance().currentUser!!.uid

            chatViewModeli=ViewModelProvider(this).get(ChatViewModel::class.java)
            chatViewModeli.refreshChat(sohbetEdilcekKisi)

            observeLiveData(sohbetEdilcekKisi)

            binding.tvvMesajGonder.setOnClickListener {

                var mesaj= binding.etMesajEkle.text.toString().trim()

                if(!TextUtils.isEmpty(mesaj.toString())){

                    var mesajAtan=HashMap<String,Any>()
                    mesajAtan.put("mesaj",binding.etMesajEkle.text.toString())
                    mesajAtan.put("gonderilmeZamani", ServerValue.TIMESTAMP)
                    mesajAtan.put("type","text")
                    mesajAtan.put("goruldu",true)
                    mesajAtan.put("user_id",auth.currentUser!!.uid)
                    var mesajKey = mref.child("mesajlar").child(auth.currentUser!!.uid).child(sohbetEdilcekKisi).push().key


                    mref.child("mesajlar").child(auth.currentUser!!.uid).child(sohbetEdilcekKisi).child(mesajKey!!).setValue(mesajAtan)

                    var mesajAlan=HashMap<String,Any>()
                    mesajAlan.put("mesaj",binding.etMesajEkle.text.toString())
                    mesajAlan.put("gonderilmeZamani", ServerValue.TIMESTAMP)
                    mesajAlan.put("type","text")
                    mesajAlan.put("goruldu",false)
                    mesajAlan.put("user_id",auth.currentUser!!.uid)
                    mref.child("mesajlar").child(sohbetEdilcekKisi).child(auth.currentUser!!.uid).child(mesajKey).setValue(mesajAlan)


                    var KonusmamesajAtan=HashMap<String,Any>()
                    KonusmamesajAtan.put("son_mesaj",binding.etMesajEkle.text.toString())
                    KonusmamesajAtan.put("gonderilmeZamani", ServerValue.TIMESTAMP)
                    KonusmamesajAtan.put("goruldu",true)

                    mref.child("konusmalar").child(auth.currentUser!!.uid).child(sohbetEdilcekKisi).setValue(KonusmamesajAtan)

                    var KonusmamesajAlan=HashMap<String,Any>()
                    KonusmamesajAlan.put("son_mesaj",binding.etMesajEkle.text.toString())
                    KonusmamesajAlan.put("gonderilmeZamani", ServerValue.TIMESTAMP)
                    KonusmamesajAlan.put("goruldu",false)

                    mref.child("konusmalar").child(sohbetEdilcekKisi).child(auth.currentUser!!.uid).setValue(KonusmamesajAlan)

                    binding.etMesajEkle.setText("")


                }




            }
            binding.refreshId.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
                override fun onRefresh() {

                    mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if(snapshot!!.childrenCount.toInt() != tumMesajlar.size){

                                refreshMesajPosition=0

                                refreshMesajlar()

                            }else{

                                binding.refreshId.isRefreshing = false
                                binding.refreshId.isEnabled = false

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })


                    binding.refreshId.isRefreshing=false
                }

            })


        }


        val layoutManager= LinearLayoutManager(activity)
        binding.sohbetRecycler.layoutManager=layoutManager
        recyclerAdapter= ChatFragmentRecyclerAdapter(tumMesajlar)
        binding.sohbetRecycler.adapter=recyclerAdapter
        myRecyclerview=binding.sohbetRecycler




    }

    private fun observeLiveData(secilenUser:String) {

        chatViewModeli.chatMutable.observe(viewLifecycleOwner) {sohbet->
            sohbet.let {

                binding.sohbetRecycler.visibility = View.VISIBLE
                recyclerAdapter!!.mesajlariGuncelle(sohbet)
            }

        }

        chatViewModeli.yukleniyor.observe(viewLifecycleOwner){
            it.let {
                if (it){
                    binding.progressBarChat.visibility=View.VISIBLE
                    binding.sohbetRecycler.visibility = View.GONE
                }else{
                    binding.progressBarChat.visibility=View.GONE

                }
            }

        }
    }

    private fun sohbetEdilenUserName(sohbetEdilcekKisi: String) {

        mref.child("users").child("isletmeler").child(sohbetEdilcekKisi).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot .getValue()!=null){

                    var bulunanKullanici=snapshot!!.getValue(KullaniciBilgileri::class.java)!!.user_name
                    binding.tvMesajlasLanUserName.setText(bulunanKullanici)




                }
                mref.child("users").child("kullanicilar").child(sohbetEdilcekKisi).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot .getValue()!=null){

                            var bulunanKullanici=snapshot!!.getValue(KullaniciBilgileri::class.java)!!.user_name
                            binding.tvMesajlasLanUserName.setText(bulunanKullanici)


                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

    private fun refreshMesajlar(){

        Log.e("refresh","mesajlarzatenlisetedeolan")


        arguments?.let {

            val sohbetEdilcekKisi= UserProfilFragmentArgs.fromBundle(it).userId
            eventListener= mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).limitToLast(gosterilecekMesajSayisi).addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {


                    if (mesajPosition==0){

                        getirilenMesajId= snapshot!!.key!!
                        zatenListedeOlanMesajID=snapshot!!.key!!

                    }
                    mesajPosition++


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

            eventListenerRefresh=mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).orderByKey().endAt(getirilenMesajId).limitToLast(gosterilecekMesajSayisi).addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    var okunanMesaj=snapshot.getValue(ChatModel::class.java)

                    if(!zatenListedeOlanMesajID.equals(snapshot!!.key)){



                        tumMesajlar.add(refreshMesajPosition++,okunanMesaj!!)

                        recyclerAdapter.notifyItemInserted(refreshMesajPosition-1)


                    }else {


                        zatenListedeOlanMesajID=getirilenMesajId

                    }

                    if(refreshMesajPosition==1){

                        getirilenMesajId=snapshot!!.key!!


                    }

                    binding.refreshId.isRefreshing = false
                    recyclerAdapter.notifyDataSetChanged()
                    myRecyclerview.scrollToPosition(0)

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

}