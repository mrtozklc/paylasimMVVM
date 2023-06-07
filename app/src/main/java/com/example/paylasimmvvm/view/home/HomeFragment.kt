package com.example.paylasimmvvm.view.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.HomeFragmentRecyclerAdapter
import com.example.paylasimmvvm.adapter.SearchviewRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentHomeBinding
import com.example.paylasimmvvm.model.KullaniciBilgileri
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.setBadge
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
import com.example.paylasimmvvm.viewmodel.kampanyalarViewModel
import com.firebase.ui.database.SnapshotParser
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.util.*


class HomeFragment : Fragment() {
    lateinit var binding:FragmentHomeBinding
    private lateinit var auth : FirebaseAuth
    //  lateinit var mauthLis: FirebaseAuth.AuthStateListener
    lateinit var mref: DatabaseReference
    private lateinit var kampanyalarViewModeli:kampanyalarViewModel
    private lateinit var badgeViewModeli: BadgeViewModel
    private lateinit var recyclerviewadapter:HomeFragmentRecyclerAdapter
    private var tumGonderiler=ArrayList<KullaniciKampanya>()
    private var sayfaBasiGonderiler=ArrayList<KullaniciKampanya>()
    private val SAYFA_BASI_GONDERI=10
    private var sayfaSayisi=1
    var sayfaninSonunaGelindi = false




    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding= FragmentHomeBinding.inflate(layoutInflater,container,false)
        val view =binding.root
        auth= Firebase.auth
        mref = FirebaseDatabase.getInstance().reference



        //  setupAuthLis()



        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        kampanyalarViewModeli= ViewModelProvider(this)[kampanyalarViewModel::class.java]
        kampanyalarViewModeli.refreshKampanyalar()

        badgeViewModeli= ViewModelProvider(this)[BadgeViewModel::class.java]
        badgeViewModeli.refreshMessageBadge()


        observeLiveDataBadge()

        observeliveData()
        setUpRecyclerview()


        binding.refreshMainId.setOnRefreshListener {
            binding.progressBar8.visibility = View.VISIBLE
            binding.recyclerAnaSayfa.visibility = View.GONE
            binding.kampanyaYok.visibility = View.GONE

            binding.recyclerAnaSayfa.visibility = View.GONE
            tumGonderiler.clear()
            sayfaBasiGonderiler.clear()
            sayfaninSonunaGelindi = false


            kampanyalarViewModeli.refreshKampanyalar()

            binding.refreshMainId.isRefreshing = false
        }


