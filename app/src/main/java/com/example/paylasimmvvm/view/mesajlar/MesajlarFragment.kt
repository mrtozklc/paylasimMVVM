package com.example.paylasimmvvm.view.mesajlar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentMesajlarBinding
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentMesajlarBinding.inflate(layoutInflater,container,false)
        val view=binding.root

        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        setupAuthLis()
        // Inflate the layout for this fragment
        return view
    }

    private fun setupAuthLis() {


        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {



                var user=FirebaseAuth.getInstance().currentUser
                Log.e("auth","auth çalıstı"+user)

                if (user==null){
                    Navigation.findNavController(requireView()).popBackStack(R.id.kampanyaOlusturFragment,true)

                    findNavController().navigate(R.id.loginFragment)


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