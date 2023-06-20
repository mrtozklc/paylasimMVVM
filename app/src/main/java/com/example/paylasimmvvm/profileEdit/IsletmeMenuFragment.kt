package com.example.paylasimmvvm.profileEdit

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentIsletmeMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class IsletmeMenuFragment : Fragment() {
    private lateinit var binding:FragmentIsletmeMenuBinding
    lateinit var mDataRef: DatabaseReference
    lateinit var mStorage: StorageReference
    private lateinit var storage: FirebaseStorage

    var secilengorsel: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentIsletmeMenuBinding.inflate(layoutInflater,container,false)
        mDataRef= FirebaseDatabase.getInstance().reference
        mStorage=FirebaseStorage.getInstance().reference
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.menuBack.setOnClickListener {

            findNavController().navigateUp()

        }



        val getImage=registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                it.let {
                    secilengorsel=it
                    if (secilengorsel!=null){

                        binding. imgMenuEkle.setImageURI(secilengorsel!!)


                    }


                }

            })


        binding.twMenuEkle.setOnClickListener {

            getImage.launch("image/*")


        }



        binding.menuOnay.setOnClickListener {
            if (secilengorsel!=null){
                binding.progressBar4.visibility=View.VISIBLE
                binding.twMenuEkle.visibility=View.GONE
                val yeniMenu=HashMap<String,Any>()



                mStorage.child("menuler").child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(secilengorsel!!.lastPathSegment!!)
                    .putFile(secilengorsel!!)
                    .addOnSuccessListener { itUploadTask ->
                        itUploadTask?.storage?.downloadUrl?.addOnSuccessListener { itUri ->
                            val downloadUrl: String = itUri.toString()
                            yeniMenu["menuler"] = downloadUrl
                            mDataRef.child("menuler").child(FirebaseAuth.getInstance().currentUser!!.uid).push().setValue(yeniMenu).addOnCompleteListener { itTask ->
                                    if (itTask.isSuccessful) {
                                        binding.progressBar4.visibility=View.GONE
                                        binding.twMenuEkle.visibility=View.VISIBLE
                                        val navOptions = NavOptions.Builder()
                                            .setPopUpTo(R.id.profilEditFragment, true)
                                            .build()

                                        val action=
                                            IsletmeMenuFragmentDirections.actionIsletmeMenuFragmentToProfilFragment()
                                        Navigation.findNavController(binding.menuOnay).navigate(action,navOptions)

                                    } else {
                                        val message = itTask.exception?.message
                                        Toast.makeText(
                                            requireActivity(),
                                            "hata" + message,
                                            Toast.LENGTH_SHORT
                                        ).show()


                                    }
                                }
                        }
                    }
            }else{

                Toast.makeText(activity, "Lütfen Görsel Seçiniz.",Toast.LENGTH_LONG).show()



            }


        }
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()

    }

}