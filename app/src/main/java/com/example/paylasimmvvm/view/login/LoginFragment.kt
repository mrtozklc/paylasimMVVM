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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentLoginBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding= FragmentLoginBinding.inflate(layoutInflater,container,false)
        val view=binding.root
        binding.progressBarLogin.visibility=View.VISIBLE
        binding.loginContainer.visibility=View.GONE
        auth = FirebaseAuth.getInstance()
        mref = FirebaseDatabase.getInstance().reference



        init()
        setupAuthLis()

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

        binding.tvLogin.addTextChangedListener(watcher)
        binding.tvSifre.addTextChangedListener(watcher)

        binding.btnLogin.setOnClickListener {
            girisYapacakKullanici(binding.tvLogin.text.toString(),binding. tvSifre.text.toString())
        }

    }
    private fun setupAuthLis() {




        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser?.uid


            if (user!=null){


                findNavController().navigate(R.id.homeFragment)


                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.visibility=View.VISIBLE
                binding.progressBarLogin.visibility=View.GONE



            }else{

                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.visibility=View.GONE
                binding.progressBarLogin.visibility=View.GONE
                binding.loginContainer.visibility=View.VISIBLE


            }
        }

    }



    private fun oturumAc(okunanKullanici: KullaniciBilgileri, sifre: String) {


        val girisYapacakEmail: String = okunanKullanici.email.toString()


        auth.signInWithEmailAndPassword(girisYapacakEmail, sifre)
            .addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    val navController = findNavController()

                    fcmTokenAl()

                    Toast.makeText(
                        requireContext(),
                        "Hoşgeldiniz \n" + okunanKullanici.user_name,
                        Toast.LENGTH_LONG
                    ).show()
                    navController.navigate(R.id.homeFragment)

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



       private fun fcmTokenAl() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            var token=task.result

            newTokenAl(token)

        })
    }



    private fun newTokenAl(newToken: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val database = FirebaseDatabase.getInstance().reference
            val usersRef = database.child("users")
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        for (userSnapshot in childSnapshot.children) {

                            if (userSnapshot.child("user_id").value == currentUser.uid) {
                                val userRef = userSnapshot.ref
                                userRef.child("FCM_TOKEN").setValue(newToken)
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    databaseError.toException().printStackTrace()
                }
            })
        }
    }
    private var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            if (binding.tvLogin.text.toString().length >= 0 && binding.tvSifre.text.toString().length >= 6) {
                binding. btnLogin.isEnabled = true


            } else {
                binding.btnLogin.isEnabled = false



            }
        }


        override fun afterTextChanged(p0: Editable?) {
        }

    }

    override fun onStart() {
        super.onStart()
        Log.e("hata","logindesin")
        auth.addAuthStateListener(mauthLis)
        (activity as AppCompatActivity).supportActionBar?.hide()


    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }







}