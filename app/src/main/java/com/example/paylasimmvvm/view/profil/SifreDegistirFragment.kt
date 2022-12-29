package com.example.paylasimmvvm.view.profil

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentSifreDegistirBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth


class SifreDegistirFragment : Fragment() {
    private lateinit var binding:FragmentSifreDegistirBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSifreDegistirBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment


        binding.sifreBack.setOnClickListener{
           findNavController().navigateUp()
        }


        binding.sifreOnay.setOnClickListener {

            var mevcutSifre=binding.mevcutSifreId.text.toString()
            var yeniSifre=binding.yeniSifreId.text.toString()
            var yeniSifreTekrar=binding.tekrarSifreId.text.toString()


            if (mevcutSifre.length>=6 && mevcutSifre.isNotEmpty()){

                var auth= FirebaseAuth.getInstance().currentUser

                if (auth!=null){
                    var credential=
                        EmailAuthProvider.getCredential(auth.email.toString(),mevcutSifre)
                    auth.reauthenticate(credential).addOnCompleteListener(object :
                        OnCompleteListener<Void> {
                        override fun onComplete(p0: Task<Void>) {
                            if (p0.isSuccessful){

                                if (yeniSifre.equals(yeniSifreTekrar)){
                                    if (yeniSifre.length>=6&&yeniSifre.isNotEmpty()){
                                        if (!mevcutSifre.equals(yeniSifre)){
                                            auth.updatePassword(yeniSifre).addOnCompleteListener(object :
                                                OnCompleteListener<Void> {
                                                override fun onComplete(p0: Task<Void>) {
                                                    if (p0.isSuccessful){
                                                        Toast.makeText(requireActivity(),"Şifre güncellendi.",
                                                            Toast.LENGTH_LONG).show()

                                                        findNavController().navigate(R.id.homeFragment)





                                                    }else{
                                                        Toast.makeText(requireActivity(),"Şifre güncellenemedi,Lütfen daha sonra tekrar deneyin.",
                                                            Toast.LENGTH_LONG).show()


                                                    }
                                                }

                                            })

                                        }else{
                                            Toast.makeText(requireActivity(),"Yeni şifre eski şifreyle aynı olamaz.",
                                                Toast.LENGTH_LONG).show()


                                        }



                                    }else{
                                        Toast.makeText(requireActivity(),"Yeni şifre en az 6 karakter olmalıdır.",
                                            Toast.LENGTH_LONG).show()


                                    }

                                }else{

                                    Toast.makeText(requireActivity(),"şifreler eşleşmiyor.", Toast.LENGTH_LONG).show()


                                }

                            }else{
                                Toast.makeText(requireActivity(),"Mevcut şifre yanlış.", Toast.LENGTH_LONG).show()


                            }
                        }

                    })
                }




            }else{
                Toast.makeText(requireActivity(),"Mevcut şifre en az 6 karakter olmalıdır.", Toast.LENGTH_LONG).show()
            }
        }




        return binding.root
    }


}