package com.example.paylasimmvvm.adapter

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.databinding.RecyclerRowMenuBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.Menuler
import com.github.chrisbanes.photoview.PhotoView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class MenulerGridAdapter( val tumMenuler: ArrayList<Menuler>) : BaseAdapter() {






    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {


        val view = convertView ?: LayoutInflater.from(parent?.context).inflate(R.layout.recycler_row_menu, parent, false)
        val dataItem = tumMenuler[position]

        val binding= RecyclerRowMenuBinding.bind(view)


        Picasso.get().load(dataItem.menuler).into(binding.imgMenu);



     binding.imgMenu.setOnClickListener {

         val builder = AlertDialog.Builder(parent!!.context)

         val photoView = PhotoView(parent.context)
         photoView.adjustViewBounds = true
         Picasso.get().load(dataItem.menuler).into(photoView)
         Log.e("gelendata","")


         builder.setView(photoView)

         val mref = FirebaseDatabase.getInstance().reference
         mref.child("users").child("isletmeler").addValueEventListener(object : ValueEventListener {
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot!!.getValue() != null) {
                     for (user in snapshot!!.children) {
                         val okunanKullanici = user.getValue(KullaniciBilgileri::class.java)!!
                         if (okunanKullanici!!.user_id.equals(FirebaseAuth.getInstance().currentUser!!.uid)) {
                             val silinicekMenu=dataItem.menuler

                             builder.setNeutralButton("MENÜ SİLİNSİN Mİ?"){dialog, _ ->

                             }
                             builder.setPositiveButton("VAZGEÇ") { dialog, _ ->
                                 dialog.dismiss()
                             }
                             builder.setNegativeButton("SİL"){dialog,_->

                                 mref.child("menuler").child(FirebaseAuth.getInstance().currentUser!!.uid).orderByChild("menuler").equalTo(silinicekMenu).addListenerForSingleValueEvent(object :ValueEventListener{
                                     override fun onDataChange(snapshot: DataSnapshot) {
                                         for (ds in snapshot.children) {
                                            var kley=ds.key
                                             mref.child("menuler").child(FirebaseAuth.getInstance().currentUser!!.uid).child(kley!!).removeValue()

                                             Toast.makeText(parent.context,"Menü silindi",Toast.LENGTH_LONG).show()
                                         }
                                     }
                                     override fun onCancelled(error: DatabaseError) {
                                     }
                                 })
                                 notifyDataSetChanged()

                                 dialog.dismiss()
                             }
                         }
                     }
                     val alertDialog = builder.create()
                     alertDialog.setCanceledOnTouchOutside(true)

                     photoView.setOnClickListener {
                         alertDialog.dismiss()
                     }
                     alertDialog.show()
                 }
             }

             override fun onCancelled(error: DatabaseError) {
             }
         })
     }




        return view
    }

    override fun getItem(position: Int): Any {
        return tumMenuler[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return tumMenuler.size
    }

    fun menuleriGuncelle(yeniMenuListesi:List<Menuler>){
        tumMenuler.clear()
        tumMenuler.addAll(yeniMenuListesi)
        notifyDataSetChanged()



    }
}