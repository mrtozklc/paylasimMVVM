package com.example.paylasimmvvm.view.login

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentIsletmeKayitBilgileriBinding
import com.example.paylasimmvvm.model.KullaniciBilgiDetaylari
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.util.EventbusData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.io.IOException
import java.util.*

class IsletmeKayitBilgileriFragment : Fragment() {
    private lateinit var binding:FragmentIsletmeKayitBilgileriBinding
    var gelenEmail = ""
    var telNo = ""
    var verificationID = ""
    var sifre = ""
    var adress=""
    lateinit var auth: FirebaseAuth
    lateinit var mref: DatabaseReference
    var emailleKayit = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding= FragmentIsletmeKayitBilgileriBinding.inflate(layoutInflater,container,false)
        var view= binding.root



        auth = Firebase.auth
        mref = FirebaseDatabase.getInstance().reference


        if (auth.currentUser != null) {
            auth.signOut()
        }

        binding.tvKaydoll.setOnClickListener {
            val action=IsletmeKayitBilgileriFragmentDirections.actionIsletmeKayitBilgileriFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }

        binding.etAdSoyadISletme.addTextChangedListener(watcher)
        binding.etKullaniciAdiISletme.addTextChangedListener(watcher)
        binding.etSifreIsletme.addTextChangedListener(watcher)
        binding.etAdresIsletme.addTextChangedListener(watcher)
        binding.etTelefonIsletme.addTextChangedListener(watcher)


