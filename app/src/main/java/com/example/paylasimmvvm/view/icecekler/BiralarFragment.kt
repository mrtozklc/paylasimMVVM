package com.example.paylasimmvvm.view.icecekler

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.paylasimmvvm.adapter.BiralarRecyclerAdapter
import com.example.paylasimmvvm.databinding.FragmentBiralarBinding
import com.example.paylasimmvvm.model.BiralarModel
import com.example.paylasimmvvm.viewmodel.BiralarViewModel

class BiralarFragment : Fragment() {
    private lateinit var binding:FragmentBiralarBinding

    private lateinit var recyclerAdapter: BiralarRecyclerAdapter
    private lateinit var biralarViewModeli: BiralarViewModel
    private var tumBiralar=ArrayList<BiralarModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= FragmentBiralarBinding.inflate(layoutInflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        biralarViewModeli= ViewModelProvider(this)[BiralarViewModel::class.java]
        biralarViewModeli.refreshData()


        observeLiveData()

        val layoutManager= LinearLayoutManager(activity)
        binding.biralarRecycler.layoutManager=layoutManager
        recyclerAdapter= BiralarRecyclerAdapter(tumBiralar)
        binding.biralarRecycler.adapter=recyclerAdapter

    }

    private fun observeLiveData(){

        biralarViewModeli.biralar.observe(viewLifecycleOwner) { biralar ->
            biralar.let {

                binding.biralarRecycler.visibility = View.VISIBLE
                if (biralar != null) {
                    recyclerAdapter.kampanyalariGuncelle(biralar)


                }

            }

        }



    }
}