        binding.searchView2.setOnQueryTextFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                binding.recyclerAnaSayfa.visibility=View.GONE
                binding.recyclerSearchHistory.visibility=View.VISIBLE
                showSearchHistory()
            }
        }

        binding.searchView2.setOnCloseListener(SearchView.OnCloseListener {
            binding.recyclerAnaSayfa.visibility=View.VISIBLE
            binding.recyclerSearchHistory.visibility=View.GONE

            false
        })
        binding.searchView2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                queryDatabase(query)

                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isEmpty()) {
                   showSearchHistory()
                } else {
                    queryDatabase(newText)
                }
                return true
            }
        })

    }
    private fun queryDatabase(query: String) {
        val ref = FirebaseDatabase.getInstance().reference.child("users")

        val kullanicilarQueryRef = ref.child("kullanicilar")
            .orderByChild("user_name")
            .startAt(query)
            .endAt(query + "\uf8ff")

        val isletmelerQueryRef = ref.child("isletmeler")
            .orderByChild("user_name")
            .startAt(query)
            .endAt(query + "\uf8ff")


        val kullanicilarQueryRef2 = ref.child("kullanicilar")
            .orderByChild("adi_soyadi")
            .startAt(query)
            .endAt(query + "\uf8ff")

        val isletmelerQueryRef2 = ref.child("isletmeler")
            .orderByChild("adi_soyadi")
            .startAt(query)
            .endAt(query + "\uf8ff")

        val parser = SnapshotParser<KullaniciBilgileri> { dataSnapshot ->
            val kullaniciBilgileri = dataSnapshot.getValue(KullaniciBilgileri::class.java)!!
            kullaniciBilgileri
        }

        val resultList = arrayListOf<KullaniciBilgileri>()
        val queryCompleteListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val kullaniciBilgileri = parser.parseSnapshot(data)

                    val existingUser = resultList.find { it.user_id == kullaniciBilgileri.user_id }
                    if (existingUser != null) {
                        existingUser.user_name = kullaniciBilgileri.user_name
                        existingUser.adi_soyadi = kullaniciBilgileri.adi_soyadi
                    } else {
                        resultList.add(kullaniciBilgileri)
                    }
                }
                val layoutManager = LinearLayoutManager(activity)
                binding.recyclerSearchHistory.layoutManager = layoutManager
                val searchResultAdapter = SearchviewRecyclerAdapter(resultList,true)
                binding.recyclerSearchHistory.adapter = searchResultAdapter
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        kullanicilarQueryRef.addListenerForSingleValueEvent(queryCompleteListener)
        isletmelerQueryRef.addListenerForSingleValueEvent(queryCompleteListener)
        kullanicilarQueryRef2.addListenerForSingleValueEvent(queryCompleteListener)
        isletmelerQueryRef2.addListenerForSingleValueEvent(queryCompleteListener)
    }

    private fun showSearchHistory() {
        val searchHistory = arrayListOf<KullaniciBilgileri>()

        FirebaseDatabase.getInstance().reference.child("SearchHistory").child(FirebaseAuth.getInstance().currentUser!!.uid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach { childSnapshot ->
                    val userId = childSnapshot.key

                    if (userId != null) {

                        searchHistory.clear()

                        FirebaseDatabase.getInstance().reference.child("users").child("kullanicilar").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val userData = userSnapshot.getValue(KullaniciBilgileri::class.java)
                                if (userData != null) {
                                    searchHistory.add(userData)


                                    binding.recyclerSearchHistory.adapter?.notifyDataSetChanged()


                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "User data fetch canceled: $error")
                            }
                        })

                        FirebaseDatabase.getInstance().reference.child("users").child("isletmeler").child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(userSnapshot: DataSnapshot) {
                                val userData = userSnapshot.getValue(KullaniciBilgileri::class.java)
                                if (userData != null) {
                                    searchHistory.add(userData)

                                    binding.recyclerSearchHistory.adapter?.notifyDataSetChanged()


                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "User data fetch canceled: $error")
                            }
                        })
                    }

                }
                if (!snapshot.exists()) {
                    searchHistory.clear()
                    binding.recyclerSearchHistory.adapter?.notifyDataSetChanged()
                    return
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Search history fetch canceled: $error")
            }
        })

        val layoutManager = LinearLayoutManager(activity)
        binding.recyclerSearchHistory.layoutManager = layoutManager
        val searchHistoryAdapter = SearchviewRecyclerAdapter(searchHistory,false)
        binding.recyclerSearchHistory.adapter = searchHistoryAdapter
    }




    override fun onResume() {
        observeliveData()
        if (binding.recyclerSearchHistory.visibility == View.VISIBLE) {
            binding.recyclerAnaSayfa.visibility = View.GONE;
        } else {
            binding.recyclerAnaSayfa.visibility = View.VISIBLE;
        }
        super.onResume()
    }
    private fun observeliveData(){


        kampanyalarViewModeli.kampanyalar.observe(viewLifecycleOwner) { kampanyalar ->
            kampanyalar.let {

                Collections.sort(kampanyalar
                ) { p0, p1 ->
                    if (p0!!.postYuklenmeTarih!! > p1!!.postYuklenmeTarih!!) {
                        -1
                    } else 1
                }

                if (tumGonderiler.size >= SAYFA_BASI_GONDERI) {


                    for (i in 0 until SAYFA_BASI_GONDERI) {
                        sayfaBasiGonderiler.add(tumGonderiler[i])

                    }
                } else {
                    for (i in 0 until tumGonderiler.size) {
                        sayfaBasiGonderiler.add(tumGonderiler[i])


                    }
                }

                binding.recyclerAnaSayfa.visibility = View.VISIBLE

                recyclerviewadapter.kampanyalariGuncelle(kampanyalar)
            }

        }

        kampanyalarViewModeli.kampanyaYok.observe(viewLifecycleOwner) { kampanyaYok ->
            kampanyaYok.let {
                if (it) {
                    binding.kampanyaYok.visibility = View.VISIBLE
                    binding.recyclerAnaSayfa.visibility = View.GONE

                } else {
                    binding.kampanyaYok.visibility = View.GONE

                }

            }

        }

        kampanyalarViewModeli.yukleniyor.observe(viewLifecycleOwner) { yukleniyor ->
            yukleniyor.let {
                if (it) {
                    binding.progressBar8.visibility = View.VISIBLE
                    binding.kampanyaYok.visibility = View.GONE
                    binding.recyclerAnaSayfa.visibility = View.GONE

                } else {
                    binding.progressBar8.visibility = View.GONE

                }

            }

        }
    }

    private  fun observeLiveDataBadge(){
        badgeViewModeli.badgeLiveMessage.observe(viewLifecycleOwner) {gorulmeyenMesajSayisi ->

            gorulmeyenMesajSayisi.let {



                val navView:BottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView)
                if (gorulmeyenMesajSayisi != null) {
                    navView.setBadge(R.id.mesajlarFragment, gorulmeyenMesajSayisi.size)


                }

            }

        }

    }


    private fun setUpRecyclerview(){

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerAnaSayfa.layoutManager=layoutManager
        recyclerviewadapter= HomeFragmentRecyclerAdapter(requireActivity(),sayfaBasiGonderiler,false)
        binding.recyclerAnaSayfa.adapter=recyclerviewadapter


        binding.recyclerAnaSayfa.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(
                recyclerView: RecyclerView,
                dx: Int,
                dy: Int
            ) {
                super.onScrolled(recyclerView, dx, dy)


                if (dy > 0 && layoutManager.findLastVisibleItemPosition() ==  binding.recyclerAnaSayfa.adapter!!.itemCount - 1) {

                    if (!sayfaninSonunaGelindi)
                        listeyeYeniElemanlariEkle()
                }


            }
        })

    }
    @SuppressLint("NotifyDataSetChanged")
    private fun listeyeYeniElemanlariEkle() {

        val yeniGetirilecekElemanlarinAltSiniri = sayfaSayisi * SAYFA_BASI_GONDERI
        val yeniGetirilecekElemanlarinUstSiniri = (sayfaSayisi +1) * SAYFA_BASI_GONDERI - 1
        for (i in yeniGetirilecekElemanlarinAltSiniri..yeniGetirilecekElemanlarinUstSiniri) {
            if (sayfaBasiGonderiler.size <= tumGonderiler.size - 1) {
                sayfaBasiGonderiler.add(tumGonderiler[i])
                binding.recyclerAnaSayfa.adapter!!.notifyDataSetChanged()
            } else {
                sayfaninSonunaGelindi = true
                sayfaSayisi = 0
                break
            }

        }

        sayfaSayisi++


    }


    override fun onStart() {
        super.onStart()
        Log.e("hata","homedasın")
        (activity as AppCompatActivity).supportActionBar?.hide()

        // auth.addAuthStateListener(mauthLis)
    }

    override fun onStop() {
        super.onStop()
        // auth.removeAuthStateListener(mauthLis)
    }




}