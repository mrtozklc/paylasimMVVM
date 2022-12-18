package com.example.paylasimmvvm.view.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentLoginBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.view.home.HomeFragment
import com.example.paylasimmvvm.view.home.HomeFragmentDirections
import com.example.paylasimmvvm.view.home.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    lateinit var mauthLis: FirebaseAuth.AuthStateListener




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= FragmentLoginBinding.inflate(layoutInflater,container,false)
        var view=binding.root
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
    fun init() {
        setupAuthLis()
        binding.tvLogin.addTextChangedListener(watcher)
        binding.tvSifre.addTextChangedListener(watcher)

        binding.btnLogin.setOnClickListener {
            girisYapacakKullanici(binding.tvLogin.text.toString(),binding. tvSifre.text.toString())
        }

    }
    private fun setupAuthLis() {


        mauthLis=object :FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {



                var user=FirebaseAuth.getInstance().currentUser
                Log.e("auth","loginmauth çalıstı"+user)

                if (user!=null){
                    findNavController().navigate(R.id.homeFragment)

                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNav.visibility=View.VISIBLE


                }else{
                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                    bottomNav.visibility=View.GONE

                }

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

                if(snapshot!!.getValue() != null ){
                    for (ds in snapshot!!.children) {

                        var okunanKullanici = ds.getValue(KullaniciBilgileri::class.java)

                        if (!okunanKullanici!!.email!!.isNullOrEmpty() && okunanKullanici!!.email!!.toString().equals(emailPhoneNumberUserName)) {

                            oturumAc(okunanKullanici, sifre)
                            kullaniciBulundu=true
                            break

                        } else if (!okunanKullanici!!.user_name!!.isNullOrEmpty() && okunanKullanici!!.user_name!!.toString().equals(emailPhoneNumberUserName)) {
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

                        if(snapshot!!.getValue() != null ){
                            for (ds in snapshot!!.children) {

                                var okunanKullanici = ds.getValue(KullaniciBilgileri::class.java)

                                if (!okunanKullanici!!.email!!.isNullOrEmpty() && okunanKullanici!!.email!!.toString().equals(emailPhoneNumberUserName)) {

                                    oturumAc(okunanKullanici, sifre)
                                    kullaniciBulundu=true
                                    break

                                } else if (!okunanKullanici!!.user_name!!.isNullOrEmpty() && okunanKullanici!!.user_name!!.toString().equals(emailPhoneNumberUserName)) {
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

    private fun oturumAc(okunanKullanici: KullaniciBilgileri, sifre: String) {

        var girisYapacakEmail = ""


        girisYapacakEmail = okunanKullanici.email.toString()


        auth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {


                        // fcmTokenAl()
                         Toast.makeText( activity,"Hoşgeldiniz:" + okunanKullanici.user_name, Toast.LENGTH_LONG).show()

                        findNavController().popBackStack(R.id.loginFragment,true)
                        findNavController().navigate(R.id.homeFragment)
                        Log.e("bulunanKullanici",""+okunanKullanici.user_name)
                         val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                         bottomNav.visibility=View.VISIBLE


                    } else {

                        Toast.makeText(
                            activity,"Kullanıcı Adı/Şifre hatalı", Toast.LENGTH_LONG).show()

                    }
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

            FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot!!.getValue() != null) {

                        for (user in snapshot!!.children) {


                            var okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                            if (okunanKullanici!!.user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {

                                FirebaseDatabase.getInstance().getReference().child("users").child("isletmeler").child(FirebaseAuth.getInstance().currentUser!!.uid).child("FCM_TOKEN").setValue(newToken)



                            }
                        }
                    }
                    FirebaseDatabase.getInstance().getReference().child("users").child("kullanicilar").addListenerForSingleValueEvent(object:
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot!!.getValue() != null) {

                                for (user in snapshot!!.children) {


                                    var okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                    Log.e("newtoken","okunankullanici"+okunanKullanici)

                                    if (okunanKullanici!!.user_id!!.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {

                                        FirebaseDatabase.getInstance().getReference().child("users").child("kullanicilar").child(FirebaseAuth.getInstance().currentUser!!.uid).child("FCM_TOKEN").setValue(newToken)



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

    var watcher: TextWatcher = object : TextWatcher {
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