package com.example.paylasimmvvm.view.login

import android.content.Context
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
import androidx.navigation.Navigation
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentKullaniciKayitBilgileriBinding
import com.example.paylasimmvvm.model.KullaniciBilgiDetaylari
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.util.EventbusData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class KullaniciKayitBilgileriFragment : Fragment() {
    private lateinit var binding: FragmentKullaniciKayitBilgileriBinding

    var gelenEmail = ""
    var telNo = ""
    var verificationID = ""
    var sifre = ""
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
        binding= FragmentKullaniciKayitBilgileriBinding.inflate(layoutInflater,container,false)

        var view = binding.root

        auth = Firebase.auth
        mref = FirebaseDatabase.getInstance().reference

        if (auth.currentUser != null) {
            auth.signOut()
        }
        binding.tvKaydoll.setOnClickListener {
            val action=KullaniciKayitBilgileriFragmentDirections.actionKullaniciKayitBilgileriFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }


        binding.etAdSoyad.addTextChangedListener(watcher)
        binding.etKullaniciAdi.addTextChangedListener(watcher)
        binding.etSifre.addTextChangedListener(watcher)

        binding.btnGiris.setOnClickListener {
            if( !binding.etAdSoyad.text.toString().trim().isEmpty()){

                if(binding.etKullaniciAdi.text.toString().trim().length>5 && binding.etSifre.text.toString().trim().length>5 && !binding.etAdSoyad.text.toString().trim().isNullOrEmpty())
                {
                    var userNameKullanimdaMi = false

                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {

                        }

                        override fun onDataChange(snapshot: DataSnapshot) {

                            if (snapshot!!.getValue() != null) {


                                for (user in snapshot!!.children) {
                                    var okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                    if (okunanKullanici!!.user_name!!.equals(binding.etKullaniciAdi.text.toString())) {
                                        Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                        userNameKullanimdaMi = true
                                        break
                                    }
                                    mref.child("users").child("isletmeler").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot!!.getValue()!=null){
                                                for (user in snapshot!!.children){
                                                    var okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                                    if (okunanKullanici!!.user_name!!.equals(binding.etKullaniciAdi.text.toString())) {
                                                        Toast.makeText(activity, "Kullanıcı adı Kullanımda", Toast.LENGTH_SHORT).show()
                                                        userNameKullanimdaMi = true
                                                        break
                                                    }


                                                }
                                            }
                                            if (!userNameKullanimdaMi) {



                                                //kullanıcı email ile kayıt
                                                if (emailleKayit) {

                                                    var sifre = binding.etSifre.text.toString()
                                                    var adSoyad = binding.etAdSoyad.text.toString()
                                                    var userName = binding.etKullaniciAdi.text.toString()


                                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                                        .addOnCompleteListener(object :
                                                            OnCompleteListener<AuthResult> {
                                                            override fun onComplete(p0: Task<AuthResult>) {

                                                                if (p0!!.isSuccessful) {

                                                                    var userID = auth.currentUser!!.uid.toString()


                                                                    var kaydedilecekKullaniciDetaylari=
                                                                        KullaniciBilgiDetaylari("0","0","0","","",null,null)

                                                                    var kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, "",userID,"",kaydedilecekKullaniciDetaylari)



                                                                    mref.child("users").child("kullanicilar").child(userID).setValue(kaydedilecekKullanici)
                                                                        .addOnCompleteListener(object :
                                                                            OnCompleteListener<Void> {
                                                                            override fun onComplete(p0: Task<Void>) {
                                                                                if (p0!!.isSuccessful) {


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


                                                                } else {

                                                                    Toast.makeText(activity, "Oturum açılamadı :" + p0!!.exception, Toast.LENGTH_SHORT).show()
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
                                    var sifre = binding.etSifre.text.toString()
                                    var adSoyad = binding.etAdSoyad.text.toString()
                                    var userName = binding.etKullaniciAdi.text.toString()


                                    auth.createUserWithEmailAndPassword(gelenEmail, sifre)
                                        .addOnCompleteListener(object :
                                            OnCompleteListener<AuthResult> {
                                            override fun onComplete(p0: Task<AuthResult>) {

                                                if (p0!!.isSuccessful) {

                                                    var userID = auth.currentUser!!.uid.toString()


                                                    //oturum açan kullanıcın verilerini databaseye kaydedelim...
                                                    var kaydedilecekKullaniciDetaylari=
                                                        KullaniciBilgiDetaylari("0","0","0","","",null,null)

                                                    var kaydedilecekKullanici = KullaniciBilgileri(gelenEmail, sifre, userName, adSoyad, "",userID,"",kaydedilecekKullaniciDetaylari)




                                                    mref.child("users").child("kullanicilar").child(userID).setValue(kaydedilecekKullanici)
                                                        .addOnCompleteListener(object :
                                                            OnCompleteListener<Void> {
                                                            override fun onComplete(p0: Task<Void>) {
                                                                if (p0!!.isSuccessful) {


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
                    Toast.makeText(activity,"Kullanıcı adı ve şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(activity,"Ad ve soyad boş bırakılamaz.", Toast.LENGTH_SHORT).show()
            }



        }





        return view
    }

    var watcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            if (binding.etAdSoyad.text.toString().length >=1 && binding.etKullaniciAdi.text.toString().length >=1 && binding.etSifre.text.toString().length >=1) {
                binding.btnGiris.isEnabled = true
                binding.btnGiris.setTextColor(ContextCompat.getColor(activity!!,R.color.white))
                binding.btnGiris.setBackgroundColor(
                    ContextCompat.getColor(
                        activity!!,
                        R.color.teal_700
                    )
                )

            } else {

                binding.btnGiris.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.white))
                binding.btnGiris.setTextColor(ContextCompat.getColor(activity!!, R.color.black))


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
        } else {
            emailleKayit = false
            telNo = kayitbilgileri.telNo!!
            verificationID = kayitbilgileri.verificationID!!

            Log.e("murat", "Gelen telefon : " + telNo)



        }


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