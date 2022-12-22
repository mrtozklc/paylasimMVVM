package com.example.paylasimmvvm.view.profil

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.ProfilFragmentRecyclerAdapter
import com.example.paylasimmvvm.adapter.UserProfilRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentProfilBinding
import com.example.paylasimmvvm.databinding.FragmentUserProfilBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.viewmodel.ProfilViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import java.util.*


class UserProfilFragment : Fragment() {
    private lateinit var binding: FragmentUserProfilBinding
    private lateinit var auth : FirebaseAuth
    lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    lateinit var muser: FirebaseUser
    var tumGonderiler= ArrayList<KullaniciKampanya>()
    private lateinit var userProfilKampanyalarViewModeli: ProfilViewModel
    private lateinit var recyclerviewadapter: UserProfilRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentUserProfilBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        arguments?.let {
            val secilenUser=UserProfilFragmentArgs.fromBundle(it).userId
            userProfilKampanyalarViewModeli= ViewModelProvider(this).get(ProfilViewModel::class.java)
            userProfilKampanyalarViewModeli.refreshProfilKampanya(secilenUser)


            observeliveData(secilenUser)
            kullaniciBilgileriVerileriniAl(secilenUser)


        }



        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerUserProfil.layoutManager=layoutManager
        recyclerviewadapter= UserProfilRecyclerAdapter(requireActivity(),tumGonderiler)
        binding.recyclerUserProfil.adapter=recyclerviewadapter




    }

    fun kullaniciBilgileriVerileriniAl(secilenUser:String) {
        val user = Firebase.auth.currentUser
        if (user != null) {
            mref.child("users").child("isletmeler").child(secilenUser).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot!!.getValue()!=null){
                        var okunanKullanici=snapshot!!.getValue(KullaniciBilgileri::class.java)
                        EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                        binding.tvMesaj.isEnabled=true
                        binding.tvKullaniciAdii.setText(okunanKullanici!!.user_name)



                        binding.tvPostt.text = okunanKullanici!!.user_detail!!.post

                        Log.e("post","sayisi"+okunanKullanici)
                        if (!okunanKullanici!!.user_detail!!.biography.isNullOrEmpty()){
                            binding.tvBioo.setText(okunanKullanici!!.user_detail!!.biography)


                        }

                        var imgUrl:String=okunanKullanici!!.user_detail!!.profile_picture!!
                        if (!imgUrl.isEmpty()){
                            Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImagee)


                        }else {
                            Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImagee)

                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
            mref.child("users").child("kullanicilar").child(secilenUser).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot!!.getValue()!=null){
                        var okunanKullanici=snapshot!!.getValue(KullaniciBilgileri::class.java)
                        EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                        binding.tvMesaj.isEnabled=true

                        binding.tvKullaniciAdii.setText(okunanKullanici!!.user_name)
                        binding.tvPostt.setText(okunanKullanici!!.user_detail!!.post)

                        if (!okunanKullanici!!.user_detail!!.biography.isNullOrEmpty()){
                            binding.tvBioo.setText(okunanKullanici!!.user_detail!!.biography)


                        }

                        var imgUrl:String=okunanKullanici!!.user_detail!!.profile_picture!!
                        Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImagee)


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }



    }

    fun observeliveData(userId:String){
        userProfilKampanyalarViewModeli.profilKampanya.observe(viewLifecycleOwner, androidx.lifecycle.Observer { profilKampanya->
            profilKampanya.let {
                Collections.sort(profilKampanya,object : Comparator<KullaniciKampanya> {
                    override fun compare(p0: KullaniciKampanya?, p1: KullaniciKampanya?): Int {
                        if (p0!!.postYuklenmeTarih!!>p1!!.postYuklenmeTarih!!){
                            return -1
                        }else return 1
                    }

                })

                binding.recyclerUserProfil.visibility = View.VISIBLE
                recyclerviewadapter!!.kampanyalariGuncelle(profilKampanya)
            }
        })

    }
}