package com.example.paylasimmvvm.massage

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowMesajlarBinding
import com.example.paylasimmvvm.util.TimeAgo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MesajlarRecyclerAdapter(private var tumMesajlar: ArrayList<Mesajlar>) :
    RecyclerView.Adapter<MesajlarRecyclerAdapter.ViewHolder>() {

    private var selectedItems: HashSet<Int> = HashSet()
    private lateinit var recyclerView: RecyclerView
    private var onItemLongClickListener: ((Int) -> Unit)? = null
    private var onAllItemsDeletedListener: (() -> Unit)? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        val binding = RecyclerRowMesajlarBinding.bind(itemView)
        private var sonAtilanmesaj = binding.sonMesajId
        private var gonderilmeZamani = binding.zamanOnceId
        var userpp = binding.imgKonusmalarpp
        var userName = binding.tvUsername
        private var okunduBilgisi = binding.okunduBilgisi

        var mref = FirebaseDatabase.getInstance().reference

        @SuppressLint("SetTextI18n")
        fun setData(oankiKonusmalar: Mesajlar, position: Int) {
            var sonAtilanmesajText = oankiKonusmalar.son_mesaj.toString()

            if (sonAtilanmesajText.isNotEmpty()) {
                sonAtilanmesajText = sonAtilanmesajText.replace("\n", " ")
                sonAtilanmesajText = sonAtilanmesajText.trim()

                if (sonAtilanmesajText.length > 40) {
                    sonAtilanmesaj.text = sonAtilanmesajText.substring(0, 40) + "..."
                } else {
                    sonAtilanmesaj.text = sonAtilanmesajText
                }
            } else {
                sonAtilanmesajText = ""
                sonAtilanmesaj.text = sonAtilanmesajText
            }

            gonderilmeZamani.text =
                "- ${TimeAgo.getTimeAgoForComments(oankiKonusmalar.gonderilmeZamani!!.toLong())}"

            if (oankiKonusmalar.goruldu == false) {
                okunduBilgisi.visibility = View.VISIBLE
            } else {
                okunduBilgisi.visibility = View.INVISIBLE
            }
           var isNavigationInProgress = false
            binding.tumLayout.setOnClickListener {
                if (!isNavigationInProgress) {
                    isNavigationInProgress = true

                    if (selectedItems.isEmpty()) {
                        val action =
                            MesajlarFragmentDirections.actionMesajlarFragmentToChatFragment(
                                oankiKonusmalar.user_id!!
                            )

                        FirebaseDatabase.getInstance().reference
                            .child("konusmalar")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child(oankiKonusmalar.user_id.toString())
                            .child("goruldu").setValue(true)
                            .addOnCompleteListener {

                                val navOptions = NavOptions.Builder()
                                    .setPopUpTo(R.id.chatFragment, true)
                                    .build()
                                Navigation.findNavController(binding.tumLayout)
                                    .navigate(action, navOptions)
                                isNavigationInProgress = false
                            }
                    } else {
                        toggleSelection(position)
                        isNavigationInProgress = true
                    }
                }
            }

            binding.tumLayout.setOnLongClickListener(View.OnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClickListener?.invoke(position)
                }
                if (selectedItems.isEmpty()) {
                    selectItem(position)
                } else {
                    toggleSelection(position)
                }
                return@OnLongClickListener true
            })

            if (selectedItems.contains(position)) {
                itemView.setBackgroundResource(R.color.divider_color)
            } else {
                itemView.background = null
            }
            userpp.setImageDrawable(null)
            konusulanKisininBilgilerinigetir(oankiKonusmalar.user_id.toString())
        }

        private fun konusulanKisininBilgilerinigetir(userID: String) {
            val mref = FirebaseDatabase.getInstance().reference

            mref.child("users").child("kullanicilar").child(userID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null) {
                            userName.text = snapshot.child("user_name").value.toString()
                            if (snapshot.child("user_detail").child("profile_picture").value.toString()
                                    .isNotEmpty()
                            ) {
                                Picasso.get()
                                    .load(snapshot.child("user_detail").child("profile_picture").value.toString())
                                    .error(R.drawable.ic_baseline_person)
                                    .placeholder(R.drawable.ic_baseline_person)
                                    .into(userpp)
                            } else {
                                Picasso.get().load(R.drawable.ic_baseline_person)
                                    .placeholder(R.drawable.ic_baseline_person)
                                    .error(R.drawable.ic_baseline_person)
                                    .into(userpp)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })

            mref.child("users").child("isletmeler").child(userID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.value != null) {
                            userName.text = snapshot.child("user_name").value.toString()
                            if (snapshot.child("user_detail").child("profile_picture").value.toString()
                                    .isNotEmpty()
                            ) {
                                Picasso.get()
                                    .load(snapshot.child("user_detail").child("profile_picture").value.toString())
                                    .error(R.drawable.ic_baseline_person)
                                    .placeholder(R.drawable.ic_baseline_person)
                                    .into(userpp)
                            } else {
                                Picasso.get().load(R.drawable.ic_baseline_person)
                                    .error(R.drawable.ic_baseline_person)
                                    .placeholder(R.drawable.ic_baseline_person)
                                    .into(userpp)
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }

        private fun selectItem(position: Int) {
            selectedItems.add(position)
            notifyItemChanged(position)
        }

        private fun deselectItem(position: Int) {
            selectedItems.remove(position)

            notifyItemChanged(position)

        }

        private fun toggleSelection(position: Int) {
            if (selectedItems.contains(position)) {
                deselectItem(position)
            } else {
                selectItem(position)
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_row_mesajlar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setData(tumMesajlar[position], position)
    }

    override fun getItemCount(): Int {
        return tumMesajlar.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun mesajlariGuncelle(yeniMesajListesi: List<Mesajlar>) {
        tumMesajlar.clear()
        tumMesajlar.addAll(yeniMesajListesi)
        notifyDataSetChanged()
    }

    fun getSelectedItems(): HashSet<Int> {
        return selectedItems
    }
    fun selectAll(){
        selectedItems.clear()
        for (i in 0 until tumMesajlar.size) {
            selectedItems.add(i)
            notifyItemChanged(i)
        }
    }
    fun deSelectAll(){
        selectedItems.clear()
        notifyDataSetChanged()
    }
    fun deleteSelectedItems() {
        val selectedIds = ArrayList<String>()
        for (position in selectedItems) {
            val mesaj = tumMesajlar[position]
            val mesajId = mesaj.user_id
            selectedIds.add(mesajId!!)
        }

        val mesajlarRef = FirebaseDatabase.getInstance().reference.child("mesajlar").child(FirebaseAuth.getInstance().currentUser!!.uid)
        for (mesajId in selectedIds) {
            mesajlarRef.child(mesajId).removeValue()
        }

        val konusmalarRef = FirebaseDatabase.getInstance().reference.child("konusmalar").child(FirebaseAuth.getInstance().currentUser!!.uid)
        for (konusmaId in selectedIds) {
            konusmalarRef.child(konusmaId).removeValue()
        }
        tumMesajlar.removeAll { mesaj -> selectedIds.contains(mesaj.user_id) }
        selectedItems.clear()
        notifyDataSetChanged()

        if (tumMesajlar.isEmpty()) {
            onAllItemsDeletedListener?.invoke()
        }
    }

    fun setOnAllItemsDeletedListener(listener: (() -> Unit)?) {
        onAllItemsDeletedListener = listener
    }
    fun setOnItemLongClickListener(listener: (Int) -> Unit) {
        onItemLongClickListener = listener
    }

}