package com.example.paylasimmvvm.view.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.R
import com.example.paylasimmvvm.adapter.HomeFragmentRecyclerAdapter
import com.example.paylasimmvvm.adapter.IsletmeListRecyclerAdapter
import com.example.paylasimmvvm.adapter.MesajlarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentIsletmeListBinding
import com.example.paylasimmvvm.model.KullaniciKampanya
import com.example.paylasimmvvm.viewmodel.BadgeViewModel
import com.example.paylasimmvvm.viewmodel.kampanyalarViewModel
import java.util.*


class IsletmeListFragment : Fragment() {
    private lateinit var binding: FragmentIsletmeListBinding
    private lateinit var kampanyalarViewModeli: kampanyalarViewModel
    private lateinit var recyclerviewadapter: IsletmeListRecyclerAdapter
    private var tumGonderiler= ArrayList<KullaniciKampanya>()

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