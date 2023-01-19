package com.example.paylasimmvvm.view.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.adapter.IsletmeListRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentIsletmeListBinding
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.util.Bildirimler.mref
import com.example.paylasimmvvm.viewmodel.kampanyalarViewModel
import com.google.firebase.auth.FirebaseAuth


class IsletmeListFragment : Fragment() {
    private lateinit var binding: FragmentIsletmeListBinding
    private lateinit var kampanyalarViewModeli: kampanyalarViewModel
    private lateinit var recyclerviewadapter: IsletmeListRecyclerAdapter
    private var tumGonderiler= ArrayList<KullaniciKampanya>()

    lateinit var locationManager: LocationManager
    lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentIsletmeListBinding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        locationManager= (requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager)!!
        locationListener=object :LocationListener{
            override fun onLocationChanged(p0: Location) {


                var konum=HashMap<String,Any>()
                konum.put("latitude",p0.latitude)
                konum.put("longitude",p0.longitude)
                konum.put("konumkullaniciId",FirebaseAuth.getInstance().currentUser!!.uid)

                mref.child("konumlar").child("kullanici_konum").child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(FirebaseAuth.getInstance().currentUser!!.uid).setValue(konum)

            }

            override fun onProviderDisabled(provider: String) {
            }

            override fun onProviderEnabled(provider: String) {
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            }

        }

        if(ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,1f,locationListener)
        }



        kampanyalarViewModeli= ViewModelProvider(this)[kampanyalarViewModel::class.java]
        kampanyalarViewModeli.getIsletmeList()


        observeliveData()

        val layoutManager= LinearLayoutManager(activity)
        binding.recyclerIsletmeList.layoutManager=layoutManager
        recyclerviewadapter= IsletmeListRecyclerAdapter(requireActivity(),tumGonderiler)
        binding.recyclerIsletmeList.adapter=recyclerviewadapter

    }

    private fun observeliveData() {

        kampanyalarViewModeli.kampanyalar.observe(viewLifecycleOwner) { kampanyalar ->
            kampanyalar.let {

                binding.recyclerIsletmeList.visibility = View.VISIBLE

                recyclerviewadapter.kampanyalariGuncelle(kampanyalar)
            }

        }

        kampanyalarViewModeli.kampanyaYok.observe(viewLifecycleOwner) { kampanyaYok ->
            kampanyaYok.let {


            }

        }
        kampanyalarViewModeli.yukleniyor.observe(viewLifecycleOwner) { yukleniyor ->
            yukleniyor.let {


            }

        }
    }



}