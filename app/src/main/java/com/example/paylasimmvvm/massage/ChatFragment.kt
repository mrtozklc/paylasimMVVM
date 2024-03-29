package com.example.paylasimmvvm.massage

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentChatBinding
import com.example.paylasimmvvm.login.KullaniciBilgileri
import com.example.paylasimmvvm.profile.UserProfilFragmentArgs
import com.example.paylasimmvvm.util.setBadge
import com.example.paylasimmvvm.util.BadgeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
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
    private lateinit var badgeViewModeli: BadgeViewModel
    var tumMesajlar=ArrayList<ChatModel>()
    private lateinit var mesajGonderenId:String
    lateinit var myRecyclerview: RecyclerView
    private lateinit var eventListener: ChildEventListener
    private lateinit var eventListenerRefresh: ChildEventListener
    private val gosterilecekMesajSayisi=10
    var mesajPosition=0
    var refreshMesajPosition=0
    var getirilenMesajId=""
    var zatenListedeOlanMesajID=""
    var listenerAtandiMi=false


    companion object {
        var fragmentAcikMi=false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

            val konusulacakKisi = arguments?.getString("konusulacakKisi")
            chatViewModeli= ViewModelProvider(this)[ChatViewModel::class.java]
            badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]

            if (konusulacakKisi != null) {
                sohbetEdilenUserName(konusulacakKisi)
                chatViewModeli.refreshChat(konusulacakKisi)
                badgeViewModeli.refreshMessageBadge()
                mesajGonder(konusulacakKisi)

            }else{
                sohbetEdilenUserName(sohbetEdilcekKisi)
                mesajGonderenId=FirebaseAuth.getInstance().currentUser!!.uid
                chatViewModeli.refreshChat(sohbetEdilcekKisi)
                badgeViewModeli.refreshMessageBadge()
                mesajGonder(sohbetEdilcekKisi)



            }

            observeLiveData()


            binding.refreshId.setOnRefreshListener {
                mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot.childrenCount.toInt() != tumMesajlar.size) {

                                refreshMesajPosition = 0

                                refreshMesajlar()

                            } else {

                                binding.refreshId.isRefreshing = false
                                binding.refreshId.isEnabled = false

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })


                binding.refreshId.isRefreshing = false
            }

            binding.imageViewBack.setOnClickListener {

                 findNavController().navigateUp()
            }

            binding.tvMesajlasLanUserName.setOnClickListener {
                if (sohbetEdilcekKisi!=FirebaseAuth.getInstance().currentUser?.uid){
                    val action= ChatFragmentDirections.actionChatFragmentToUserProfilFragment(
                        sohbetEdilcekKisi!!
                    )
                    Navigation.findNavController(it).navigate(action)
                }

            }


        }


        val layoutManager= LinearLayoutManager(activity)
        binding.sohbetRecycler.layoutManager=layoutManager
        recyclerAdapter= ChatFragmentRecyclerAdapter(tumMesajlar)
        binding.sohbetRecycler.adapter=recyclerAdapter
        myRecyclerview=binding.sohbetRecycler




    }

    private fun mesajGonder(mesajGonderilecekKisi:String){

        binding.tvvMesajGonder.setOnClickListener {

            val mesaj= binding.etMesajEkle.text.toString().trim()

            if(!TextUtils.isEmpty(mesaj)){

                val mesajAtan=HashMap<String,Any>()
                mesajAtan["mesaj"] = binding.etMesajEkle.text.toString()
                mesajAtan["gonderilmeZamani"] = ServerValue.TIMESTAMP
                mesajAtan["type"] = "text"
                mesajAtan["goruldu"] = true
                mesajAtan["user_id"] = auth.currentUser!!.uid
                val mesajKey = mref.child("mesajlar").child(auth.currentUser!!.uid).child(mesajGonderilecekKisi).push().key


                mref.child("mesajlar").child(auth.currentUser!!.uid).child(mesajGonderilecekKisi).child(mesajKey!!).setValue(mesajAtan)

                val mesajAlan=HashMap<String,Any>()
                mesajAlan["mesaj"] = binding.etMesajEkle.text.toString()
                mesajAlan["gonderilmeZamani"] = ServerValue.TIMESTAMP
                mesajAlan["type"] = "text"
                mesajAlan["goruldu"] = false
                mesajAlan["user_id"] = auth.currentUser!!.uid
                mref.child("mesajlar").child(mesajGonderilecekKisi).child(auth.currentUser!!.uid).child(mesajKey).setValue(mesajAlan)


                val KonusmamesajAtan=HashMap<String,Any>()
                KonusmamesajAtan["son_mesaj"] = binding.etMesajEkle.text.toString()
                KonusmamesajAtan["gonderilmeZamani"] = ServerValue.TIMESTAMP
                KonusmamesajAtan["goruldu"] = true

                mref.child("konusmalar").child(auth.currentUser!!.uid).child(mesajGonderilecekKisi).setValue(KonusmamesajAtan)

                val KonusmamesajAlan=HashMap<String,Any>()
                KonusmamesajAlan["son_mesaj"] = binding.etMesajEkle.text.toString()
                KonusmamesajAlan["gonderilmeZamani"] = ServerValue.TIMESTAMP
                KonusmamesajAlan["goruldu"] = false

                mref.child("konusmalar").child(mesajGonderilecekKisi).child(auth.currentUser!!.uid).setValue(KonusmamesajAlan)

                binding.etMesajEkle.setText("")


            }




        }

    }

    private fun observeLiveData() {

        chatViewModeli.chatMutable.observe(viewLifecycleOwner) {sohbet->
            sohbet.let {

                binding.sohbetRecycler.visibility = View.VISIBLE
                recyclerAdapter.mesajlariGuncelle(sohbet)

                if (recyclerAdapter.itemCount > 0) {
                    binding.sohbetRecycler.scrollToPosition(recyclerAdapter.itemCount - 1)
                }

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
        badgeViewModeli.badgeLiveMessage.observe(viewLifecycleOwner) {gorulmeyenMesajSayisi ->
            gorulmeyenMesajSayisi.let {

                val navView: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
                if (gorulmeyenMesajSayisi != null) {
                    navView.setBadge(R.id.mesajlarFragment, gorulmeyenMesajSayisi.size)

                }

            }

        }
    }


    private fun sohbetEdilenUserName(sohbetEdilcekKisi: String) {

        mref.child("users").child("isletmeler").child(sohbetEdilcekKisi).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot .value !=null){

                    val bulunanKullanici= snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                    binding.tvMesajlasLanUserName.text = bulunanKullanici




                }
                mref.child("users").child("kullanicilar").child(sohbetEdilcekKisi).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot .value !=null){

                            val bulunanKullanici= snapshot.getValue(KullaniciBilgileri::class.java)!!.user_name
                            binding.tvMesajlasLanUserName.text = bulunanKullanici


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




        arguments?.let {

            val sohbetEdilcekKisi= UserProfilFragmentArgs.fromBundle(it).userId
            eventListener= mref.child("mesajlar").child(mesajGonderenId).child(sohbetEdilcekKisi).limitToLast(gosterilecekMesajSayisi).addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {


                    if (mesajPosition==0){

                        getirilenMesajId= snapshot.key!!
                        zatenListedeOlanMesajID= snapshot.key!!

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
                @SuppressLint("NotifyDataSetChanged")
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                    val okunanMesaj=snapshot.getValue(ChatModel::class.java)

                    if(zatenListedeOlanMesajID != snapshot.key){



                        tumMesajlar.add(refreshMesajPosition++,okunanMesaj!!)

                        recyclerAdapter.notifyItemInserted(refreshMesajPosition-1)


                    }else {


                        zatenListedeOlanMesajID=getirilenMesajId

                    }

                    if(refreshMesajPosition==1){

                        getirilenMesajId= snapshot.key!!


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

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()


        fragmentAcikMi = if(!fragmentAcikMi){
            true
        }else{
            true
        }
        chatViewModeli.addListeners()
    }

    override fun onResume() {
        super.onResume()
        Log.e("onresumechat","")
        fragmentAcikMi = if(!fragmentAcikMi){
            true
        }else{
            true
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e("onpausechat","")

        fragmentAcikMi = if(fragmentAcikMi){
            false
        }else{
            false
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("ondestroychat","")


    }

    override fun onStop() {
        Log.e("onStop", "ChatViewModel state: ${chatViewModeli.toString()}")
        Log.e("onstopcalıschat","")
        super.onStop()
        fragmentAcikMi = if(fragmentAcikMi){
            false
        }else{
            false
        }
        Log.e("lifesycle",""+viewLifecycleOwner.lifecycle.currentState.toString())



       chatViewModeli.removeListener()


    }


}