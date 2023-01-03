package com.example.paylasimmvvm.view.profil

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.ProfilFragmentRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentProfilBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.view.login.SignOutFragment
import com.example.paylasimmvvm.viewmodel.ProfilViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import java.util.*


class ProfilFragment : Fragment() {
    private lateinit var binding: FragmentProfilBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    private var tumGonderiler=ArrayList<KullaniciKampanya>()
    private lateinit var profilKampanyalarViewModeli:ProfilViewModel
    private lateinit var recyclerviewadapter:ProfilFragmentRecyclerAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mref= FirebaseDatabase.getInstance().reference


        setupAuthLis()

        profilKampanyalarViewModeli= ViewModelProvider(this)[ProfilViewModel::class.java]
        profilKampanyalarViewModeli.refreshProfilKampanya(auth.currentUser!!.uid)

       kullaniciBilgileriVerileriniAl()
        observeliveData()
        profilDuzenle()


        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerProfil.layoutManager=layoutManager
        recyclerviewadapter= ProfilFragmentRecyclerAdapter(requireActivity(),tumGonderiler)
        binding.recyclerProfil.adapter=recyclerviewadapter

        val menuHost: MenuHost = requireActivity()


        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu2, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                if (menuItem.itemId== R.id.cikisYap){
                    val dialog=SignOutFragment()
                    dialog.show(parentFragmentManager,"Çıkış yap")
                }
                else if(menuItem.itemId==R.id.kampanyaOlustur_id){


                  val action=ProfilFragmentDirections.actionProfilFragmentToKampanyaOlusturFragment()

                    Navigation.findNavController(view).navigate(action)

                }
                else if(menuItem.itemId==R.id.sabitkampanyaOlustur_id){


                    val action=ProfilFragmentDirections.actionProfilFragmentToBiralarFragment()
                    Navigation.findNavController(view).navigate(action)
                }

                return true
            }


    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeliveData(){
        profilKampanyalarViewModeli.profilKampanya.observe(viewLifecycleOwner) { profilKampanya ->
            profilKampanya.let {
                Collections.sort(profilKampanya, object : Comparator<KullaniciKampanya> {
                    override fun compare(p0: KullaniciKampanya?, p1: KullaniciKampanya?): Int {
                        if (p0!!.postYuklenmeTarih!! > p1!!.postYuklenmeTarih!!) {
                            return -1
                        } else return 1
                    }

                })

                binding.recyclerProfil.visibility = View.VISIBLE
                recyclerviewadapter.kampanyalariGuncelle(profilKampanya)
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentProfilBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference


        return view
    }

    private fun kullaniciBilgileriVerileriniAl() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            mref.child("users").child("isletmeler").child(auth.currentUser!!.uid).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){
                        val okunanKullanici= snapshot.getValue(KullaniciBilgileri::class.java)
                        EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                        binding.tvMesaj.isEnabled=true

                        binding.tvKullaniciAdi.text = okunanKullanici!!.user_name

                        binding.tvPost.text = okunanKullanici.user_detail!!.post

                        Log.e("post", "sayisi$okunanKullanici")
                        if (!okunanKullanici.user_detail!!.biography.isNullOrEmpty()){
                            binding.tvBio.text = okunanKullanici.user_detail!!.biography


                        }

                        val imgUrl:String= okunanKullanici.user_detail!!.profile_picture!!
                        if (imgUrl.isNotEmpty()){
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
                    if (snapshot.value !=null){
                        val okunanKullanici= snapshot.getValue(KullaniciBilgileri::class.java)
                        EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                        binding.tvMesaj.isEnabled=true

                        binding.tvKullaniciAdi.text = okunanKullanici!!.user_name
                        binding.tvPost.text = okunanKullanici.user_detail!!.post

                        if (!okunanKullanici.user_detail!!.biography.isNullOrEmpty()){
                            binding.tvBio.text = okunanKullanici.user_detail!!.biography


                        }

                        val imgUrl:String= okunanKullanici.user_detail!!.profile_picture!!

                        if (imgUrl.isNotEmpty()){
                            Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)


                        }else {
                            Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }



    }



    private fun setupAuthLis() {
        val user=FirebaseAuth.getInstance().currentUser




        mauthLis= FirebaseAuth.AuthStateListener {
            if (user==null){
                findNavController().popBackStack(R.id.profilFragment,true)
                findNavController().navigate(R.id.loginFragment)
                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.visibility=View.GONE


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

    private fun profilDuzenle(){
        binding.tvMesaj.setOnClickListener {
            val navOptions = NavOptions.Builder().setPopUpTo(R.id.profilFragment, true).build()
            Navigation.findNavController(it).navigate(R.id.profilEditFragment, null, navOptions)
        }

        }



}