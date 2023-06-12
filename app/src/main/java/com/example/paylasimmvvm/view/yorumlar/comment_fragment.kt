package com.example.paylasimmvvm.view.yorumlar

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.adapter.YorumlarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentCommentFragmentBinding
import com.example.paylasimmvvm.model.Reply
import com.example.paylasimmvvm.model.Yorumlar
import com.example.paylasimmvvm.util.Bildirimler
import com.example.paylasimmvvm.viewmodel.YorumlarViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue


class comment_fragment : Fragment() , YorumlarRecyclerAdapter.OnReplyClickListener{

    lateinit var binding:FragmentCommentFragmentBinding
    private lateinit var recyclerviewadapter: YorumlarRecyclerAdapter
    private var tumYorumlar=ArrayList<Yorumlar>()
    private lateinit var yorumlarViewModel: YorumlarViewModel
    var tiklananUser=""
    private var isPost: Boolean = false
    var postURL=""
    var gonderiID=""
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    var yorumYapilanGonderi: String? =null
    var yorumKey:String?=null
    private var isReplying = false



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentCommentFragmentBinding.inflate(layoutInflater,container,false)
        val view =binding.root

        return view

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth= FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!

        arguments?.let { it ->
            yorumlarViewModel = ViewModelProvider(this)[YorumlarViewModel::class.java]
            isPost = comment_fragmentArgs.fromBundle(it).isPost
            tiklananUser = comment_fragmentArgs.fromBundle(it).userId
            postURL=comment_fragmentArgs.fromBundle(it).postUrl
            gonderiID=comment_fragmentArgs.fromBundle(it).postId
            yorumYapilanGonderi = arguments?.getString("yorumYapilanGonderi")
            yorumKey= arguments?.getString("yorumKey")
            val  bildirimID = it.getString("bildirimID")

            if (bildirimID!=null){
                val databaseRef = FirebaseDatabase.getInstance().reference.child("bildirimler").child(FirebaseAuth.getInstance().currentUser!!.uid).child(bildirimID)
                databaseRef.child("goruldu").setValue(true)
            }


            if (isPost){
                if (yorumKey!=null){
                    yorumlarViewModel.gonderiYorumlariniAlWithKey(tiklananUser,gonderiID,yorumKey!!)

                }
                if (yorumYapilanGonderi!=null){
                   yorumlarViewModel.gonderiYorumlariniAl(tiklananUser,yorumYapilanGonderi!!)
                }else{
                    yorumlarViewModel.gonderiYorumlariniAl(tiklananUser,gonderiID)
                }
            }
            if(!isPost){
                if (yorumKey!=null){
                    yorumlarViewModel.isletmeYorumlariniAlWithKey(tiklananUser,yorumKey!!)

                }else{
                    yorumlarViewModel.isletmeYorumlariniAl(tiklananUser)
                }


            }


        }

        observeliveData()

        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerviewYorumlar.layoutManager = layoutManager

        recyclerviewadapter =  if (isPost) {
            if (yorumYapilanGonderi != null) {

                YorumlarRecyclerAdapter(tumYorumlar, true, yorumYapilanGonderi!!,tiklananUser)
            }else{
                YorumlarRecyclerAdapter(tumYorumlar, true, gonderiID,tiklananUser)
            }
        } else {

            YorumlarRecyclerAdapter(tumYorumlar, false, "",tiklananUser)
        }
        recyclerviewadapter.setOnReplyClickListener(this)

        binding.recyclerviewYorumlar.adapter = recyclerviewadapter




        binding.recyclerviewYorumlar.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()
                val isLastItem = (firstVisibleItemPosition + visibleItemCount) >= totalItemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition()

