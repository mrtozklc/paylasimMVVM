package com.example.paylasimmvvm.view.profil

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.MenulerGridAdapter
import com.example.paylasimmvvm.adapter.MudavimlerRecyclerAdapter
import com.example.paylasimmvvm.adapter.UserProfilRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentUserProfilBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.model.Menuler
import com.example.paylasimmvvm.model.Mudavimler
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
import com.example.paylasimmvvm.viewmodel.ProfilViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import java.util.*


class UserProfilFragment : Fragment() {
    private lateinit var binding: FragmentUserProfilBinding
    private lateinit var auth : FirebaseAuth
    lateinit var mref: DatabaseReference
    private var tumGonderiler= ArrayList<KullaniciKampanya>()
    private var tumMudavimler= ArrayList<Mudavimler>()
    private lateinit var userProfilKampanyalarViewModeli: ProfilViewModel
    private lateinit var recyclerviewadapter: UserProfilRecyclerAdapter
    private lateinit var recyclerMudavimler:MudavimlerRecyclerAdapter
    private lateinit var profilBadges:BadgeViewModel
    private var tumMenuler=ArrayList<Menuler>()
    var tiklananUser=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentUserProfilBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference
        // Inflate the layout for this fragment
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.menu.visibility=View.GONE
        binding.yorumlar.visibility=View.GONE
        binding.mudavimler.visibility=View.GONE
        binding.mudavimOl.visibility=View.GONE
        binding.paylasimlar.visibility=View.GONE
        binding.recyclerProfil.visibility=View.GONE

