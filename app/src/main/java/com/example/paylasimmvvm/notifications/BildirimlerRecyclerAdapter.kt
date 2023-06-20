package com.example.paylasimmvvm.notifications

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowBildirimlerBinding
import com.example.paylasimmvvm.util.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class BildirimlerRecyclerAdapter(private val tumBildirimler: ArrayList<BildirimModel>) :
    RecyclerView.Adapter<BildirimlerRecyclerAdapter.mViewHolder>() {

    var ILK_GOSTERIM=10
    private var onAllItemsDeletedListener: (() -> Unit)? = null


   inner class mViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        private val binding = RecyclerRowBildirimlerBinding.bind(itemView)
        private val gonderiBegenildi = binding.contentTitle
        private val yorumYapildi = binding.contentTitle
        private val begenenPP = binding.profilPP
        private val kampanya = binding.gonderiImageView




        fun setdata(anlikBildirim: BildirimModel) {

            if (anlikBildirim.bildirim_tur == 1) {
            kullanicininBilgileriBegen(anlikBildirim.bildirim_yapan_id, anlikBildirim.gonderi_id, anlikBildirim.time!!)
            } else if (anlikBildirim.bildirim_tur == 2) {
             kullanicininBilgileriYorum(anlikBildirim.bildirim_yapan_id, anlikBildirim.gonderi_id, anlikBildirim.time!!,anlikBildirim.yorum!!)
            }
            else if (anlikBildirim.bildirim_tur == 3) {
             kullanicininBilgileriYorumBegeni(anlikBildirim.bildirim_yapan_id, anlikBildirim.time!!,anlikBildirim.yorum!!)
            }
            else if (anlikBildirim.bildirim_tur == 4) {
            kullanicininBilgileriYorumIsletme(anlikBildirim.bildirim_yapan_id, anlikBildirim.time!!,anlikBildirim.yorum!!)
            }

            if (anlikBildirim.goruldu==false){
                itemView.setBackgroundResource(R.color.divider_color)
            }else{
                itemView.background = null

            }

            binding.gonderiImageView.setOnClickListener {
              navigateClick(it,anlikBildirim)
            }
            binding.contentTitle.setOnClickListener {
                navigateClick(it,anlikBildirim)
            }
            binding.contentText.setOnClickListener {
                navigateClick(it,anlikBildirim)
            }
            binding.profilPP.setOnClickListener {
                if (anlikBildirim.bildirim_yapan_id!=FirebaseAuth.getInstance().currentUser!!.uid){
                    val actipn=
                        BildirimlerFragmentDirections.actionBildirimlerFragmentToUserProfilFragment(
                            anlikBildirim.bildirim_yapan_id!!
                        )
                    Navigation.findNavController(it).navigate(actipn)
                }
            }
            binding.optionsImageView.setOnClickListener {
                val popupMenu = androidx.appcompat.widget.PopupMenu(it.context, it)
                popupMenu.inflate(R.menu.menu_delete)
                popupMenu.setForceShowIcon(false)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.menu_delete -> {
                            val silinicekBildirim = anlikBildirim.bildirim_id

                            FirebaseDatabase.getInstance().reference.child("bildirimler").child(FirebaseAuth.getInstance().currentUser!!.uid)
                                .child(silinicekBildirim!!)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        snapshot.ref.removeValue()
                                        val position = bindingAdapterPosition
                                        if (position != RecyclerView.NO_POSITION) {
                                            val bildirim = tumBildirimler[position]
                                            tumBildirimler.remove(bildirim)
                                            notifyItemRemoved(position)
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })
                            true
                        }
                        R.id.menu_deleteAll->{
                            val alertDialog = AlertDialog.Builder(itemView.context)
                                .setTitle("Bildirimleri Sil")
                                .setMessage("Tüm bildirimler silinsin mi?")
                                .setPositiveButton("Evet") { _, _ ->

                                    FirebaseDatabase.getInstance().reference.child("bildirimler").child(FirebaseAuth.getInstance().currentUser!!.uid)

                                        .addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                snapshot.ref.removeValue()
                                                tumBildirimler.clear()
                                                notifyDataSetChanged()

                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                            }
                                        })
                                }
                                .setNegativeButton("Hayır", null)
                                .create()

                            alertDialog.show()




                            true
                        }
                        else -> false
                    }

                }
                if (tumBildirimler.isEmpty()) {
                    onAllItemsDeletedListener?.invoke()
                }

                popupMenu.gravity = Gravity.CENTER
                popupMenu.show()
            }

            }


        private fun navigateClick(view:View,anlikBildirim: BildirimModel){
            Log.e("bildirimfragment",""+anlikBildirim.bildirim_yapan_id)
            when (anlikBildirim.bildirim_tur) {

                1 -> {
                    handleYorumlarSnapshot(anlikBildirim.bildirim_yapan_id, anlikBildirim.gonderi_id) { yorumlarVarMi ->
                        val action = if (yorumlarVarMi) {
                            BildirimlerFragmentDirections.actionBildirimlerFragmentToGonderiDetayFragment(
                                anlikBildirim.gonderi_id!!,
                                anlikBildirim.bildirim_yapan_id!!,
                                null,
                                true
                            )
                        } else {

                            BildirimlerFragmentDirections.actionBildirimlerFragmentToGonderiDetayFragment(
                                anlikBildirim.gonderi_id!!,
                                anlikBildirim.bildirim_yapan_id!!,
                                null,
                                false
                            )
                        }
                        Navigation.findNavController(view).navigate(action)
                    }
                }
                4 -> {
                    val action =
                        BildirimlerFragmentDirections.actionBildirimlerFragmentToCommentFragment(
                            FirebaseAuth.getInstance().currentUser!!.uid,
                            false,
                            "",
                            "",
                            anlikBildirim.yorum_key
                        )
                    Navigation.findNavController(view).navigate(action)
                }

                3->{    if (anlikBildirim.gonderi_id!=null&& anlikBildirim.gonderi_id!!.isNotEmpty()){


                    val action =
                        BildirimlerFragmentDirections.actionBildirimlerFragmentToGonderiDetayFragment(
                            anlikBildirim.gonderi_id!!,
                            anlikBildirim.gonderi_sahibi_id!!,
                            anlikBildirim.yorum_key,
                            true
                        )
                    Navigation.findNavController(view).navigate(action)
                }else{


                    val action =
                        BildirimlerFragmentDirections.actionBildirimlerFragmentToCommentFragment(
                            anlikBildirim.gonderi_sahibi_id!!,
                            false,
                            "",
                            "",
                            anlikBildirim.yorum_key
                        )
                    Navigation.findNavController(view).navigate(action)
                }

                }
                else -> {

                    if (anlikBildirim.gonderi_id!=null&& anlikBildirim.gonderi_id!!.isNotEmpty()){


                        val action =
                            BildirimlerFragmentDirections.actionBildirimlerFragmentToGonderiDetayFragment(
                                anlikBildirim.gonderi_id!!,
                                anlikBildirim.bildirim_yapan_id!!,
                                anlikBildirim.yorum_key,
                                true
                            )
                        Navigation.findNavController(view).navigate(action)
                    }else{


                        val action =
                            BildirimlerFragmentDirections.actionBildirimlerFragmentToCommentFragment(
                                FirebaseAuth.getInstance().currentUser!!.uid,
                                false,
                                "",
                                "",
                                anlikBildirim.yorum_key
                            )
                        Navigation.findNavController(view).navigate(action)
                    }

                }
            }
        }


        private fun kullanicininBilgileriYorumIsletme(userId: String?,  time: Long,yorum:String?) {
            binding.gonderiImageView.visibility=View.INVISIBLE
            handleUserSnapshot(userId, time) { userName, profilePicture ->
                val message = " işletmeniz hakkında bir yorum yaptı."

                val spannableString = SpannableString("$userName$message")
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD), 0,
                    userName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                yorumYapildi.text = spannableString
                binding.bildirimZamaniTextView.text= TimeAgo.getTimeAgoForComments(time)
                val maxChararecters = 100
                val yorumKarakter = if (yorum?.length!! > maxChararecters) yorum.substring(0, maxChararecters) + "..." else yorum
                binding.contentText.text=yorumKarakter
                if (profilePicture.isNotEmpty()){
                    Picasso.get().load(profilePicture).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(begenenPP)

                }else{
                    Picasso.get().load(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(begenenPP)

                }
            }

        }

        private fun kullanicininBilgileriYorumBegeni(user_id: String?, bildirimZamani: Long,yorum: String?) {
            binding.gonderiImageView.visibility=View.INVISIBLE
            handleUserSnapshot(user_id, bildirimZamani) { userName, profilePicture ->
                val message = " yorumunu beğendi."

                val spannableString = SpannableString("$userName$message")
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD), 0,
                    userName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                yorumYapildi.text = spannableString
                binding.bildirimZamaniTextView.text= TimeAgo.getTimeAgoForComments(bildirimZamani)
                val MAX_CHARACTERS = 100
                val yorumKarakter = if (yorum?.length!! > MAX_CHARACTERS) yorum.substring(0, MAX_CHARACTERS) + "..." else yorum
                binding.contentText.text=yorumKarakter
                if (profilePicture.isNotEmpty()){
                    Picasso.get().load(profilePicture).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(begenenPP)

                }else{
                    Picasso.get().load(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(begenenPP)

                }
            }

        }
        private fun kullanicininBilgileriYorum(user_id: String?, gonderi_id: String?, bildirimZamani: Long,yorum: String?) {
            handleUserSnapshot(user_id, bildirimZamani) { userName, profilePicture ->
                val maxChararecters = 100
                val yorumKarakter = if (yorum?.length!! > maxChararecters) yorum.substring(0, maxChararecters) + "..." else yorum
                val message = " gönderine yorum yaptı."

                val spannableString = SpannableString("$userName$message")
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD), 0,
                    userName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                yorumYapildi.text = spannableString

                binding.bildirimZamaniTextView.text= TimeAgo.getTimeAgoForComments(bildirimZamani)
                binding.contentText.text=yorumKarakter

                if (profilePicture.isNotEmpty()){
                    Picasso.get().load(profilePicture).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(begenenPP)

                }else{
                    Picasso.get().load(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).into(begenenPP)

                }
            }
            handleKampanyaSnapshot( gonderi_id) { fileUrl ->
                kampanya.visibility = if (fileUrl.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                Picasso.get().load(fileUrl).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(kampanya)
            }
        }

        private fun kullanicininBilgileriBegen(user_id: String?, gonderi_id: String?, bildirimZamani: Long) {

            handleUserSnapshot(user_id, bildirimZamani) { userName, profilePicture ->
                val message = " gönderini beğendi."

                val spannableString = SpannableString("$userName$message")
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD), 0,
                    userName.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                gonderiBegenildi.text =spannableString
                binding.bildirimZamaniTextView.text= TimeAgo.getTimeAgoForComments(bildirimZamani)
                if (profilePicture.isNotEmpty()){
                    Picasso.get().load(profilePicture).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)

                }else{
                    Picasso.get().load(R.drawable.ic_baseline_person).placeholder(R.drawable.ic_baseline_person).error(R.drawable.ic_baseline_person).into(begenenPP)

                }
            }
            handleKampanyaSnapshot( gonderi_id) { fileUrl ->
                kampanya.visibility = if (fileUrl.isNotEmpty()) View.VISIBLE else View.INVISIBLE
                Picasso.get().load(fileUrl).into(kampanya)
            }
        }

        private fun handleUserSnapshot(user_id: String?, bildirim20Zamani: Long, onSuccess: (String, String) -> Unit) {
            val userRef = FirebaseDatabase.getInstance().reference.child("users").child("kullanicilar").child(user_id!!)
            val isletmeRef = FirebaseDatabase.getInstance().reference.child("users").child("isletmeler").child(user_id!!)
            isletmeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userName = snapshot.child("user_name").value.toString()
                        val profilePicture = snapshot.child("user_detail").child("profile_picture").value.toString()

                        onSuccess.invoke(userName, profilePicture)

                    }else{
                        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            @SuppressLint("SetTextI18n")
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val userName = snapshot.child("user_name").value.toString()
                                    val profilePicture = snapshot.child("user_detail").child("profile_picture").value.toString()

                                    onSuccess.invoke(userName, profilePicture)

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
        }

        private fun handleKampanyaSnapshot( gonderi_id: String?, onSuccess: (String) -> Unit) {
            val kampanyaRef = FirebaseDatabase.getInstance().reference.child("kampanya").child(FirebaseAuth.getInstance().currentUser!!.uid).child(gonderi_id!!)
            kampanyaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        val fileUrl = snapshot.child("file_url").value.toString()


                        onSuccess.invoke(fileUrl)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
        private fun handleYorumlarSnapshot(user_id: String?, gonderi_id: String?, onSuccess: (Boolean) -> Unit) {
            val kampanyaRef = FirebaseDatabase.getInstance().reference.child("kampanya").child(user_id!!).child(gonderi_id!!)
            kampanyaRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val yorumlarDugumu = snapshot.child("yorumlar")

                    val yorumlarVarMi = yorumlarDugumu.exists()

                    onSuccess.invoke(yorumlarVarMi)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_bildirimler, parent, false)

        return mViewHolder(view)
    }

    override fun onBindViewHolder(holder: mViewHolder, position: Int) {
        holder.setdata(tumBildirimler[position])
    }

    override fun getItemCount(): Int {
        return if (tumBildirimler.size < ILK_GOSTERIM) {
            tumBildirimler.size
        } else {
            ILK_GOSTERIM
        }

    }




    @SuppressLint("NotifyDataSetChanged")
    fun bildirimleriGuncelle(yeniBildirimListesi: List<BildirimModel>) {

        tumBildirimler.clear()
        tumBildirimler.addAll(yeniBildirimListesi)
        notifyDataSetChanged()
    }
    fun setOnAllItemsDeletedListener(listener: (() -> Unit)?) {
        onAllItemsDeletedListener = listener
    }
}