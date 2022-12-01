package com.example.paylasimmvvm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paylasimmvvm.model.KullaniciKampanya

class kampanyalarViewModel: ViewModel() {

    val kampanyalar=MutableLiveData<List<KullaniciKampanya>>()
    val kampanyaYok=MutableLiveData<Boolean>()
    val yukleniyor=MutableLiveData<Boolean>()

    fun refreshKampanyalar(){
        var kampanya=KullaniciKampanya("1234560","murat","https://www.evrensel.net/haber/280904/birakin-yuvayi-kuslar-yapsin","","selam","",""
        ,12321321,12.0,1321.0)
        kampanyalar.value= listOf(kampanya)
        kampanyaYok.value=false
        yukleniyor.value=false
    }
}