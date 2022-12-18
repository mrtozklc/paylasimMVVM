package com.example.paylasimmvvm.view.profil

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.ProfilFragmentRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentProfilBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.viewmodel.ProfilViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import java.util.*


class ProfilFragment : Fragment() {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var auth : FirebaseAuth
    lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    lateinit var muser: FirebaseUser
    var tumGonderiler=ArrayList<KullaniciKampanya>()
    private lateinit var profilKampanyalarViewModeli:ProfilViewModel
    private lateinit var recyclerviewadapter:ProfilFragmentRecyclerAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mref= FirebaseDatabase.getInstance().reference
        Log.e("oncreated","")

        setupAuthLis()

        profilKampanyalarViewModeli= ViewModelProvider(this).get(ProfilViewModel::class.java)
        profilKampanyalarViewModeli.refreshProfilKampanya()

       kullaniciBilgileriVerileriniAl()
        observeliveData()
        profilDuzenle()


        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerProfil.layoutManager=layoutManager
        recyclerviewadapter= ProfilFragmentRecyclerAdapter(requireActivity(),tumGonderiler)
        binding.recyclerProfil.adapter=recyclerviewadapter

    }
    fun observeliveData(){
        profilKampanyalarViewModeli.profilKampanya.observe(viewLifecycleOwner, androidx.lifecycle.Observer { profilKampanya->
            profilKampanya.let {
                Collections.sort(profilKampanya,object : Comparator<KullaniciKampanya> {
                    override fun compare(p0: KullaniciKampanya?, p1: KullaniciKampanya?): Int {
                        if (p0!!.postYuklenmeTarih!!>p1!!.postYuklenmeTarih!!){
                            return -1
                        }else return 1
                    }

                })

                binding.recyclerProfil.visibility = View.VISIBLE
                recyclerviewadapter!!.kampanyalariGuncelle(profilKampanya)
            }
        })

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentProfilBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference


        return view
    }

    fun kullaniciBilgileriVerileriniAl() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            mref.child("users").child("isletmeler").child(auth.currentUser!!.uid).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot!!.getValue()!=null){
                        var okunanKullanici=snapshot!!.getValue(KullaniciBilgileri::class.java)
                        EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                        binding.tvMesaj.isEnabled=true

                        binding.tvKullaniciAdi.setText(okunanKullanici!!.user_name)

                        binding.tvPost.text = okunanKullanici!!.user_detail!!.post

                        Log.e("post","sayisi"+okunanKullanici)
                        if (!okunanKullanici!!.user_detail!!.biography.isNullOrEmpty()){
                            binding.tvBio.setText(okunanKullanici!!.user_detail!!.biography)


                        }

                        var imgUrl:String=okunanKullanici!!.user_detail!!.profile_picture!!
                        if (!imgUrl.isEmpty()){
                            Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)


                        }else {
                            Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
            mref.child("users").child("kullanicilar").child(auth.currentUser!!.uid).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot!!.getValue()!=null){
                        var okunanKullanici=snapshot!!.getValue(KullaniciBilgileri::class.java)
                        EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                        binding.tvMesaj.isEnabled=true

                        binding.tvKullaniciAdi.setText(okunanKullanici!!.user_name)
                        binding.tvPost.setText(okunanKullanici!!.user_detail!!.post)

                        if (!okunanKullanici!!.user_detail!!.biography.isNullOrEmpty()){
                            binding.tvBio.setText(okunanKullanici!!.user_detail!!.biography)


                        }

                        var imgUrl:String=okunanKullanici!!.user_detail!!.profile_picture!!
                        Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }



    }


    private fun setupAuthLis() {
        var user=FirebaseAuth.getInstance().currentUser




        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {

                if (user==null){
                    findNavController().popBackStack(R.id.profilFragment,true)
                    findNavController().navigate(R.id.loginFragment)
                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNav.visibility=View.GONE


                }

            }

        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","profildesin")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {

        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }

    fun profilDuzenle(){
        binding.tvMesaj.setOnClickListener(){
          Navigation.findNavController(it).navigate(R.id.profilAyarlarFragment)
        }
    }


}