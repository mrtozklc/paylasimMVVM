package com.example.paylasimmvvm.view.yorumlar

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.YorumlarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentCommentFragmentBinding
import com.example.paylasimmvvm.model.Yorumlar
import com.example.paylasimmvvm.viewmodel.YorumlarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.ktx.Firebase


class comment_fragment : Fragment() {

    lateinit var binding:FragmentCommentFragmentBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var recyclerviewadapter: YorumlarRecyclerAdapter
    private var tumYorumlar=ArrayList<Yorumlar>()
    private lateinit var yorumlarViewModel: YorumlarViewModel
    var tiklananUser=""
    private var isPost: Boolean = false



    var   yorumYapilacakGonderininID:String?=null
    var yorumYapilacakIsletmeID:String?=null
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCommentFragmentBinding.inflate(layoutInflater,container,false)
        val view =binding.root

        return view
        // Inflate the layout for this fragment
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth= FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!

        arguments?.let { it ->
            isPost = comment_fragmentArgs.fromBundle(it).isPost
            val secilenUser = comment_fragmentArgs.fromBundle(it).userId
            tiklananUser = secilenUser


            yorumlarViewModel = ViewModelProvider(this)[YorumlarViewModel::class.java]
            yorumlarViewModel.yorumlariAl(tiklananUser)

        }

        observeliveData()


        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewYorumlar.layoutManager = layoutManager

        recyclerviewadapter = if (isPost) {
            YorumlarRecyclerAdapter(tumYorumlar, true,tiklananUser)
        } else {
            YorumlarRecyclerAdapter(tumYorumlar, false,tiklananUser)
        }

        binding.recyclerviewYorumlar.adapter = recyclerviewadapter


        binding.twYorumPaylas.setOnClickListener {

            val yorum=binding.etMesajEkle.text.toString().trim()


            if(!TextUtils.isEmpty(yorum)){

                val yeniYorum = hashMapOf<String, Any>(
                    "user_id" to mUser.uid,
                    "yorum" to binding.etMesajEkle.text.toString(),
                    "yorum_begeni" to "0",
                    "yorum_tarih" to ServerValue.TIMESTAMP
                )

                FirebaseDatabase.getInstance().reference.child("yorumlar")
                    .child(tiklananUser!!).push().setValue(yeniYorum)


                binding.etMesajEkle.setText("")

                binding.recyclerviewYorumlar.smoothScrollToPosition(binding.recyclerviewYorumlar.adapter!!.itemCount)


            }
        }

        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()
        }




    }

    private fun observeliveData(){


        yorumlarViewModel.mutableYorumlar.observe(viewLifecycleOwner) { Yorumlar ->
            Yorumlar.let {

                binding.recyclerviewYorumlar.visibility = View.VISIBLE
                recyclerviewadapter.yorumlariGuncelle(Yorumlar)
            }

        }

        yorumlarViewModel.yorumYok.observe(viewLifecycleOwner) { YorumYok->
            YorumYok.let {
                if (it){
                      binding.yorumYok.visibility=View.VISIBLE
                    binding.recyclerviewYorumlar.visibility=View.GONE

                }else{
                   binding.yorumYok.visibility=View.GONE

                }

            }

        }
        yorumlarViewModel.yukleniyor.observe(viewLifecycleOwner) { yukleniyor->
            yukleniyor.let {
                if (it){
                     binding.progressBar8.visibility=View.VISIBLE
                      binding.yorumYok.visibility=View.GONE
                      binding.recyclerviewYorumlar.visibility=View.GONE

                }else{
                     binding.progressBar8.visibility=View.GONE

                }

            }

        }

    }







}