package com.example.paylasimmvvm.home

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowBinding
import com.example.paylasimmvvm.createPost.KullaniciKampanya
import com.example.paylasimmvvm.util.Bildirimler
import com.example.paylasimmvvm.util.TimeAgo
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class HomeFragmentRecyclerAdapter (var context: Context, private val tumKampanyalar:ArrayList<KullaniciKampanya>, private val tekGonderi:Boolean): RecyclerView.Adapter<HomeFragmentRecyclerAdapter.ViewHolder>(){

    class ViewHolder(itemView: View,private val tekGonderi: Boolean ): RecyclerView.ViewHolder(itemView) {

        val binding= RecyclerRowBinding.bind(itemView)
        private var profileImage = binding.profilImage
        private var userNameTitle = binding.kullaniciAdiTepe
        private var gonderi = binding.kampanyaPhoto
        private var userName = binding.userName
        private var kampanyaTarihi = binding.kampanyaTarihi
        private var yorumYap = binding.imgYorum
        var gonderiBegen = binding.imgBegen
        var begenmeSayisi=binding.begenmeSayisi
        var yorumlariGoster=binding.tvYorumGoster
        private var postMenu=binding.postMesaj
        private var delete=binding.delete

        @SuppressLint("SetTextI18n")
        fun setData(anlikGonderi: KullaniciKampanya) {


            delete.visibility=View.GONE

            if (tekGonderi){
                delete.visibility=View.VISIBLE
                binding.tvYorumGoster.visibility=View.GONE
                binding.imgBegen.visibility=View.GONE
                binding.imgYorum.visibility=View.GONE
                binding.profilImage.visibility=View.GONE
                binding.kullaniciAdiTepe.visibility=View.GONE
                binding.delete.visibility=View.GONE

            }else{

               yorumlariGoster(anlikGonderi)

                yorumYap.setOnClickListener {
                    yorumlarFragmentiniBaslat(anlikGonderi)
                }

                yorumlariGoster.setOnClickListener {
                    yorumlarFragmentiniBaslat(anlikGonderi)
                }

            }
            userNameTitle.text = anlikGonderi.userName

            if (anlikGonderi.userPhotoURL!!.isNotEmpty()){
                Picasso.get().load(anlikGonderi.userPhotoURL).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)
            }

            else {
                Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).fit().centerCrop().into(profileImage)
            }

            userName.text = anlikGonderi.userName.toString()
            binding.aciklama.text=anlikGonderi.postAciklama.toString()

            Picasso.get().load(anlikGonderi.postURL).into(gonderi)

            gonderi.setOnClickListener {

                val builder = AlertDialog.Builder(itemView.context)

                val imageView = PhotoView(itemView.context)
                imageView.adjustViewBounds = true

                Picasso.get().load(anlikGonderi.postURL).into(imageView)

                builder.setView(imageView)
                val alertDialog = builder.create()
                alertDialog.setCanceledOnTouchOutside(true)
                imageView.setOnClickListener {
                    alertDialog.dismiss()
                }
                alertDialog.show()

            }

            kampanyaTarihi.text = TimeAgo.getTimeAgo(anlikGonderi.postYuklenmeTarih!!)

            userNameTitle.setOnClickListener {

                if (anlikGonderi.userID!! != FirebaseAuth.getInstance().currentUser!!.uid){
                    val action= HomeFragmentDirections.actionHomeFragmentToUserProfilFragment(anlikGonderi.userID!!)
                    Navigation.findNavController(it).navigate(action)
                }
            }

            postMenu.setOnClickListener {
                val mref= FirebaseDatabase.getInstance().reference
                val mauth= FirebaseAuth.getInstance().currentUser!!.uid

                mref.child("kampanya").child(anlikGonderi.userID!!).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.value !=null){
                            if (!anlikGonderi.userID.equals(mauth)){
                                val action= HomeFragmentDirections.actionHomeFragmentToChatFragment(
                                    anlikGonderi.userID!!
                                )
                                Navigation.findNavController(it).navigate(action)
                            }else{
                                val action=
                                    HomeFragmentDirections.actionHomeFragmentToProfilFragment()
                                Navigation.findNavController(it).navigate(action)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }

            val mauth:String=FirebaseAuth.getInstance().currentUser!!.uid
           begeniKontrolu(anlikGonderi)

            if (anlikGonderi.userID.equals(mauth)){
                postMenu.visibility=View.GONE
            }
            gonderiBegen.setOnClickListener {
                val ref = FirebaseDatabase.getInstance().reference
                val currentID = FirebaseAuth.getInstance().currentUser!!.uid
                ref.child("kampanya").child(anlikGonderi.userID!!).child(anlikGonderi.postID!!).child("begeniler")
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.hasChild(currentID)) {
                                ref.child("kampanya").child(anlikGonderi.userID!!).child(anlikGonderi.postID!!).child("begeniler").child(currentID)
                                    .removeValue()
                                gonderiBegen.setImageResource(R.drawable.ic_baseline_favorite)
                            } else {
                                ref.child("kampanya").child(anlikGonderi.userID!!).child(anlikGonderi.postID!!).child("begeniler").child(currentID)
                                    .setValue(currentID)

                                if (anlikGonderi.userID!= FirebaseAuth.getInstance().currentUser!!.uid) {

                                    Bildirimler.bildirimKaydet(
                                        anlikGonderi.userID!!,
                                        Bildirimler.KAMPANYA_BEGENILDI,
                                        anlikGonderi.postID!!,
                                        anlikGonderi.postURL!!, "", "", ""
                                    )
                                }
                                gonderiBegen.setImageResource(R.drawable.baseline_favorite_red_24)
                                begenmeSayisi.visibility=View.VISIBLE
                                begenmeSayisi.text = ""+ snapshot.childrenCount.toString()+" beğeni"
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
            }


            if (anlikGonderi.userID.equals(mauth)){
                postMenu.visibility=View.GONE
            }
        }

        private fun begeniKontrolu(anlikGonderi: KullaniciKampanya) {

            val mRef = FirebaseDatabase.getInstance().reference
            val userID = FirebaseAuth.getInstance().currentUser!!.uid
            mRef.child("kampanya").child(anlikGonderi.userID!!).child(anlikGonderi.postID!!).child("begeniler").addValueEventListener(object : ValueEventListener {

                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.value !=null){

                        begenmeSayisi.visibility=View.VISIBLE
                        begenmeSayisi.text = ""+ snapshot.childrenCount.toString()+" beğeni"

                    }else {
                        begenmeSayisi.visibility=View.GONE
                    }

                    if (snapshot.hasChild(userID)) {
                        gonderiBegen.setImageResource(R.drawable.baseline_favorite_red_24)
                    } else {
                        gonderiBegen.setImageResource(R.drawable.ic_baseline_favorite)

                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }

        private fun yorumlarFragmentiniBaslat(anlikGonderi: KullaniciKampanya) {

           val action= HomeFragmentDirections.actionHomeFragmentToCommentFragment(
               anlikGonderi.userID!!,
               true,
               anlikGonderi.postID!!,
               anlikGonderi.postURL!!,
               null
           )
            Navigation.findNavController(itemView).navigate(action)

        }



        private fun yorumlariGoster(anlikGonderi: KullaniciKampanya) {
            val mref=FirebaseDatabase.getInstance().reference

            mref.child("kampanya").child(anlikGonderi.userID!!).child(anlikGonderi.postID!!).child("yorumlar").addListenerForSingleValueEvent(object :ValueEventListener{
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    val yorumSayisi = snapshot.childrenCount.toInt()
                    if (yorumSayisi >= 1) {
                        yorumlariGoster.visibility = View.VISIBLE
                        yorumlariGoster.text = "$yorumSayisi yorum"
                    } else {
                        yorumlariGoster.visibility = View.GONE
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val viewHolder = LayoutInflater.from(context).inflate(R.layout.recycler_row, parent, false)

        return ViewHolder(viewHolder,tekGonderi)
    }
    override fun getItemCount(): Int {
        return tumKampanyalar.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tumKampanyalar[position])

    }
    @SuppressLint("NotifyDataSetChanged")
    fun kampanyalariGuncelle(yeniKampanyaListesi:List<KullaniciKampanya>){
        tumKampanyalar.clear()
        tumKampanyalar.addAll(yeniKampanyaListesi)
        notifyDataSetChanged()
    }
}