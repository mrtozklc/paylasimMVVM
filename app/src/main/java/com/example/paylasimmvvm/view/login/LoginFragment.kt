package com.example.paylasimmvvm.view.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentLoginBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentLoginBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        auth = FirebaseAuth.getInstance()
        mref = FirebaseDatabase.getInstance().reference


        init()

        return view


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding. tvKaydoll.setOnClickListener {

           findNavController().navigate(R.id.registerFragment)

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNav.visibility=View.GONE
       }
    }
    private fun init() {
        setupAuthLis()
        binding.tvLogin.addTextChangedListener(watcher)
        binding.tvSifre.addTextChangedListener(watcher)

        binding.btnLogin.setOnClickListener {
            girisYapacakKullanici(binding.tvLogin.text.toString(),binding. tvSifre.text.toString())
        }

    }
    private fun setupAuthLis() {


        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser
            Log.e("auth", "loginmauth çalıstı$user")

            if (user!=null){
                Log.e("auth", "loginmauth çalıstıboş değil")


                findNavController().navigate(R.id.homeFragment)


                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.visibility=View.VISIBLE


            }else{
                Log.e("auth", "loginmauth çalıstıboş ")
                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.visibility=View.GONE


            }
        }

    }



    private fun oturumAc(okunanKullanici: KullaniciBilgileri, sifre: String) {


        val girisYapacakEmail: String = okunanKullanici.email.toString()


        auth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val navController = findNavController()

                    // fcmTokenAl()

                    Toast.makeText(
                        requireContext(),
                        "Hoşgeldiniz:" + okunanKullanici.user_name,
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(R.id.homeFragment)
                    Log.e("bulunanKullanici", "" + okunanKullanici.user_name)
                    val bottomNav =
                        requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNav.visibility = View.VISIBLE


                } else {

                    Toast.makeText(
                        activity, "Kullanıcı Adı/Şifre hatalı", Toast.LENGTH_LONG
                    ).show()

                }
            }

    }
    private fun girisYapacakKullanici(emailPhoneNumberUserName: String, sifre: String) {

        var kullaniciBulundu=false

        mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.value != null ){
                    for (ds in snapshot.children) {

                        val okunanKullanici = ds.getValue(KullaniciBilgileri::class.java)

                        if (okunanKullanici!!.email!!.isNotEmpty() && okunanKullanici.email!!.toString() == emailPhoneNumberUserName) {

                            oturumAc(okunanKullanici, sifre)
                            kullaniciBulundu=true
                            break

                        } else if (okunanKullanici.user_name!!.isNotEmpty() && okunanKullanici.user_name!!.toString() == emailPhoneNumberUserName) {
                            oturumAc(okunanKullanici, sifre)
                            kullaniciBulundu=true
                            break
                        }
                    }
                }

                mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {

                    }

                    override fun onDataChange(snapshot: DataSnapshot) {

                        if(snapshot.value != null ){
                            for (ds in snapshot.children) {

                                val okunanKullanici = ds.getValue(KullaniciBilgileri::class.java)

                                if (okunanKullanici!!.email!!.isNotEmpty() && okunanKullanici.email!!.toString() == emailPhoneNumberUserName) {

                                    oturumAc(okunanKullanici, sifre)
                                    kullaniciBulundu=true
                                    break

                                } else if (okunanKullanici.user_name!!.isNotEmpty() && okunanKullanici.user_name!!.toString() == emailPhoneNumberUserName) {
                                    oturumAc(okunanKullanici, sifre)
                                    kullaniciBulundu=true
                                    break
                                }
                            }
                        }
                        if(!kullaniciBulundu){
                            Toast.makeText(activity,"Kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        })
    }



/*    private fun fcmTokenAl() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            var token=task.result

            newTokenAl(token)



        })
    }*/


    private fun newTokenAl(newToken: String) {

        if (FirebaseAuth.getInstance().currentUser!=null){

            FirebaseDatabase.getInstance().reference.child("users").child("isletmeler").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value != null) {

                        for (user in snapshot.children) {


                            val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                            if (okunanKullanici!!.user_id!! == FirebaseAuth.getInstance().currentUser!!.uid) {

                                FirebaseDatabase.getInstance().reference.child("users").child("isletmeler").child(FirebaseAuth.getInstance().currentUser!!.uid).child("FCM_TOKEN").setValue(newToken)



                            }
                        }
                    }
                    FirebaseDatabase.getInstance().reference.child("users").child("kullanicilar").addListenerForSingleValueEvent(object:
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.value != null) {

                                for (user in snapshot.children) {


                                    val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                    Log.e("newtoken", "okunankullanici$okunanKullanici")

                                    if (okunanKullanici!!.user_id!! == FirebaseAuth.getInstance().currentUser!!.uid) {

                                        FirebaseDatabase.getInstance().reference.child("users").child("kullanicilar").child(FirebaseAuth.getInstance().currentUser!!.uid).child("FCM_TOKEN").setValue(newToken)



                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }

    }

    private var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (binding.tvLogin.text.toString().length >= 6 && binding.tvSifre.text.toString().length >= 6) {
                binding. btnLogin.isEnabled = true
                binding. btnLogin.setTextColor(ContextCompat.getColor(activity!!,R.color.white))
                binding. btnLogin.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.teal_700
                    )
                )

            } else {
                binding.btnLogin.isEnabled = false
                binding. btnLogin.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.white
                    )
                )
                binding.btnLogin.setTextColor(ContextCompat.getColor(activity!!,R.color.black))


            }
        }


        override fun afterTextChanged(p0: Editable?) {
        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","logindesin")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }







}