package com.example.paylasimmvvm.profileEdit

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentProfilEditBinding
import com.example.paylasimmvvm.login.KullaniciBilgileri
import com.example.paylasimmvvm.util.EventbusData
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class ProfilEditFragment : Fragment() {
    lateinit var binding:FragmentProfilEditBinding
    private lateinit var profileImage:CircleImageView
    var gelenKullaniciBilgileri= KullaniciBilgileri()


    lateinit var mDataRef:DatabaseReference
    private lateinit var mStorage:StorageReference

    private lateinit var storage: FirebaseStorage

    private var secilengorsel: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilEditBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.twMenuEkle.visibility=View.GONE


        mDataRef= FirebaseDatabase.getInstance().reference
        mStorage=FirebaseStorage.getInstance().reference
        storage=FirebaseStorage.getInstance()
        setUpKullaniciBilgileri()

        profileImage=binding.profileImage




        setUpprofilePhoto()

        binding.twMenuEkle.setOnClickListener {
            val action= ProfilEditFragmentDirections.actionProfilEditFragmentToIsletmeMenuFragment()
            Navigation.findNavController(it).navigate(action)
        }

      binding.backButton.setOnClickListener {

          findNavController().navigateUp()
        }

        val getImage=registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) {
            it.let {
                secilengorsel = it
                if (secilengorsel != null) {

                    binding.profileImage.setImageURI(secilengorsel!!)
                }
            }
        }

        binding.fotoDegis.setOnClickListener {
            getImage.launch("image/*")

        }

        binding.kayitButon.setOnClickListener {
            if (secilengorsel!=null){
                binding.progressBar4.visibility=View.VISIBLE

                mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                      if (snapshot.value!=null){

                          mStorage.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!)
                              .child(secilengorsel!!.lastPathSegment!!)
                              .putFile(secilengorsel!!)
                              .addOnSuccessListener { itUploadTask ->
                                  itUploadTask?.storage?.downloadUrl?.addOnSuccessListener { itUri ->
                                      val downloadUrl: String = itUri.toString()
                                      mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!)
                                          .child("user_detail")
                                          .child("profile_picture")
                                          .setValue(downloadUrl).addOnCompleteListener { itTask ->
                                              if (itTask.isSuccessful) {
                                                  binding.progressBar4.visibility=View.GONE


                                                  kullaniciAdiGuncelle(true)
                                              } else {
                                                  val message = itTask.exception?.message
                                                  Toast.makeText(
                                                      requireActivity(),
                                                      "hata$message",
                                                      Toast.LENGTH_SHORT
                                                  ).show()
                                                  kullaniciAdiGuncelle(false)

                                              }
                                          }
                                  }
                              }

                      }else{
                          mStorage.child("users").child("kullanicilar").child(gelenKullaniciBilgileri.user_id!!)
                              .child(secilengorsel!!.lastPathSegment!!)
                              .putFile(secilengorsel!!)
                              .addOnSuccessListener { itUploadTask ->
                                  itUploadTask?.storage?.downloadUrl?.addOnSuccessListener { itUri ->
                                      val downloadUrl: String = itUri.toString()
                                      mDataRef.child("users").child("kullanicilar").child(gelenKullaniciBilgileri.user_id!!)
                                          .child("user_detail")
                                          .child("profile_picture")
                                          .setValue(downloadUrl).addOnCompleteListener { itTask ->
                                              if (itTask.isSuccessful) {
                                                  binding.progressBar4.visibility=View.GONE
                                                  NavOptions.Builder()
                                                      .setPopUpTo(R.id.profilFragment, true)
                                                      .build()


                                                  kullaniciAdiGuncelle(true)
                                              } else {
                                                  val message = itTask.exception?.message
                                                  Toast.makeText(
                                                      requireActivity(),
                                                      "hata$message",
                                                      Toast.LENGTH_SHORT
                                                  ).show()
                                                  kullaniciAdiGuncelle(false)

                                              }
                                          }
                                  }
                              }

                      }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

            }else{

                kullaniciAdiGuncelle(null)


            }


        }

        binding.sifreDegistir.setOnClickListener {
            findNavController().navigate(R.id.sifreDegistirFragment)

        }

    }

     private fun kullaniciAdiGuncelle(profilResmiGuncellendi: Boolean?) {

        if (gelenKullaniciBilgileri.user_name!! != binding.editTextTextPersonName3.text.toString()){

            if (binding.editTextTextPersonName3.text.toString().trim().isNotEmpty()){
                mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value!=null){
                            mDataRef.child("users").child("isletmeler").orderByChild("user_name").addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    var usernameKullanim=false
                                    for (ds in snapshot.children){
                                        val okunanKllaniciAdi=ds!!.getValue(KullaniciBilgileri::class.java)!!.user_name

                                        if (okunanKllaniciAdi!! == binding.editTextTextPersonName3.text.toString()){
                                            profilBilgileriGuncelle(profilResmiGuncellendi, false)
                                            usernameKullanim=true
                                            break

                                        }

                                    }
                                    if (!usernameKullanim){
                                        mDataRef.child("users").child("isletmeler").child(
                                            gelenKullaniciBilgileri.user_id!!).child("user_name").setValue( binding.editTextTextPersonName3.text.toString())
                                        profilBilgileriGuncelle(profilResmiGuncellendi, true)




                                    }



                                }

                                override fun onCancelled(error: DatabaseError) {
                                }

                            })


                        }else{
                            mDataRef.child("users").child("kullanicilar").orderByChild("user_name").addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    var usernameKullanim=false
                                    for (ds in snapshot.children){
                                        val okunanKllaniciAdi=ds!!.getValue(KullaniciBilgileri::class.java)!!.user_name

                                        if (okunanKllaniciAdi!! == binding.editTextTextPersonName3.text.toString()){
                                            profilBilgileriGuncelle(profilResmiGuncellendi, false)
                                            usernameKullanim=true
                                            break

                                        }

                                    }
                                    if (!usernameKullanim){
                                        mDataRef.child("users").child("kullanicilar").child(
                                            gelenKullaniciBilgileri.user_id!!).child("user_name").setValue( binding.editTextTextPersonName3.text.toString())
                                        profilBilgileriGuncelle(profilResmiGuncellendi, true)

                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }

                })



            }else{
                Toast.makeText(activity, "Kullanıcı adı boş olamaz",Toast.LENGTH_LONG).show()


            }



        }else{
            profilBilgileriGuncelle(profilResmiGuncellendi, null)


        }



    }

    private fun profilBilgileriGuncelle(
        profilResmiGuncellendi: Boolean?,
        profilBilgileriGuncellendi: Boolean?
    ) {
        var profilGuncel:Boolean?=null
        mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value!=null){
                    mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {


                            if (gelenKullaniciBilgileri.adi_soyadi!! != binding.editTextTextPersonName2.text.toString()){
                                if ( binding.editTextTextPersonName2.text.toString().trim().isNotEmpty()){
                                    mDataRef.child("users").child("isletmeler").child(gelenKullaniciBilgileri.user_id!!).child("adi_soyadi").setValue( binding.editTextTextPersonName2.text.toString())
                                    profilGuncel=true

                                }else{
                                    Toast.makeText(activity, "Ad soyad boş olamaz.",Toast.LENGTH_LONG).show()

                                }

                            }


                            if (profilResmiGuncellendi==null&&profilBilgileriGuncellendi==null&&profilGuncel==null){
                                Toast.makeText(activity, "Lütfen Bilgileri Güncelleyiniz",Toast.LENGTH_LONG).show()
                            }

                            else if (profilBilgileriGuncellendi==false&&(profilResmiGuncellendi==true||profilGuncel==true)){
                                Toast.makeText(activity, "Kullanıcı adı Kullanımda,diğer bilgiler güncellendi",Toast.LENGTH_LONG).show()


                            }else{
                                Toast.makeText(activity, "Profil Güncellendi",Toast.LENGTH_LONG).show()

                            }


                        }

                        override fun onCancelled(error: DatabaseError) {
                        }

                    })


                }else{

                            if (gelenKullaniciBilgileri.adi_soyadi!! != binding.editTextTextPersonName2.text.toString()){
                                if ( binding.editTextTextPersonName2.text.toString().trim().isNotEmpty()){
                                    mDataRef.child("users").child("kullanicilar").child(gelenKullaniciBilgileri.user_id!!).child("adi_soyadi").setValue( binding.editTextTextPersonName2.text.toString())
                                    profilGuncel=true

                                }else{
                                    Toast.makeText(activity, "Ad soyad boş olamaz.",Toast.LENGTH_LONG).show()

                                }

                            }


                            if (profilResmiGuncellendi==null&&profilBilgileriGuncellendi==null&&profilGuncel==null){
                                Toast.makeText(activity, "Lütfen Bilgileri Güncelleyiniz",Toast.LENGTH_LONG).show()
                            }

                            else if (profilBilgileriGuncellendi==false&&(profilResmiGuncellendi==true||profilGuncel==true)){
                                Toast.makeText(activity, "Kullanıcı adı Kullanımda,diğer bilgiler güncellendi",Toast.LENGTH_LONG).show()


                            }else{
                                Toast.makeText(activity, "Profil Güncellendi",Toast.LENGTH_LONG).show()

                            }







                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })



    }


    private fun setUpKullaniciBilgileri() {
        Log.e("gelenkullanici","bilgisi"+gelenKullaniciBilgileri.user_name)

           mDataRef.child("users").child("kullanicilar").child(gelenKullaniciBilgileri.user_id!!).addListenerForSingleValueEvent(object :ValueEventListener{
                  override fun onDataChange(snapshot: DataSnapshot) {
                      if(snapshot.value !=null){

                              binding.twMenuEkle.visibility=View.GONE
                              binding.editTextTextPersonName2.setText(gelenKullaniciBilgileri.adi_soyadi)
                              binding.editTextTextPersonName3.setText(gelenKullaniciBilgileri.user_name)


                              val imgUrl:String= gelenKullaniciBilgileri.user_detail!!.profile_picture!!
                              if (imgUrl.isNotEmpty()){
                                  Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)
                              }


                      }
                      else{

                          binding.twMenuEkle.visibility=View.VISIBLE
                          binding.editTextTextPersonName2.setText(gelenKullaniciBilgileri.adi_soyadi)
                          binding.editTextTextPersonName3.setText(gelenKullaniciBilgileri.user_name)


                          val imgUrl:String= gelenKullaniciBilgileri.user_detail!!.profile_picture!!
                          if (imgUrl.isNotEmpty()){
                              Picasso.get().load(imgUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.profileImage)
                          }


                      }
                  }

                  override fun onCancelled(error: DatabaseError) {
                  }

              })



    }




    private fun setUpprofilePhoto() {

       profileImage.setBackgroundResource(R.drawable.ic_baseline_person)
    }
    @Subscribe(sticky = true)

    internal fun onKullaniciBilgileriKayitEvent(kullanicibilgileri: EventbusData.kullaniciBilgileriniGonder) {

        gelenKullaniciBilgileri= kullanicibilgileri.kullanici!!


    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        (activity as AppCompatActivity).supportActionBar?.show()
        EventBus.getDefault().unregister(this)
    }



}