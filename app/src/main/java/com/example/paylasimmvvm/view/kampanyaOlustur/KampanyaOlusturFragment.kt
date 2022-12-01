package com.example.paylasimmvvm.view.kampanyaOlustur

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentKampanyaOlusturBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


class KampanyaOlusturFragment : Fragment() {
    private lateinit var binding: FragmentKampanyaOlusturBinding
    lateinit var mauthLis:FirebaseAuth.AuthStateListener
    private lateinit var auth : FirebaseAuth
    lateinit var mref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentKampanyaOlusturBinding.inflate(layoutInflater,container,false)
        val view=binding.root

        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        setupAuthLis()

        return inflater.inflate(R.layout.fragment_kampanya_olustur, container, false)
    }

    private fun setupAuthLis() {


        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {



                var user=FirebaseAuth.getInstance().currentUser
                Log.e("auth","auth çalıstı"+user)

                if (user==null){
                    Navigation.findNavController(requireView()).popBackStack(R.id.homeFragment,true)

                  findNavController().navigate(R.id.loginFragment)


                }

            }

        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","kampanyaolusturdasın")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }


}