        arguments?.let { it ->
            val secilenUser=UserProfilFragmentArgs.fromBundle(it).userId
            tiklananUser=secilenUser
            userProfilKampanyalarViewModeli= ViewModelProvider(this)[ProfilViewModel::class.java]
            profilBadges= ViewModelProvider(this)[BadgeViewModel::class.java]
            profilBadges.refreshIsletmeYorumlarBadge(secilenUser)
            userProfilKampanyalarViewModeli.getMenus(secilenUser)
            userProfilKampanyalarViewModeli.getMudavimler(secilenUser)
            userProfilKampanyalarViewModeli.refreshProfilKampanya(secilenUser)
            kullaniciBilgileriVerileriniAl(secilenUser)
            binding.paylasimlar.isEnabled=false

            binding.paylasimlar.setOnClickListener {
                binding.paylasimlar.isEnabled=false
                binding.menu.isEnabled=true
                binding.yorumlar.isEnabled=true
                binding.mudavimler.isEnabled=true
                binding.mudavimOl.visibility=View.VISIBLE


                userProfilKampanyalarViewModeli.refreshProfilKampanya(secilenUser)

                userProfilKampanyalarViewModeli.gonderiYok.observe(viewLifecycleOwner){

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

            binding.imageViewBack.setOnClickListener { findNavController().navigateUp() }

            binding.menu.setOnClickListener {
                binding.menu.isEnabled=false
                binding.paylasimlar.isEnabled=true
                binding.yorumlar.isEnabled=true
                binding.mudavimler.isEnabled=true
                binding.mudavimOl.visibility=View.INVISIBLE
                userProfilKampanyalarViewModeli.getMenus(secilenUser)
                userProfilKampanyalarViewModeli.gonderiYok.observe(viewLifecycleOwner){
                    it.let { if(it){ binding.gonderiYok.text = "Henüz menü yüklenmemiş."
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
                EventBus.getDefault().postSticky(EventbusData.YorumYapilacakGonderininIDsiniGonder(secilenUser))
                val action=UserProfilFragmentDirections.actionUserProfilFragmentToYorumlarFragment()
                Navigation.findNavController(it).navigate(action)
            }

            binding.mudavimOl.setOnClickListener {



                if (binding.mudavimOl.text == "Müdavim Ol") {

                    val mudavim = HashMap<String, Any>()
                    mudavim["mudavimOlma_zamani"] = ServerValue.TIMESTAMP
                    mudavim["mudavim_id"] = auth.currentUser!!.uid
                    mref.child("mudavimler").child(secilenUser).child(auth.uid!!).setValue(mudavim)
                    binding.mudavimOl.setText("Takibi Bırak")
                } else {
                    mref.child("mudavimler").child(secilenUser).child(auth.uid!!).removeValue()
                    binding.mudavimOl.setText("Müdavim Ol")

                }
                    userProfilKampanyalarViewModeli.getMudavimSayisi(secilenUser)
            }

            binding.mudavimler.setOnClickListener {
                binding.mudavimler.isEnabled=false
                binding.menu.isEnabled=true
                binding.paylasimlar.isEnabled=true
                binding.yorumlar.isEnabled=true
                binding.mudavimOl.visibility=View.INVISIBLE
                userProfilKampanyalarViewModeli.getMudavimler(secilenUser)
                userProfilKampanyalarViewModeli.mudavimYok.observe(viewLifecycleOwner){
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
                userProfilKampanyalarViewModeli.mudavimlerMutableLiveData.observe(viewLifecycleOwner){
                    it.let {
                        if (it!=null){
                            val layoutManager= LinearLayoutManager(activity)
                            recyclerMudavimler= MudavimlerRecyclerAdapter(tumMudavimler)
                            binding.recyclerProfil.layoutManager=layoutManager
                            binding.recyclerProfil.adapter=recyclerMudavimler
                            binding.recyclerProfil.visibility = View.VISIBLE
                            binding.gridId.visibility=View.GONE
                            binding.gonderiYok.visibility=View.GONE
                            recyclerMudavimler.mudavimListesiniGuncelle(it)
                        }
                    }
                }
            }
            binding.mesajGonder.setOnClickListener {
                val action=UserProfilFragmentDirections.actionUserProfilFragmentToChatFragment(secilenUser)
                Navigation.findNavController(it).navigate(action)
            }
        }
        observeliveData()
        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerProfil.layoutManager=layoutManager
        recyclerviewadapter= UserProfilRecyclerAdapter(requireActivity(),tumGonderiler)
        binding.recyclerProfil.adapter=recyclerviewadapter
    }
    private fun kullaniciBilgileriVerileriniAl(secilenUser:String) {
        val user = Firebase.auth.currentUser
        if (user != null) {
        mref.child("users").child("isletmeler").child(secilenUser).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){

                        binding.menu.visibility=View.VISIBLE
                        binding.yorumlar.visibility=View.VISIBLE
                        binding.mudavimler.visibility=View.VISIBLE
                        binding.mudavimOl.visibility=View.VISIBLE
                        binding.paylasimlar.visibility=View.VISIBLE
                        binding.recyclerProfil.visibility=View.VISIBLE

                        val okunanKullanici= snapshot.getValue(KullaniciBilgileri::class.java)


                        binding.kullaniciadiId.text = okunanKullanici!!.user_name


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
          mref.child("users").child("kullanicilar").child(secilenUser).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){


                        val okunanKullanici= snapshot.getValue(KullaniciBilgileri::class.java)


                        binding.kullaniciadiId.text = okunanKullanici!!.user_name

                        if (!okunanKullanici.user_detail!!.biography.isNullOrEmpty()){


                        }



                        val imgUrl:String= okunanKullanici.user_detail!!.profile_picture!!
                        if (imgUrl.isNotEmpty()){
                            Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)

                        }else{
                            Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)

                        }


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }



    }

    @SuppressLint("SetTextI18n")
    private fun observeliveData(){
        val gridView = requireActivity().findViewById(R.id.grid_id) as GridView
        val customAdapter = MenulerGridAdapter(tumMenuler,false)
        gridView.adapter = customAdapter

        userProfilKampanyalarViewModeli.profilKampanya.observe(viewLifecycleOwner, androidx.lifecycle.Observer { profilKampanya->
            profilKampanya.let {

                Collections.sort(profilKampanya,object : Comparator<KullaniciKampanya> {
                    override fun compare(p0: KullaniciKampanya?, p1: KullaniciKampanya?): Int {
                        if (p0!!.postYuklenmeTarih!!>p1!!.postYuklenmeTarih!!){
                            return -1
                        }else return 1
                    }
                })
                val layoutManager= LinearLayoutManager(activity)
                binding.recyclerProfil.layoutManager=layoutManager
                recyclerviewadapter= UserProfilRecyclerAdapter(requireActivity(),tumGonderiler)
                binding.recyclerProfil.adapter=recyclerviewadapter
                binding.recyclerProfil.visibility = View.VISIBLE
                binding.gridId.visibility=View.GONE
                recyclerviewadapter.kampanyalariGuncelle(profilKampanya)
                binding.paylasimlar.text = "Paylaşımlar \n[${profilKampanya.size}]"
            }
        })
        userProfilKampanyalarViewModeli.profilMenu.observe(viewLifecycleOwner) { profilMenu ->
            profilMenu.let {
                if (profilMenu!=null){
                    binding.gridId.visibility = View.VISIBLE
                    customAdapter.menuleriGuncelle(profilMenu)
                    binding.recyclerProfil.visibility=View.GONE
                    binding.gonderiYok.visibility=View.GONE
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
        userProfilKampanyalarViewModeli.menuSayisiMutable.observe(viewLifecycleOwner) { profilMenu ->
            profilMenu.let {
                if (profilMenu!=null){
                    binding.menu.text = "Menu\n${profilMenu}"
                }
            }
        }
        userProfilKampanyalarViewModeli.mudavimSayisiMutableLiveData.observe(viewLifecycleOwner){ Mudavimler->
            Mudavimler.let {
                if (Mudavimler!=null){
                    binding.mudavimler.text="Müdavim \n${Mudavimler}"
                }
            }
        }
        userProfilKampanyalarViewModeli.mudavimlerMutableLiveData.observe(viewLifecycleOwner){ Mudavimler->
            Mudavimler.let {
                if (Mudavimler!=null){

                    var isMudavim = false
                    for (mudavim in Mudavimler) {
                        if (auth.currentUser != null && auth.currentUser!!.uid == mudavim.mudavim_id) {
                            isMudavim = true
                            break
                        }
                    }
                    if (isMudavim) {
                        binding.mudavimOl.setText("Takibi Bırak")
                    } else {

                        binding.mudavimOl.setText("Müdavim Ol")
                    }
                }
            }
        }
        userProfilKampanyalarViewModeli.gonderiYok.observe(viewLifecycleOwner){
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
        userProfilKampanyalarViewModeli.yukleniyor.observe(viewLifecycleOwner){
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
    }
}