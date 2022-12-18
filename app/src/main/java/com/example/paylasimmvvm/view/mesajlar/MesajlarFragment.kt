package com.example.paylasimmvvm.view.mesajlar

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.adapter.MesajlarRecyclerAdapter
import com.example.paylasimmvvm.adapter.ProfilFragmentRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentMesajlarBinding
import com.example.paylasimmvvm.model.Mesajlar
import com.example.paylasimmvvm.viewmodel.MesajlarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class MesajlarFragment : Fragment() {
    private lateinit var binding: FragmentMesajlarBinding
    private lateinit var auth : FirebaseAuth
    lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    private lateinit var recyclerAdapter:MesajlarRecyclerAdapter
    private lateinit var mesajlarViewModeli:MesajlarViewModel
    var tumMesajlar=ArrayList<Mesajlar>()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMesajlarBinding.inflate(layoutInflater,container,false)
        val view=binding.root

        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        setupAuthLis()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mesajlarViewModeli=ViewModelProvider(this).get(MesajlarViewModel::class.java)
        mesajlarViewModeli.refreshMesajlar()

        observeLiveData()

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerMesajlar.layoutManager=layoutManager
        recyclerAdapter= MesajlarRecyclerAdapter(tumMesajlar)
        binding.recyclerMesajlar.adapter=recyclerAdapter

    }

    fun observeLiveData(){
        mesajlarViewModeli.mesajlarMutable.observe(viewLifecycleOwner, Observer { Mesajlar->
            Mesajlar.let {

                binding.recyclerMesajlar.visibility = View.VISIBLE
                recyclerAdapter!!.mesajlariGuncelle(Mesajlar)

            }

        })

        mesajlarViewModeli.mesajYok.observe(viewLifecycleOwner, Observer {
            it.let {
                if(it){
                    binding.mesajYok.visibility=View.VISIBLE
                    binding.recyclerMesajlar.visibility=View.GONE
                }
                else{
                    binding.mesajYok.visibility=View.GONE
                }
            }
        })

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
    }


    private fun setupAuthLis() {


        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {



                var user=FirebaseAuth.getInstance().currentUser
                Log.e("auth","auth çalıstı"+user)

                if (user==null){
                    findNavController().popBackStack(com.example.paylasimmvvm.R.id.mesajlarFragment,true)


                    findNavController().navigate(com.example.paylasimmvvm.R.id.loginFragment)


                }

            }

        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","mesajlardasın")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }

}