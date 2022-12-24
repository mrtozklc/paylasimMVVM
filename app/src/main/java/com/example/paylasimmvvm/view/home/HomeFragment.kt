package com.example.paylasimmvvm.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentHomeBinding
import com.example.paylasimmvvm.adapter.HomeFragmentRecyclerAdapter
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.setBadge
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
import com.example.paylasimmvvm.viewmodel.kampanyalarViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*


class HomeFragment : Fragment() {
    lateinit var binding:FragmentHomeBinding
    private lateinit var auth : FirebaseAuth
 //  lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    private lateinit var kampanyalarViewModeli:kampanyalarViewModel
    private lateinit var badgeViewModeli: BadgeViewModel
    private lateinit var recyclerviewadapter:HomeFragmentRecyclerAdapter
    var tumGonderiler=ArrayList<KullaniciKampanya>()
    var sayfaBasiGonderiler=ArrayList<KullaniciKampanya>()
    var tumPostlar=ArrayList<String>()
    private val SAYFA_BASI_GONDERI=10
    private var sayfaSayisi=1
    var sayfaninSonunaGelindi = false



    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

         binding= FragmentHomeBinding.inflate(layoutInflater,container,false)
        val view =binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

      //  setupAuthLis()



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       kampanyalarViewModeli= ViewModelProvider(this)[kampanyalarViewModel::class.java]
        kampanyalarViewModeli.refreshKampanyalar()

        badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]
        badgeViewModeli.refreshBadge()

        observeLiveDataBadge()




        observeliveData()
        setUpRecyclerview()




    }
    private fun observeliveData(){


        kampanyalarViewModeli.kampanyalar.observe(viewLifecycleOwner) { kampanyalar ->
            kampanyalar.let {

                Collections.sort(kampanyalar
                ) { p0, p1 ->
                    if (p0!!.postYuklenmeTarih!! > p1!!.postYuklenmeTarih!!) {
                        -1
                    } else 1
                }

                if (tumGonderiler.size >= SAYFA_BASI_GONDERI) {


                    for (i in 0 until SAYFA_BASI_GONDERI) {
                        sayfaBasiGonderiler.add(tumGonderiler[i])

                    }
                } else {
                    for (i in 0 until tumGonderiler.size) {
                        sayfaBasiGonderiler.add(tumGonderiler[i])


                    }
                }

                binding.recyclerAnaSayfa.visibility = View.VISIBLE
                binding.kampanyaYok.visibility=View.VISIBLE
                recyclerviewadapter.kampanyalariGuncelle(kampanyalar)
            }

        }

        kampanyalarViewModeli.kampanyaYok.observe(viewLifecycleOwner) { kampanyaYok ->
            kampanyaYok.let {
                if (it) {
                    binding.kampanyaYok.visibility = View.VISIBLE
                    binding.recyclerAnaSayfa.visibility = View.GONE

                } else {
                    binding.kampanyaYok.visibility = View.GONE

                }

            }

        }
        kampanyalarViewModeli.yukleniyor.observe(viewLifecycleOwner) { yukleniyor ->
            yukleniyor.let {
                if (it) {
                    binding.progressBar8.visibility = View.VISIBLE
                    binding.kampanyaYok.visibility = View.GONE
                    binding.recyclerAnaSayfa.visibility = View.GONE

                } else {
                    binding.progressBar8.visibility = View.GONE

                }

            }

        }
    }

    private  fun observeLiveDataBadge(){
        badgeViewModeli.badgeLive.observe(viewLifecycleOwner) {gorulmeyenMesajSayisi ->
            gorulmeyenMesajSayisi.let {

                val navView:BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
                if (gorulmeyenMesajSayisi != null) {
                    navView.setBadge(R.id.mesajlarFragment, gorulmeyenMesajSayisi.size)

                }

            }

        }

    }




    private fun setUpRecyclerview(){




        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerAnaSayfa.layoutManager=layoutManager
        recyclerviewadapter= HomeFragmentRecyclerAdapter(requireActivity(),sayfaBasiGonderiler)
        binding.recyclerAnaSayfa.adapter=recyclerviewadapter




        binding.recyclerAnaSayfa.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)


                if (dy > 0 && layoutManager.findLastVisibleItemPosition() ==  binding.recyclerAnaSayfa.adapter!!.itemCount - 1) {

                    if (!sayfaninSonunaGelindi)
                        listeyeYeniElemanlariEkle()
                }


            }
        })

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun listeyeYeniElemanlariEkle() {

        val yeniGetirilecekElemanlarinAltSiniri = sayfaSayisi * SAYFA_BASI_GONDERI
        val yeniGetirilecekElemanlarinUstSiniri = (sayfaSayisi +1) * SAYFA_BASI_GONDERI - 1
        for (i in yeniGetirilecekElemanlarinAltSiniri..yeniGetirilecekElemanlarinUstSiniri) {
            if (sayfaBasiGonderiler.size <= tumGonderiler.size - 1) {
                sayfaBasiGonderiler.add(tumGonderiler[i])
                binding.recyclerAnaSayfa.adapter!!.notifyDataSetChanged()
            } else {
                sayfaninSonunaGelindi = true
                sayfaSayisi = 0
                break
            }

        }
        Log.e("XXX",
            "$yeniGetirilecekElemanlarinAltSiniri dan $yeniGetirilecekElemanlarinUstSiniri kadar eleman eklendi"
        )
        sayfaSayisi++


    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","homedasın")
       // auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
       // auth.removeAuthStateListener(mauthLis)
    }



}