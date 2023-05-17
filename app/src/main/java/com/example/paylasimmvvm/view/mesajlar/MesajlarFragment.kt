package com.example.paylasimmvvm.view.mesajlar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.MesajlarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentMesajlarBinding
import com.example.paylasimmvvm.model.Mesajlar
import com.example.paylasimmvvm.util.setBadge
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
import com.example.paylasimmvvm.viewmodel.MesajlarViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class MesajlarFragment : Fragment() {
    private lateinit var binding: FragmentMesajlarBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    private lateinit var recyclerAdapter:MesajlarRecyclerAdapter
    private lateinit var mesajlarViewModeli:MesajlarViewModel
    var tumMesajlar=ArrayList<Mesajlar>()
    private lateinit var badgeViewModeli: BadgeViewModel
    var listenerAtandiMi=false

    companion object {
        var fragmentAcikMi=false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMesajlarBinding.inflate(layoutInflater,container,false)
        val view=binding.root


        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        setupAuthLis()


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("listeneratandımı","oncreate"+listenerAtandiMi)

        if(listenerAtandiMi==false){
            listenerAtandiMi=true
            Log.e("listeneratandımı","oncreateifsonrası"+listenerAtandiMi)

            mesajlarViewModeli= ViewModelProvider(this)[MesajlarViewModel::class.java]

            mesajlarViewModeli.refreshMesajlar()

            badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]
            badgeViewModeli.refreshBadge()

            observeLiveData()

        }






        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerMesajlar.layoutManager=layoutManager
        recyclerAdapter= MesajlarRecyclerAdapter(tumMesajlar)
        binding.recyclerMesajlar.adapter=recyclerAdapter

    }

  private  fun observeLiveData(){
        mesajlarViewModeli.mesajlarMutable.observe(viewLifecycleOwner) { Mesajlar ->
            Mesajlar.let {

                binding.recyclerMesajlar.visibility = View.VISIBLE
                if (Mesajlar != null) {


                    recyclerAdapter.mesajlariGuncelle(Mesajlar)
                }

            }

        }

      mesajlarViewModeli.mesajYok.observe(viewLifecycleOwner) {
          it.let {
              if (it) {
                  binding.mesajYok.visibility = View.VISIBLE
                  binding.recyclerMesajlar.visibility = View.GONE
              } else {
                  binding.mesajYok.visibility = View.GONE
              }
          }
      }

      mesajlarViewModeli.yukleniyor.observe(viewLifecycleOwner) {
            it.let {
                if (it) {
                    binding.progressBarMesajlar.visibility = View.VISIBLE
                    binding.recyclerMesajlar.visibility=View.GONE
                }else{
                    binding.progressBarMesajlar.visibility=View.GONE
                }
            }
        }
      badgeViewModeli.badgeLive.observe(viewLifecycleOwner) {gorulmeyenMesajSayisi ->
          gorulmeyenMesajSayisi.let {

              val navView: BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
              if (gorulmeyenMesajSayisi != null) {
                  navView.setBadge(R.id.mesajlarFragment, gorulmeyenMesajSayisi.size)


              }

          }

      }
    }


    private fun setupAuthLis() {


        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser

            if (user==null){
                findNavController().popBackStack(com.example.paylasimmvvm.R.id.mesajlarFragment,true)


                findNavController().navigate(com.example.paylasimmvvm.R.id.loginFragment)


            }
        }

    }


    override fun onStart() {
        Log.e("startmesajlar","")
        fragmentAcikMi=true
        super.onStart()


        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        Log.e("stopmesajlar","")
        fragmentAcikMi=false

        super.onStop()
        Log.e("lisetneratandımı","onstop"+listenerAtandiMi)


        auth.removeAuthStateListener(mauthLis)

    }

    override fun onResume() {
        Log.e("resumeesajlar","")


        fragmentAcikMi=true
        Log.e("listeneratandımı","onresume"+listenerAtandiMi)

        if(listenerAtandiMi==false){
            tumMesajlar.clear()
            listenerAtandiMi=true
            recyclerAdapter.notifyDataSetChanged()
            Log.e("listeneratandımıı","onresumeifsonbras"+listenerAtandiMi)
            mesajlarViewModeli.refreshMesajlar()
        }
        super.onResume()


    }

    override fun onPause() {
        Log.e("pausemesajlar","")
        fragmentAcikMi=false
        super.onPause()


        Log.e("lisetneratandımıonpause",""+listenerAtandiMi)
        if(listenerAtandiMi==true){
            tumMesajlar.clear()
            Log.e("lisetneratandımı","onpauseifsobrası"+listenerAtandiMi)

            listenerAtandiMi=false
            mesajlarViewModeli.removeListeners()
        }


    }
    override fun onDestroy() {
        super.onDestroy()
        Log.e("ondestroymesajlar","")
    }




}