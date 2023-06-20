package com.example.paylasimmvvm.notifications

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
import com.example.paylasimmvvm.databinding.FragmentBildirimlerBinding
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
    private lateinit var recyclerviewadapter: BildirimlerRecyclerAdapter
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    private var tumBildirimler=ArrayList<BildirimModel>()
    private lateinit var bildirimlerViewModeli: BildirimlerViewModel







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

        bildirimlerViewModeli = ViewModelProvider(this)[BildirimlerViewModel::class.java]
        bildirimlerViewModeli.refreshBildirimler()
        observeliveData()



        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerBildirim.layoutManager = layoutManager
        recyclerviewadapter = BildirimlerRecyclerAdapter(tumBildirimler)
        binding.recyclerBildirim.adapter = recyclerviewadapter




        recyclerviewadapter.setOnAllItemsDeletedListener {
            bildirimlerViewModeli.refreshBildirimler()
        }


        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun observeliveData(){


        bildirimlerViewModeli.tumBildirimlerLive.observe(viewLifecycleOwner) { bildirimler ->
            bildirimler.let {
                Collections.sort(bildirimler
                ) { p0, p1 ->
                    if (p0!!.time!! > p1!!.time!!) {
                        -1
                    } else 1
                }

                binding.recyclerBildirim.visibility = View.VISIBLE
                recyclerviewadapter.bildirimleriGuncelle(bildirimler)
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


        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser

            if (user==null){

                findNavController().popBackStack(R.id.bildirimlerFragment,true)

                findNavController().navigate(R.id.loginFragment)


            }
        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","bildirimlerdesin")
        auth.addAuthStateListener(mauthLis)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
        (activity as AppCompatActivity).supportActionBar?.show()
    }










}