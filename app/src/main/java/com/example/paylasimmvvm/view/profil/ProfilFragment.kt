package com.example.paylasimmvvm.view.profil

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.MenulerGridAdapter
import com.example.paylasimmvvm.adapter.MudavimlerRecyclerAdapter
import com.example.paylasimmvvm.adapter.ProfilFragmentRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentProfilBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.model.Menuler
import com.example.paylasimmvvm.model.Mudavimler
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.view.login.SignOutFragment
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
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
    private var tumMenuler=ArrayList<Menuler>()
    private var tumMudavimler= ArrayList<Mudavimler>()

    private var tumGonderiler=ArrayList<KullaniciKampanya>()
    private lateinit var profilKampanyalarViewModeli:ProfilViewModel
    private lateinit var recyclerviewadapter:ProfilFragmentRecyclerAdapter
    private lateinit var recyclerMudavimler: MudavimlerRecyclerAdapter
    private lateinit var profilBadges:BadgeViewModel
    var tiklanilanKullanici:String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.menu.visibility=View.INVISIBLE
        binding.paylasimlar.visibility=View.INVISIBLE
        binding.yorumlar.visibility=View.INVISIBLE
        binding.mudavimler.visibility=View.INVISIBLE
        binding.recyclerProfil.visibility=View.INVISIBLE

        mref= FirebaseDatabase.getInstance().reference

        setupAuthLis()

        profilKampanyalarViewModeli= ViewModelProvider(this)[ProfilViewModel::class.java]
        profilKampanyalarViewModeli.refreshProfilKampanya(auth.currentUser!!.uid)

        profilBadges= ViewModelProvider(this)[BadgeViewModel::class.java]
        profilBadges.refreshIsletmeYorumlarBadge(auth.currentUser!!.uid)
        profilBadges.refreshBadge()
        profilKampanyalarViewModeli.getMenus(auth.currentUser!!.uid)
        profilKampanyalarViewModeli.getMudavimler(auth.currentUser!!.uid)

        kullaniciBilgileriVerileriniAl()
        observeliveData()

        binding.paylasimlar.isEnabled=false


        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerProfil.layoutManager=layoutManager
        recyclerviewadapter= ProfilFragmentRecyclerAdapter(requireActivity(),tumGonderiler)
        binding.recyclerProfil.adapter=recyclerviewadapter



        binding.paylasimlar.setOnClickListener {
            binding.paylasimlar.isEnabled=false
            binding.menu.isEnabled=true
            binding.yorumlar.isEnabled=true
            binding.mudavimler.isEnabled=true

            profilKampanyalarViewModeli= ViewModelProvider(this)[ProfilViewModel::class.java]
            profilKampanyalarViewModeli.refreshProfilKampanya(auth.currentUser!!.uid)

            profilKampanyalarViewModeli.gonderiYok.observe(viewLifecycleOwner){
                it.let {
                    if(it){
                        binding.gonderiYok.text = "Henüz hiç gönderi yok."
                        binding.gonderiYok.visibility=View.VISIBLE
                        binding.gridId.visibility=View.GONE
                        binding.recyclerProfil.visibility=View.GONE

                    }else{
                        binding.gonderiYok.visibility=View.GONE


                    }
                }
            }


        }

        binding.menu.setOnClickListener {
            binding.menu.isEnabled=false
            binding.paylasimlar.isEnabled=true
            binding.yorumlar.isEnabled=true
            binding.mudavimler.isEnabled=true

            profilKampanyalarViewModeli.getMenus(auth.currentUser!!.uid)
            profilKampanyalarViewModeli.gonderiYok.observe(viewLifecycleOwner){
                it.let {
                    if(it){
                        binding.gonderiYok.text = "Henüz menü yüklenmemiş."
                        binding.gonderiYok.visibility=View.VISIBLE
                        binding.gridId.visibility=View.GONE
                        binding.recyclerProfil.visibility=View.GONE

                    }else{
                        binding.gonderiYok.visibility=View.GONE

                    }
                }
            }


        }

        binding.yorumlar.setOnClickListener {
            binding.yorumlar.isEnabled=false
            binding.menu.isEnabled=true
            binding.paylasimlar.isEnabled=true
            binding.mudavimler.isEnabled=true

            EventBus.getDefault()
                .postSticky(EventbusData.YorumYapilacakGonderininIDsiniGonder(tiklanilanKullanici))

            val action=ProfilFragmentDirections.actionProfilFragmentToYorumlarFragment()
            Navigation.findNavController(it).navigate(action)




        }

        binding.mudavimler.setOnClickListener {

            binding.mudavimler.isEnabled=false
            binding.menu.isEnabled=true
            binding.paylasimlar.isEnabled=true
            binding.yorumlar.isEnabled=true
            profilKampanyalarViewModeli.getMudavimler(auth.currentUser!!.uid)

            val layoutManager= LinearLayoutManager(activity)
            binding.recyclerProfil.layoutManager=layoutManager
            recyclerMudavimler= MudavimlerRecyclerAdapter(tumMudavimler)
            binding.recyclerProfil.adapter=recyclerMudavimler
            binding.recyclerProfil.visibility = View.VISIBLE
            binding.gridId.visibility=View.GONE

            profilKampanyalarViewModeli.mudavimYok.observe(viewLifecycleOwner){
                it.let {
                    if(it){
                        binding.gonderiYok.text = "Henüz hiç müdavim yok."
                        binding.gonderiYok.visibility=View.VISIBLE
                        binding.gridId.visibility=View.GONE
                        binding.recyclerProfil.visibility=View.GONE

                    }else{
                        binding.gonderiYok.visibility=View.GONE

                    }
                }
            }
            profilKampanyalarViewModeli.mudavimlerMutableLiveData.observe(viewLifecycleOwner){
                it.let {
                    if (it!=null){
                        val layoutManager= LinearLayoutManager(activity)
                        binding.recyclerProfil.layoutManager=layoutManager
                        binding.recyclerProfil.adapter=recyclerMudavimler
                        binding.recyclerProfil.visibility = View.VISIBLE
                        binding.gridId.visibility=View.GONE
                        binding.gonderiYok.visibility=View.GONE
                        recyclerMudavimler= MudavimlerRecyclerAdapter(tumMudavimler)
                        recyclerMudavimler.mudavimListesiniGuncelle(it)
                    }
                }
            }
        }

        val menuHost: MenuHost = requireActivity()



        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu2, menu)
            }


            @SuppressLint("SuspiciousIndentation")
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {

                    R.id.cikisYap -> {
                        val dialog=SignOutFragment()
                        dialog.show(parentFragmentManager,"Çıkış yap")
                    }
                    R.id.kampanyaOlustur_id -> {

                        val action=ProfilFragmentDirections.actionProfilFragmentToKampanyaOlusturFragment()
                        Navigation.findNavController(view).navigate(action)

                    }


                    R.id.profilDuzenle_id -> {

                        val action=ProfilFragmentDirections.actionProfilFragmentToProfilEditFragment()
                        Navigation.findNavController(view).navigate(action)


                    }
                    R.id.bildirimlerbar -> {

                        val action=ProfilFragmentDirections.actionProfilFragmentToBildirimlerFragment()
                        Navigation.findNavController(view).navigate(action)


                    }
                }

                return true
            }


    }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    @SuppressLint("SetTextI18n")
    private fun observeliveData(){

        val gridView = requireActivity().findViewById(R.id.grid_id) as GridView

        val customAdapter = MenulerGridAdapter(tumMenuler,true)

        gridView.adapter = customAdapter

        profilKampanyalarViewModeli.profilMenu.observe(viewLifecycleOwner) { profilMenu ->
            profilMenu.let {
                if (profilMenu!=null){
                    binding.gridId.visibility = View.VISIBLE
                    customAdapter.menuleriGuncelle(profilMenu)
                    binding.recyclerProfil.visibility=View.GONE
                    binding.gonderiYok.visibility=View.GONE

                }
            }
        }
        profilKampanyalarViewModeli.menuSayisiMutable.observe(viewLifecycleOwner) { profilMenu ->
            profilMenu.let {
                if (profilMenu!=null){

                    binding.menu.text = "Menu\n${profilMenu}"
                }
            }
        }

        profilBadges.isletmeYorumlarBadgeLive.observe(viewLifecycleOwner) {yorumSayisi ->
            yorumSayisi.let {

                if (yorumSayisi!=null){

                    binding.yorumlar.text = "Yorumlar \n$yorumSayisi"

                }

            }

        }

        profilKampanyalarViewModeli.gonderiYok.observe(viewLifecycleOwner){
            it.let {
                if(it){
                    binding.gonderiYok.text = "Henüz hiç gönderi yok."
                    binding.gonderiYok.visibility=View.VISIBLE
                    binding.gridId.visibility=View.GONE
                    binding.recyclerProfil.visibility=View.GONE

                }else{
                    binding.gonderiYok.visibility=View.GONE


                }
            }

        }

        profilKampanyalarViewModeli.yukleniyor.observe(viewLifecycleOwner){
            it.let {
                if(it){
                    binding.gonderiYok.visibility=View.GONE
                    binding.gridId.visibility=View.GONE
                    binding.recyclerProfil.visibility=View.GONE
                    binding.progresBarKokteyl.visibility=View.VISIBLE

                }else{
                    binding.progresBarKokteyl.visibility=View.GONE

                }
            }
        }

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
                binding.gridId.visibility=View.GONE
                recyclerviewadapter.kampanyalariGuncelle(profilKampanya)
                binding.paylasimlar.text = "Paylaşımlar [${profilKampanya.size}]"
            }
        }

        profilKampanyalarViewModeli.mudavimSayisiMutableLiveData.observe(viewLifecycleOwner){ Mudavimler->
            Mudavimler.let {
                if (Mudavimler!=null){
                    binding.mudavimler.text="Müdavim \n${Mudavimler}"
                }
            }
        }

        profilKampanyalarViewModeli.mudavimlerMutableLiveData.observe(viewLifecycleOwner){ Mudavimler->
            Mudavimler.let {
                if (Mudavimler!=null){

                    recyclerMudavimler= MudavimlerRecyclerAdapter(tumMudavimler)
                    recyclerMudavimler.mudavimListesiniGuncelle(Mudavimler)

                }
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

                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){

                        binding.menu.visibility=View.VISIBLE
                        binding.paylasimlar.visibility=View.VISIBLE
                        binding.yorumlar.visibility=View.VISIBLE
                        binding.mudavimler.visibility=View.VISIBLE

                        val okunanKullanici= snapshot.getValue(KullaniciBilgileri::class.java)
                        EventBus.getDefault().postSticky(EventbusData.kullaniciBilgileriniGonder(okunanKullanici))

                        tiklanilanKullanici=okunanKullanici!!.user_id

                        binding.tvKullaniciAdi.text = okunanKullanici.user_name


                        binding.paylasimlar.text = "Paylaşımlar [${okunanKullanici.user_detail!!.post}]"


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

                        binding.tvKullaniciAdi.text = okunanKullanici!!.user_name




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



}