                if (isLastItem && dy > 0 ) {
                    if (lastVisibleItemPosition == lastCompletelyVisibleItemPosition && lastVisibleItemPosition == totalItemCount - 1) {
                        // Son item tamamen görünüyorsa nextComment'i çağır
                        yorumlarViewModel.nextComments()
                    }
                }
            }
        })

        binding.twYorumPaylas.setOnClickListener {
            val yorum=binding.etMesajEkle.text.toString().trim()
            if(!TextUtils.isEmpty(yorum)){
                if (isPost){
                    val yeniYorumGonderiReference = FirebaseDatabase.getInstance().reference.child("kampanya").child(tiklananUser).child(gonderiID!!).child("yorumlar").push()
                    val pushKey = yeniYorumGonderiReference.key
                    val yeniYorum = hashMapOf<String, Any>(
                        "yorum_key" to pushKey.toString(),
                        "user_id" to mUser.uid,
                        "yorum" to binding.etMesajEkle.text.toString(),
                        "yorum_begeni" to "0",
                        "yorum_tarih" to ServerValue.TIMESTAMP)
                    yeniYorumGonderiReference.setValue(yeniYorum)
                    if (tiklananUser!=FirebaseAuth.getInstance().currentUser!!.uid) {}
                        Bildirimler.bildirimKaydet(
                            tiklananUser,
                            Bildirimler.YORUM_YAPILDI,
                            gonderiID,
                            postURL,
                            binding.etMesajEkle.text.toString(),
                            pushKey)
                    yorumlarViewModel.gonderiYorumlariniAl(tiklananUser,gonderiID)
                }else{
                    val yeniYorumIsletmeReference = FirebaseDatabase.getInstance().reference.child("users").child("isletmeler").child(tiklananUser).child("yorumlar").push()
                    val pushKey = yeniYorumIsletmeReference.key
                    val yeniYorum = hashMapOf<String, Any>(
                        "yorum_key" to pushKey.toString(),
                        "user_id" to mUser.uid,
                        "yorum" to binding.etMesajEkle.text.toString(),
                        "yorum_begeni" to "0",
                        "yorum_tarih" to ServerValue.TIMESTAMP)
                   yeniYorumIsletmeReference.setValue(yeniYorum)
                    if (tiklananUser!=FirebaseAuth.getInstance().currentUser!!.uid) {}
                        Bildirimler.bildirimKaydet(
                            tiklananUser,
                            Bildirimler.YORUM_YAPILDI_ISLETME,
                            gonderiID,
                            postURL,
                            binding.etMesajEkle.text.toString(),
                            pushKey)
                    yorumlarViewModel.isletmeYorumlariniAl(tiklananUser)
                }
                binding.etMesajEkle.setText("")

                binding.recyclerviewYorumlar.smoothScrollToPosition(binding.recyclerviewYorumlar.adapter!!.itemCount)
            }
        }
        binding.imageViewBack.setOnClickListener {
            findNavController().navigateUp()


        }
        binding.commentClose.setOnClickListener {
            binding.replyCommentContainer.visibility=View.GONE
            if (isReplying) {
                isReplying = false
                val imm = getSystemService(requireView().context, InputMethodManager::class.java)
                imm?.hideSoftInputFromWindow(binding.etMesajEkleReply.windowToken, 0)
            }

        }
    }

    private fun observeliveData(){

        yorumlarViewModel.mutableYorumlar.observe(viewLifecycleOwner) { Yorumlar ->
            Yorumlar.let {
             for (yorum in Yorumlar) {
                 val yanitlarSize = yorum.yanitlar.size
                 val yorumID=yorum.yorum_key

                 recyclerviewadapter.updateReplyCount(yorumID!!,yanitlarSize)

                }
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


                }else{
                     binding.progressBar8.visibility=View.GONE
                }
            }
        }




    }
    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.hide()

    }


    override fun onReplyClick(username: String, yorumID: String, yorum: String) {

        binding.replyCommentContainer.visibility=View.VISIBLE
        binding.replyUsernameTv.hint=username
        Log.e("gelenyorum",""+yorum)
        if (yorum.length>40){
            binding.replyCommentTv.hint=yorum.substring(0, 40) + ".."
        }else{
            binding.replyCommentTv.hint=yorum
        }

        isReplying=true
        // klavyeyi aç
        binding.etMesajEkleReply.requestFocus()
        val imm = getSystemService(requireView().context, InputMethodManager::class.java)
        imm?.showSoftInput(binding.etMesajEkleReply, InputMethodManager.SHOW_IMPLICIT)


        binding.twYorumPaylas.setOnClickListener {
            val yorum=binding.etMesajEkleReply.text.toString().trim()
            if(!TextUtils.isEmpty(yorum)){
                val yeniYorumGonderiReference = FirebaseDatabase.getInstance().reference.child("kampanya").child(tiklananUser).child(gonderiID!!).child("yorumlar").child(yorumID).child("yanitlar").push()
                val pushKey = yeniYorumGonderiReference.key
                val yanit = Reply(
                    yorum_key = pushKey.toString(),
                    user_id = mUser.uid,
                    yorum = binding.etMesajEkle.text.toString(),
                    yorum_begeni = "0",
                    yorum_tarih =  System.currentTimeMillis()
                )

                yeniYorumGonderiReference.setValue(yanit)
                binding.etMesajEkle.setText("")


            }
        }

    }

}