package com.example.paylasimmvvm.view.yorumlar

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.FragmentYorumlarBinding
import com.example.paylasimmvvm.databinding.RecyclerRowYorumlarBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.Yorumlar
import com.example.paylasimmvvm.util.EventbusData
import com.example.paylasimmvvm.util.TimeAgo
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class YorumlarFragment : Fragment() {

    private lateinit var binding:FragmentYorumlarBinding

    var   yorumYapilacakGonderininID:String?=null
    var yorumYapilacakIsletmeID:String?=null
    lateinit var mAuth: FirebaseAuth
    lateinit var mUser: FirebaseUser
    lateinit var mRef: DatabaseReference
    lateinit var adapter: FirebaseRecyclerAdapter<Yorumlar, YorumlarViewHolder>




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentYorumlarBinding.inflate(layoutInflater,container,false)

        val view=binding.root

        mAuth= FirebaseAuth.getInstance()
        mUser=mAuth.currentUser!!
        mRef= FirebaseDatabase.getInstance().reference.child("yorumlar").child(yorumYapilacakGonderininID!!)

        val options = FirebaseRecyclerOptions.Builder<Yorumlar>()
            .setQuery(mRef, Yorumlar::class.java)
            .build()

        adapter=object : FirebaseRecyclerAdapter<Yorumlar, YorumlarViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YorumlarViewHolder {
                var yorumlarViewHolder=LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_yorumlar, parent, false)

                return YorumlarViewHolder(yorumlarViewHolder)
            }

            override fun onBindViewHolder(holder: YorumlarViewHolder, position: Int, model: Yorumlar) {

                holder.setData(model)

                if(position==0 && (yorumYapilacakGonderininID!!.equals(getRef(0).key))){
                    holder.yorumBegen.visibility=View.INVISIBLE
                }
                holder.begenme(yorumYapilacakGonderininID!!,getRef(position).key)
                holder.begenmeDurumu(yorumYapilacakGonderininID!!,getRef(position).key)


            }

        }
        binding.recyclerviewYorumlar.layoutManager= LinearLayoutManager(activity,
            LinearLayoutManager.VERTICAL,false)
        binding.recyclerviewYorumlar.adapter=adapter



        binding.twYorumPaylas.setOnClickListener {

            var yorum=binding.etMesajEkle.text.toString().trim()


            if(!TextUtils.isEmpty(yorum.toString())){

                var yeniYorum = hashMapOf<String, Any>(
                    "user_id" to mUser.uid,
                    "yorum" to binding.etMesajEkle.text.toString(),
                    "yorum_begeni" to "0",
                    "yorum_tarih" to ServerValue.TIMESTAMP
                )

                FirebaseDatabase.getInstance().getReference().child("yorumlar")
                    .child(yorumYapilacakGonderininID!!).push().setValue(yeniYorum)


                binding.etMesajEkle.setText("")

                binding.recyclerviewYorumlar.smoothScrollToPosition(binding.recyclerviewYorumlar.adapter!!.itemCount)





            }
        }

        setupProfilPicture()




        return view
    }

    class YorumlarViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val binding= RecyclerRowYorumlarBinding.bind(itemView!!)


        var yorumYapanUserPhoto=binding.profilPhotoYorumlar
        var kullaniciAdiveYorum=binding.tvAciklama
        var yorumBegen=binding.like
        var yorumSure=binding.tvYorumZamani
        var yorumBegenmeSayisi=binding.tvBegeni

        fun setData(oanOlusturulanYorum: Yorumlar) {


            yorumSure.text = TimeAgo.getTimeAgoForComments(oanOlusturulanYorum!!.yorum_tarih!!)
            yorumBegenmeSayisi.text = oanOlusturulanYorum.yorum_begeni
            kullaniciAdiveYorum.text = oanOlusturulanYorum.yorum

            kullaniciAdiveYorum.setOnClickListener {
                if (oanOlusturulanYorum.user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)){
                    val action=YorumlarFragmentDirections.actionYorumlarFragmentToProfilFragment()
                    Navigation.findNavController(it).navigate(action)
                }else{
                    val action=YorumlarFragmentDirections.actionYorumlarFragmentToUserProfilFragment(oanOlusturulanYorum.user_id!!)
                    Navigation.findNavController(it).navigate(action)

                }

            }




            kullaniciBilgileriniGetir(oanOlusturulanYorum.user_id,oanOlusturulanYorum.yorum)



        }


        private fun kullaniciBilgileriniGetir(user_id: String?, yorum: String?) {

            val mref= FirebaseDatabase.getInstance().reference
            mref.child("users").child("kullanicilar").child(user_id!!).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){
                        val userNameveYorum="<font color=#000>"+ snapshot.getValue(
                            KullaniciBilgileri::class.java)!!.user_name!!.toString()+"</font>" + " " + yorum
                        val sonuc: Spanned?
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            sonuc= Html.fromHtml(userNameveYorum, Html.FROM_HTML_MODE_LEGACY)
                        }else {
                            sonuc= Html.fromHtml(userNameveYorum)
                        }
                        kullaniciAdiveYorum.text = sonuc
                        Picasso.get().load(snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture!!.toString()).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(yorumYapanUserPhoto)

                    }else{

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


            mref.child("users").child("isletmeler").child(user_id).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value !=null){
                        val userNameveYorum="<font color=#000>"+ snapshot.getValue(
                            KullaniciBilgileri::class.java)!!.user_name!!.toString()+"</font>" + " " + yorum
                        val sonuc: Spanned? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                            Html.fromHtml(userNameveYorum, Html.FROM_HTML_MODE_LEGACY)
                        }else {
                            Html.fromHtml(userNameveYorum)
                        }
                        kullaniciAdiveYorum.text = sonuc
                        if (!snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture!!.toString().isEmpty())
                         {
                             Picasso.get().load(snapshot.getValue(KullaniciBilgileri::class.java)!!.user_detail!!.profile_picture!!.toString()).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(yorumYapanUserPhoto)



                         }else {

                           Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(yorumYapanUserPhoto)



                        }


                    }

                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        }

        fun begenme(yorumYapilacakGonderininID: String, begenilecekYorumId: String?) {

            val mRef= FirebaseDatabase.getInstance().reference.child("yorumlar").child(yorumYapilacakGonderininID).child(begenilecekYorumId!!)


            yorumBegen.setOnClickListener {


                mRef.child("begenenler").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                            mRef.child("begenenler").child(FirebaseAuth.getInstance().currentUser!!.uid).removeValue()
                             yorumBegen.setImageResource(R.drawable.ic_launcher_like_foreground)



                        }else{

                            mRef.child("begenenler").child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(
                                FirebaseAuth.getInstance().currentUser!!.uid)

                         yorumBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)



                        }


                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })


            }

        }

        fun begenmeDurumu(yorumYapilacakGonderininID: String, begenilecekYorumId: String?) {
            val mRef= FirebaseDatabase.getInstance().reference.child("yorumlar").child(yorumYapilacakGonderininID).child(begenilecekYorumId!!)


            mRef.child("begenenler").addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        yorumBegenmeSayisi.visibility=View.VISIBLE
                        yorumBegenmeSayisi.text= snapshot.childrenCount.toString()+" beğenme"

                    }else{
                        yorumBegenmeSayisi.visibility=View.INVISIBLE


                    }

                    if (snapshot.hasChild(FirebaseAuth.getInstance().currentUser!!.uid)){
                     yorumBegen.setImageResource(R.drawable.ic_launcher_like_red_foreground)




                    }else{
                       yorumBegen.setImageResource(R.drawable.ic_launcher_like_foreground)



                    }


                }

                override fun onCancelled(error: DatabaseError) {
                }

            })


        }
    }


    @Subscribe(sticky = true)
    internal fun onYorumYapilacakGonderi(gonderi: EventbusData.YorumYapilacakGonderininIDsiniGonder) {


        yorumYapilacakGonderininID = gonderi.gonderiID!!

    }

    internal fun onYorumYapilacakIsletme(isletmeID: EventbusData.yorumYapilacakIsletmeID){
        yorumYapilacakIsletmeID=isletmeID.isletmeID


    }
    private fun setupProfilPicture() {

        mRef= FirebaseDatabase.getInstance().reference.child("users")
        mRef.child(mUser.uid).child("user_detail").addListenerForSingleValueEvent(object :
            ValueEventListener {




            override fun onDataChange(snapshot: DataSnapshot) {
                val profilPictureURL= snapshot.child("profile_picture").value.toString()

            Picasso.get().load(profilPictureURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(binding.circleProfilPhoto)}

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        EventBus.getDefault().register(this)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}