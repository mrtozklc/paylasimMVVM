package com.example.paylasimmvvm.profileEdit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentSifreDegistirBinding

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class SifreDegistirFragment : Fragment() {
    private lateinit var binding:FragmentSifreDegistirBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSifreDegistirBinding.inflate(layoutInflater, container, false)


        binding.sifreBack.setOnClickListener{
           findNavController().navigateUp()
        }

        binding.sifreOnay.setOnClickListener {
            val mevcutSifre=binding.mevcutSifreId.text.toString()
            val yeniSifre=binding.yeniSifreId.text.toString()
            val yeniSifreTekrar=binding.tekrarSifreId.text.toString()
            if (mevcutSifre.length>=6 && mevcutSifre.isNotEmpty()){
                val auth= FirebaseAuth.getInstance().currentUser

                if (auth!=null){
                    val credential=
                        EmailAuthProvider.getCredential(auth.email.toString(),mevcutSifre)
                    auth.reauthenticate(credential).addOnCompleteListener { it ->
                        if (it.isSuccessful) {

                            if (yeniSifre == yeniSifreTekrar) {
                                if (yeniSifre.length >= 6 && yeniSifre.isNotEmpty()) {
                                    if (mevcutSifre != yeniSifre) {
                                        auth.updatePassword(yeniSifre)
                                            .addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    Toast.makeText(requireActivity(), "Şifre güncellendi.", Toast.LENGTH_LONG).show()
                                                    findNavController().navigate(R.id.homeFragment)
                                                } else {
                                                    Toast.makeText(requireActivity(), "Şifre güncellenemedi,Lütfen daha sonra tekrar deneyin.", Toast.LENGTH_LONG).show()
                                                }
                                            }
                                    } else {
                                        Toast.makeText(requireActivity(), "Yeni şifre eski şifreyle aynı olamaz.", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        requireActivity(), "Yeni şifre en az 6 karakter olmalıdır.", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(requireActivity(), "şifreler eşleşmiyor.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(requireActivity(), "Mevcut şifre yanlış.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }else{
                Toast.makeText(requireActivity(),"Mevcut şifre en az 6 karakter olmalıdır.", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()

    }


}