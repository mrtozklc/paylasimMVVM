package com.example.paylasimmvvm.view.bildirimler

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.BildirimlerRecyclerAdapter
import com.example.paylasimmvvm.adapter.ProfilFragmentRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentBildirimlerBinding
import com.example.paylasimmvvm.model.BildirimModel
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.view.login.LoginFragment
import com.example.paylasimmvvm.viewmodel.BildirimlerViewModel
import com.example.paylasimmvvm.viewmodel.ProfilViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class BildirimlerFragment : Fragment() {
    lateinit var binding:FragmentBildirimlerBinding
    private lateinit var auth : FirebaseAuth
    lateinit var recyclerviewadapter:BildirimlerRecyclerAdapter
    lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    var tumBildirimler=ArrayList<BildirimModel>()
    private lateinit var bildirimlerViewModeli:BildirimlerViewModel






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         binding=FragmentBildirimlerBinding.inflate(layoutInflater,container,false)
        val view=binding.root

        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference
        setupAuthLis()



        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bildirimlerViewModeli= ViewModelProvider(this).get(BildirimlerViewModel::class.java)
        bildirimlerViewModeli.refreshBildirimler()


        observeliveData()



        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerBildirim.layoutManager=layoutManager
        recyclerviewadapter= BildirimlerRecyclerAdapter(tumBildirimler)
        binding.recyclerBildirim.adapter=recyclerviewadapter

        binding.refreshId.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                tumBildirimler.clear()
                recyclerviewadapter= BildirimlerRecyclerAdapter(tumBildirimler)
                bildirimlerViewModeli.refreshBildirimler()

                binding.refreshId.isRefreshing=false
            }

        })

    }
    fun observeliveData(){


        bildirimlerViewModeli.tumBildirimlerLive.observe(viewLifecycleOwner) { bildirimler ->
            bildirimler.let {

                binding.recyclerBildirim.visibility = View.VISIBLE
                recyclerviewadapter!!.kampanyalariGuncelle(bildirimler)
            }

        }
        bildirimlerViewModeli.bildirimYok.observe(viewLifecycleOwner) { bildirimYok->
            bildirimYok.let {
                if (it){
                    binding.bildirimYok.visibility=View.VISIBLE
                    binding.recyclerBildirim.visibility=View.GONE

                }else{
                    binding.bildirimYok.visibility=View.GONE

                }

            }

        }
        bildirimlerViewModeli.yukleniyor.observe(viewLifecycleOwner) { yukleniyor->
            yukleniyor.let {
                if (it){
                    binding.progressBarBildirim.visibility=View.VISIBLE
                    binding.bildirimYok.visibility=View.GONE
                    binding.recyclerBildirim.visibility=View.GONE

                }else{
                    binding.progressBarBildirim.visibility=View.GONE

                }

            }

        }

    }

    private fun setupAuthLis() {


        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {



                var user=FirebaseAuth.getInstance().currentUser
                Log.e("auth","auth çalıstı"+user)

                if (user==null){

                    findNavController().popBackStack(R.id.bildirimlerFragment,true)

                    findNavController().navigate(R.id.loginFragment)


                }

            }

        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","bildirimlerdesin")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }







}