        binding.btnGirisISletme.setOnClickListener{
            if( binding.etKullaniciAdiISletme.text.toString().trim().length>5 &&  binding.etSifreIsletme.text.toString().trim().length>5 && ! binding.etAdSoyadISletme.text.toString().trim().isNullOrEmpty()){
                if ( binding.etTelefonIsletme.text.toString().trim().length==10){
                    var userNameKullanimdaMi = false
                    mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot!!.getValue() != null) {

                                for (user in snapshot!!.children) {
                                    var okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                    if (okunanKullanici!!.user_name!!.equals( binding.etKullaniciAdiISletme.text.toString())) {
                                        Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                        userNameKullanimdaMi = true
                                        break
                                    }
                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot!!.getValue()!=null){
                                                for (user in snapshot!!.children ){
                                                    var okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                                    if (okunanKullanici!!.user_name!!.equals( binding.etKullaniciAdiISletme.text.toString())) {
                                                        Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                                        userNameKullanimdaMi = true
                                                        break
                                                    }

                                                }
                                            }
                                            if (!userNameKullanimdaMi) {

                                                if (emailleKayit) {

                                                    var sifre =  binding.etSifreIsletme.text.toString()
                                                    var adSoyad =  binding.etAdSoyadISletme.text.toString()
                                                    var userName =  binding.etKullaniciAdiISletme.text.toString()
                                                    var adres= binding.etAdresIsletme.text.toString()
                                                    var telefon= binding.etTelefonIsletme.text.toString()




                                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                                        .addOnCompleteListener(object :
                                                            OnCompleteListener<AuthResult> {
                                                            override fun onComplete(p0: Task<AuthResult>) {

                                                                if (p0!!.isSuccessful) {

                                                                    var userID = auth.currentUser!!.uid.toString()

                                                                    getAddressFromLocation(adres,context,userID)


                                                                    var kaydedilecekKullaniciDetaylari=
                                                                        KullaniciBilgiDetaylari("0","","","",adres,null,null)

                                                                    var kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, telefon,userID,"",kaydedilecekKullaniciDetaylari)


                                                                    mref.child("users").child("isletmeler").child(userID).setValue(kaydedilecekKullanici)
                                                                        .addOnCompleteListener(object :
                                                                            OnCompleteListener<Void> {
                                                                            override fun onComplete(p0: Task<Void>) {
                                                                                if (p0!!.isSuccessful) {
                                                                                    Toast.makeText(activity, "Hoşgeldiniz ${userName}", Toast.LENGTH_SHORT).show()
                                                                                    val action=IsletmeKayitBilgileriFragmentDirections.actionIsletmeKayitBilgileriFragmentToHomeFragment()
                                                                                    Navigation.findNavController(it).navigate(action)
                                                                                  //  findNavController().navigate(R.id.homeFragment)
                                                                                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                                                                                    bottomNav.visibility=View.VISIBLE

                                                                                } else {

                                                                                    auth.currentUser!!.delete()
                                                                                        .addOnCompleteListener(object :
                                                                                            OnCompleteListener<Void> {
                                                                                            override fun onComplete(p0: Task<Void>) {
                                                                                                if (p0!!.isSuccessful) {
                                                                                                    Toast.makeText(activity, "Kullanıcı kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                                                                }
                                                                                            }

                                                                                        })
                                                                                }
                                                                            }


                                                                        })


                                                                }

                                                            }

                                                        })

                                                }




                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })
                                }



                            }
                            //veritabanında kullanıcı yok, aynen kaydet
                            else{
                                if (emailleKayit) {
                                    var sifre =  binding.etSifreIsletme.text.toString()
                                    var adSoyad =  binding.etAdSoyadISletme.text.toString()
                                    var userName =  binding.etKullaniciAdiISletme.text.toString()
                                    var adres= binding.etAdresIsletme.text.toString()
                                    var telefon= binding.etTelefonIsletme.text.toString()






                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                        .addOnCompleteListener(object :
                                            OnCompleteListener<AuthResult> {
                                            override fun onComplete(p0: Task<AuthResult>) {

                                                if (p0!!.isSuccessful) {

                                                    var userID = auth.currentUser!!.uid.toString()

                                                    getAddressFromLocation(
                                                        adres,
                                                        context,userID)

                                                    //oturum açan kullanıcın verilerini databaseye kaydet

                                                    var kaydedilecekKullaniciDetaylari=
                                                        KullaniciBilgiDetaylari("0","","","",adres,null,null)

                                                    var kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, telefon,userID,"",kaydedilecekKullaniciDetaylari)

                                                    mref.child("users").child("isletmeler").child(userID).setValue(kaydedilecekKullanici)
                                                        .addOnCompleteListener(object :
                                                            OnCompleteListener<Void> {
                                                            override fun onComplete(p0: Task<Void>) {
                                                                if (p0!!.isSuccessful) {
                                                                    Toast.makeText(activity, "Hoşgeldiniz ${userName}", Toast.LENGTH_SHORT).show()
                                                                    val action=IsletmeKayitBilgileriFragmentDirections.actionIsletmeKayitBilgileriFragmentToHomeFragment()
                                                                    Navigation.findNavController(it).navigate(action)
                                                                   // findNavController().navigate(R.id.homeFragment)
                                                                    val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                                                                    bottomNav.visibility=View.VISIBLE

                                                                } else {

                                                                    auth.currentUser!!.delete()
                                                                        .addOnCompleteListener(object :
                                                                            OnCompleteListener<Void> {
                                                                            override fun onComplete(p0: Task<Void>) {
                                                                                if (p0!!.isSuccessful) {
                                                                                    Toast.makeText(activity, "Kullanıcı kaydedilemedi, Tekrar deneyin", Toast.LENGTH_SHORT).show()
                                                                                }
                                                                            }

                                                                        })
                                                                }
                                                            }


                                                        })


                                                }

                                            }

                                        })

                                }





                            }


                        }


                    })

                }else{
                    Toast.makeText(activity,"Lütfen telefon numarasını 10 hane şeklinde giriniz.", Toast.LENGTH_SHORT).show()

                }

            }else{
                Toast.makeText(activity,"Kullanıcı adı ve şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show()
            }
        }


        return view
    }

    var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            if ( binding.etAdSoyadISletme.text.toString().length >=1 &&  binding.etKullaniciAdiISletme.text.toString().length >=1 &&  binding.etSifreIsletme.text.toString().length >=1&& binding.etAdresIsletme.text.toString().length>=1&&binding.etTelefonIsletme.text.toString().length>=1) {
                binding. btnGirisISletme.isEnabled = true
                binding.btnGirisISletme.setTextColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnGirisISletme.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                      R.color.teal_700
                    )
                )

            } else {

                binding.btnGirisISletme.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
                binding. btnGirisISletme.setTextColor(ContextCompat.getColor(activity!!,R.color.black))


            }



        }

        override fun afterTextChanged(p0: Editable?) {

        }


    }


    @Subscribe(sticky = true)

    internal fun onKayitEvent(kayitbilgileri: EventbusData.kayitBilgileriniGonder) {

        if (kayitbilgileri.emailkayit == true) {
            emailleKayit = true
            gelenEmail = kayitbilgileri.email!!

            // Toast.makeText(activity, "Gelen email : " + gelenEmail, Toast.LENGTH_SHORT).show()
            Log.e("murat", "Gelen email : " + gelenEmail)
        }





    }



    fun getAddressFromLocation(
        locationAddress: String,
        context: Context?,
        userID:String,
    ) {
        val thread: Thread = object : Thread() {
            override fun run() {
                val geocoder = Geocoder(context, Locale.getDefault())
                var result: String? = null
                try {
                    val addressList: List<*>? = geocoder.getFromLocationName(locationAddress, 1)
                    if (addressList != null && addressList.size > 0) {
                        val address = addressList[0] as Address

                        Log.e("adres","gelen"+address)

                        var latitude=address.latitude
                        var longitude=address.longitude

                        mref.child("users").child("isletmeler").child(userID).child("user_detail").child("latitude").setValue(latitude)
                        mref.child("users").child("isletmeler").child(userID).child("user_detail").child("longitude").setValue(longitude)





                    }
                } catch (e: IOException) {
                } finally {

                    if (result != null) {

                    } else {

                    }

                }
            }
        }
        thread.start()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }


}