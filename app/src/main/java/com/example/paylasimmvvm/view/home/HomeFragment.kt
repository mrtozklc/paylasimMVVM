package com.example.paylasimmvvm.view.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentHostCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentHomeBinding
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.HomeFragmentRecyclerAdapter
import com.example.paylasimmvvm.view.login.LoginFragment
import com.example.paylasimmvvm.view.login.LoginFragmentDirections
import com.example.paylasimmvvm.viewmodel.kampanyalarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {
    private lateinit var binding:FragmentHomeBinding
    private lateinit var auth : FirebaseAuth
    lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    private lateinit var kampanyalarViewModeli:kampanyalarViewModel
    private  var recyclerviewadapter=HomeFragmentRecyclerAdapter(arrayListOf())








    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)




    }


    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

         binding= FragmentHomeBinding.inflate(layoutInflater,container,false)
        val view =binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        setupAuthLis()



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


       kampanyalarViewModeli=ViewModelProvider(this).get(kampanyalarViewModel::class.java)



        binding.recyclerAnaSayfa.layoutManager=LinearLayoutManager(context)

        binding.recyclerAnaSayfa.adapter=recyclerviewadapter

        observeliveData()






    }
    fun observeliveData(){

        kampanyalarViewModeli.kampanyalar.observe(viewLifecycleOwner) { kampanyalar ->
            kampanyalar.let {
                binding.recyclerAnaSayfa.visibility = View.VISIBLE
                binding.kampanyaYok.visibility=View.VISIBLE
                recyclerviewadapter.kampanyalariGuncelle(kampanyalar)
            }

        }

        kampanyalarViewModeli.kampanyaYok.observe(viewLifecycleOwner, Observer { kampanyaYok->
            kampanyaYok.let {
                if (it){
                    binding.kampanyaYok.visibility=View.VISIBLE

                }else{
                    binding.kampanyaYok.visibility=View.GONE

                }

            }

        })
        kampanyalarViewModeli.yukleniyor.observe(viewLifecycleOwner, Observer { yukleniyor->
            yukleniyor.let {
                if (it){
                    binding.progressBar8.visibility=View.VISIBLE
                    binding.kampanyaYok.visibility=View.GONE
                    binding.recyclerAnaSayfa.visibility=View.GONE

                }else{
                    binding.progressBar8.visibility=View.GONE

                }

            }

        })
    }


    private fun setupAuthLis() {


        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {



                var user=FirebaseAuth.getInstance().currentUser
                Log.e("auth","auth çalıstı"+user)

                if (user==null){
                    Navigation.findNavController(requireView()).popBackStack(R.id.bildirimlerFragment,true)
                    findNavController().navigate(R.id.loginFragment)


                }

            }

        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","homedasın")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }



}