package com.example.paylasimmvvm.view.login

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentRegisterBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.util.EventbusData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import org.greenrobot.eventbus.EventBus


class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    lateinit var mref: DatabaseReference
    lateinit var auth: FirebaseAuth
    private lateinit var mauthLis: FirebaseAuth.AuthStateListener


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding= FragmentRegisterBinding.inflate(layoutInflater,container,false)

        val view=binding.root

        mref = FirebaseDatabase.getInstance().reference
        auth= FirebaseAuth.getInstance()

        setupAuthLis(view)
        register()
        return view
    }

    private fun checkMail(kontrolEdilenMail: String): Boolean {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(kontrolEdilenMail).matches()

    }
    private fun setupAuthLis(view:View) {

        mauthLis= FirebaseAuth.AuthStateListener {
            val user=FirebaseAuth.getInstance().currentUser

            if (user!=null){
                val action=RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                Navigation.findNavController(view).navigate(action)

            }
        }

    }
    override fun onStart() {
        super.onStart()
        Log.e("hata","registedas??n")
        auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(mauthLis)
    }


    private fun register() {





        binding.textViewTelefon.setOnClickListener {
            binding.view2.visibility = View.VISIBLE
            binding. viewEposta.visibility = View.INVISIBLE
            binding. editTextTextPersonName.setText("")
            binding.editTextTextPersonName.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            binding. editTextTextPersonName.hint = "e-posta"
            binding. btnKayit.isEnabled = false


        }

        binding.textViewEposta.setOnClickListener {

            binding. view2.visibility = View.INVISIBLE
            binding.viewEposta.visibility = View.VISIBLE
            binding. editTextTextPersonName.setText("")
            binding.editTextTextPersonName.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            binding.editTextTextPersonName.hint = "E-POSTA"
            binding. btnKayit.isEnabled = false


        }

        binding.editTextTextPersonName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.length >= 10) {
                    binding. btnKayit.isEnabled = true
                    binding. btnKayit.setTextColor(
                        ContextCompat.getColor(
                            activity!!,
                        R.color.white
                        )
                    )
                    binding. btnKayit.setBackgroundColor(
                        ContextCompat.getColor(
                            activity!!,
                           R.color.teal_700
                        )
                    )


                } else {
                    binding. btnKayit.isEnabled = false


                }


            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        binding.btnKayit.setOnClickListener {

            if(binding.editTextTextPersonName.hint.toString() == "e-posta"){


                if (checkMail( binding.editTextTextPersonName.text.toString())) {

                    var emailKullanimdaMi = false


                    mref.child("users").child("isletmeler")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {

                                if (snapshot.value != null) {

                                    for (user in snapshot.children) {


                                        val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                        if (okunanKullanici!!.email!! == binding.editTextTextPersonName.text.toString()) {


                                            Log.e("kullanicilar??n", "i??letmeb??l??m??$okunanKullanici")



                                            Toast.makeText(  activity!!, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()


                                            emailKullanimdaMi = true
                                            break
                                        }




                                    }
                                    //i??letme de??il kullanicilara bak
                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value !=null){
                                                for (user in snapshot.children){

                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)

                                                    if (okunanKullanici!!.email!! == binding.editTextTextPersonName.text.toString()) {

                                                        Log.e("kullanicilar??n",
                                                            "kullanicib??l??m??$okunanKullanici"
                                                        )


                                                        Toast.makeText(  activity!!, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()

                                                        emailKullanimdaMi = true
                                                        break

                                                    }

                                                } // i??letme var kullanici var kullanici bulunamad??,kaydet
                                                if (!emailKullanimdaMi){
                                                    binding. loginroot.visibility = View.GONE
                                                    binding. loginframe.visibility = View.VISIBLE

                                                    findNavController().navigate(R.id.kullaniciKayitBilgileriFragment)


                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            binding.editTextTextPersonName.text.toString(),
                                                            null,
                                                            true,

                                                        )
                                                    )



                                                }
                                            }
                                            if(!emailKullanimdaMi){

                                                Log.e("veritaban??nda i??letme var,kullanici yok ancak kay??tl?? de??il,kaydet","??al??st??")


                                                binding.loginroot.visibility = View.GONE
                                                binding. loginframe.visibility = View.VISIBLE

                                                findNavController().navigate(R.id.kullaniciKayitBilgileriFragment)

                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        binding.editTextTextPersonName.text.toString(),
                                                        null,
                                                        true,

                                                    )
                                                )

                                            }

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })





                                }

                                //veritaban??nda hi?? i??letme  yok, kullanicilara bak
                                else{

                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            if (snapshot.value !=null){
                                                for (user in snapshot.children){


                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)
                                                    if (okunanKullanici!!.email!! == binding.editTextTextPersonName.text.toString()) {

                                                        Log.e("i??letme yok",
                                                            "kullanici var,bulunan kullanici$okunanKullanici"
                                                        )
                                                        Toast.makeText(activity!!, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()

                                                        emailKullanimdaMi = true
                                                        break
                                                    }



                                                }
                                                //veritaban??nda i??letme yok,kullanici var ancak kay??tl?? de??il,kaydet
                                                if(!emailKullanimdaMi)
                                                {
                                                    Log.e("veritaban??nda i??letme yok,kullanici var ancak kay??tl?? de??il,kaydet","??al??st??")


                                                    binding.loginroot.visibility = View.GONE
                                                    binding.loginframe.visibility = View.VISIBLE

                                                    findNavController().navigate(R.id.kullaniciKayitBilgileriFragment)

                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            binding. editTextTextPersonName.text.toString(),
                                                            null,
                                                            true,

                                                        )
                                                    )
                                                }
                                            }
                                            if  (!emailKullanimdaMi)
                                            //veritaban??nda  i??letme ve kullanici yok direkt kaydet
                                            {
                                                Log.e("elsekkaydet","direktkaydetcal??st??")

                                                Log.e("veritaban??nda kullanicida yok direkt kaydet","??al??st??")



                                                binding. loginroot.visibility = View.GONE
                                                binding. loginframe.visibility = View.VISIBLE

                                                findNavController().navigate(R.id.kullaniciKayitBilgileriFragment)

                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        binding.editTextTextPersonName.text.toString(),
                                                        null,
                                                        true,

                                                    )
                                                )

                                            }



                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("hata","hatacal??st??")
                                        }

                                    })



                                }

                            }

                        })


                }

            }
            else {



                if (checkMail( binding.editTextTextPersonName.text.toString())) {

                    var emailKullanimdaMi = false


                    mref.child("users").child("isletmeler")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {

                            }

                            override fun onDataChange(snapshot: DataSnapshot) {

                                if (snapshot.value != null) {

                                    for (user in snapshot.children) {


                                        val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)
                                        if (okunanKullanici!!.email!! == binding.editTextTextPersonName.text.toString()) {


                                            Log.e("kullanicilar??n", "i??letmeb??l??m??$okunanKullanici")



                                            Toast.makeText(activity!!, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()


                                            emailKullanimdaMi = true
                                            break
                                        }




                                    }
                                    //i??letme de??il kullanicilara bak
                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.value !=null){
                                                for (user in snapshot.children){

                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)

                                                    if (okunanKullanici!!.email!! == binding.editTextTextPersonName.text.toString()) {

                                                        Log.e("kullanicilar??n", "kullanicib??l??m??$okunanKullanici"
                                                        )


                                                        Toast.makeText(activity!!, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()

                                                        emailKullanimdaMi = true
                                                        break

                                                    }

                                                } // kullanici bulunamad??,kaydet
                                                if (!emailKullanimdaMi){
                                                    binding. loginroot.visibility = View.GONE
                                                    binding. loginframe.visibility = View.VISIBLE

                                                    findNavController().navigate(R.id.isletmeKayitBilgileriFragment)

                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            binding. editTextTextPersonName.text.toString(),
                                                            null,
                                                            true,

                                                        )
                                                    )


                                                }
                                            }
                                            if(!emailKullanimdaMi){

                                                Log.e("veritaban??nda i??letme var,kullanici yok ancak kay??tl?? de??il,kaydet","??al??st??")


                                                binding.loginroot.visibility = View.GONE
                                                binding. loginframe.visibility = View.VISIBLE

                                                findNavController().navigate(R.id.isletmeKayitBilgileriFragment)

                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        binding.editTextTextPersonName.text.toString(),
                                                        null,
                                                        true,

                                                    )
                                                )

                                            }

                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })





                                }

                                //veritaban??nda hi?? i??letme  yok, kullanicilara bak
                                else{

                                    mref.child("users").child("kullanicilar").addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            if (snapshot.value !=null){
                                                for (user in snapshot.children){


                                                    val okunanKullanici = user.getValue(
                                                        KullaniciBilgileri::class.java)
                                                    if (okunanKullanici!!.email!! == binding.editTextTextPersonName.text.toString()) {

                                                        Log.e("i??letme yok",
                                                            "kullanici var,bulunan kullanici$okunanKullanici"
                                                        )
                                                        Toast.makeText(activity!!, "E-mail Kullan??mda", Toast.LENGTH_SHORT).show()

                                                        emailKullanimdaMi = true
                                                        break
                                                    }



                                                }
                                                //veritaban??nda i??letme yok,kullanici var ancak kay??tl?? de??il,kaydet
                                                if(!emailKullanimdaMi)
                                                {
                                                    Log.e("veritaban??nda i??letme yok,kullanici var ancak kay??tl?? de??il,kaydet","??al??st??")


                                                    binding. loginroot.visibility = View.GONE
                                                    binding.loginframe.visibility = View.VISIBLE

                                                    findNavController().navigate(R.id.isletmeKayitBilgileriFragment)

                                                    EventBus.getDefault().postSticky(
                                                        EventbusData.kayitBilgileriniGonder(
                                                            null,
                                                            binding.editTextTextPersonName.text.toString(),
                                                            null,
                                                            true,

                                                        )
                                                    )
                                                }
                                            }
                                            if  (!emailKullanimdaMi)
                                            //veritaban??nda kullanicida yok direkt kaydet
                                            {
                                                Log.e("elsekkaydet","direktkaydetcal??st??")

                                                Log.e("veritaban??nda kullanicida yok direkt kaydet","??al??st??")



                                                binding. loginroot.visibility = View.GONE
                                                binding.  loginframe.visibility = View.VISIBLE

                                                findNavController().navigate(R.id.isletmeKayitBilgileriFragment)

                                                EventBus.getDefault().postSticky(
                                                    EventbusData.kayitBilgileriniGonder(
                                                        null,
                                                        binding. editTextTextPersonName.text.toString(),
                                                        null,
                                                        true,

                                                    )
                                                )

                                            }



                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    })



                                }

                            }

                        })

                } else {
                    Toast.makeText(
                        requireActivity(),
                        "L??tfen ge??erli bir E-mail  giriniz",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }


    }


}